package com.lessa.healthmonitoring.service.impl;

import com.lessa.healthmonitoring.domain.TemperatureRecord;
import com.lessa.healthmonitoring.domain.TemperatureScale;
import com.lessa.healthmonitoring.persistence.repository.TemperatureRepository;
import com.lessa.healthmonitoring.persistence.repository.UserRepository;
import com.lessa.healthmonitoring.service.TemperatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TemperatureServiceImpl implements TemperatureService {

    private final TemperatureRepository temperatureRepository;
    private final UserRepository userRepository;

    @Override
    public TemperatureRecord recordTemperature(Long userId, TemperatureRecord temperatureRecord) {
        return null;
    }

    @Override
    public List<TemperatureRecord> getTemperatureRecordsPerDay(Long userId, LocalDate date, TemperatureScale scale) {
        return null;
    }

    @Override
    public boolean delete(Long temperatureRecordId) {
        return false;
    }
}
