package com.lessa.healthmonitoring.service.impl;

import com.lessa.healthmonitoring.domain.TemperatureRecord;
import com.lessa.healthmonitoring.domain.TemperatureScale;
import com.lessa.healthmonitoring.persistence.entity.TemperatureRecordEntity;
import com.lessa.healthmonitoring.persistence.repository.TemperatureRepository;
import com.lessa.healthmonitoring.persistence.repository.UserRepository;
import com.lessa.healthmonitoring.service.TemperatureService;
import com.lessa.healthmonitoring.service.exception.UserNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;


@Service
@RequiredArgsConstructor
public class TemperatureServiceImpl implements TemperatureService {

    private final TemperatureRepository temperatureRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public TemperatureRecord recordTemperature(Long userId, TemperatureRecord temperatureRecord) {
        var maybeUser = userRepository.findById(userId);

        if (maybeUser.isEmpty()) {
            throw new UserNotFoundException("User with id " + userId + " not found to record a temperature.");
        } else {
            var userEntity = maybeUser.get();
            var temperatureRecordEntity = TemperatureRecordEntity.toEntity(temperatureRecord);
            temperatureRecordEntity.setUser(userEntity);
            return temperatureRepository.save(temperatureRecordEntity).toDomain();
        }
    }

    @Override
    public List<TemperatureRecord> getTemperatureRecordsPerDay(Long userId, LocalDate date, TemperatureScale scale) {

        var startDate = date.atStartOfDay(ZoneOffset.UTC).toInstant();
        var endDate = startDate.plus(1, ChronoUnit.DAYS).minus(1, ChronoUnit.SECONDS);

        var temperatureRecordsEntity = temperatureRepository.findAllByUserIdAndRecordDateBetween(userId, startDate, endDate);

        if (!temperatureRecordsEntity.isEmpty()) {
            var temperatureRecords = temperatureRecordsEntity.stream().map(TemperatureRecordEntity::toDomain).toList();

            temperatureRecords.forEach(temperatureRecord -> {
                if (!scale.equals(temperatureRecord.getScale())) {
                    var temperatureConverted = convertTemperature(temperatureRecord.getScale(), scale, temperatureRecord.getTemperature());
                    temperatureRecord.setTemperature(temperatureConverted);
                    temperatureRecord.setScale(scale);
                }
            });

            return temperatureRecords;
        }
        return Collections.emptyList();
    }

    @Transactional
    @Override
    public boolean delete(Long temperatureRecordId) {
        var maybeTemperatureRecord = temperatureRepository.findById(temperatureRecordId);
        if (maybeTemperatureRecord.isPresent()) {
            temperatureRepository.deleteById(temperatureRecordId);
            return true;
        }
        return false;
    }

    private Double convertTemperature(TemperatureScale originalScale, TemperatureScale scaleToConvert, Double temperature) {
        switch (originalScale) {
            case FAHRENHEIT -> {
                return convertFromFahrenheit(scaleToConvert, temperature);
            }
            case CELSIUS -> {
                return convertFromCelsius(scaleToConvert, temperature);
            }
            case KELVIN -> {
                return convertFromKelvin(scaleToConvert, temperature);
            }
        }
        return temperature;
    }

    private Double convertFromFahrenheit(TemperatureScale scaleToConvert, Double temperature) {
        switch (scaleToConvert) {
            case CELSIUS -> {
                return convertFahrenheitToCelsius(temperature);
            }
            case KELVIN -> {
                return convertFahrenheitToKelvin(temperature);
            }
            default -> throw new IllegalStateException("Unexpected value: " + scaleToConvert);
        }
    }

    private Double convertFromCelsius(TemperatureScale scaleToConvert, Double temperature) {
        switch (scaleToConvert) {
            case FAHRENHEIT -> {
                return convertCelsiusToFahrenheit(temperature);
            }
            case KELVIN -> {
                return convertCelsiusToKelvin(temperature);
            }
            default -> throw new IllegalStateException("Unexpected value: " + scaleToConvert);
        }
    }

    private Double convertFromKelvin(TemperatureScale scaleToConvert, Double temperature) {

        switch (scaleToConvert) {
            case FAHRENHEIT -> {
                return convertKelvinToFahrenheit(temperature);
            }
            case CELSIUS -> {
                return convertKelvinToCelsius(temperature);
            }
            default -> throw new IllegalStateException("Unexpected value: " + scaleToConvert);
        }
    }

    private Double convertCelsiusToFahrenheit(Double temperature) {
        return (temperature * 9.0 / 5.0) + 32.0;
    }

    private Double convertCelsiusToKelvin(Double temperature) {
        return temperature + 273.15;
    }

    private Double convertFahrenheitToCelsius(Double temperature) {
        return (temperature - 32.0) * 5.0 / 9.0;
    }

    private Double convertKelvinToCelsius(Double temperature) {
        return temperature - 273.15;
    }

    private Double convertFahrenheitToKelvin(Double temperature) {
        return (temperature - 32.0) * 5.0 / 9.0 + 273.15;
    }

    private Double convertKelvinToFahrenheit(Double temperature) {
        return (temperature - 273.15) * 9.0 / 5.0 + 32.0;
    }
}
