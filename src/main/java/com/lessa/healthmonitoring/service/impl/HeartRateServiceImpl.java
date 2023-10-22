package com.lessa.healthmonitoring.service.impl;

import com.lessa.healthmonitoring.domain.HeartRateRecord;
import com.lessa.healthmonitoring.persistence.entity.HeartRateRecordEntity;
import com.lessa.healthmonitoring.persistence.repository.HeartRateRepository;
import com.lessa.healthmonitoring.persistence.repository.UserRepository;
import com.lessa.healthmonitoring.service.HeartRateService;
import com.lessa.healthmonitoring.service.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HeartRateServiceImpl implements HeartRateService {

    private final HeartRateRepository heartRateRepository;
    private final UserRepository userRepository;

    @Override
    public HeartRateRecord recordHeartRate(Long userId, HeartRateRecord heartRateRecord) {
        var maybeUser = userRepository.findById(userId);

        if (maybeUser.isEmpty()) {
            throw new UserNotFoundException("User with id " + userId + " not found to record a heart rate.");
        } else {
            var userEntity = maybeUser.get();
            var heartRateRecordEntity = HeartRateRecordEntity.toEntity(heartRateRecord);
            heartRateRecordEntity.setUser(userEntity);
            return heartRateRepository.save(heartRateRecordEntity).toDomain();
        }
    }

    @Override
    public List<HeartRateRecord> getHeartRateRecordsPerDay(Long userId, LocalDate date) {
        var startDate = date.atStartOfDay(ZoneOffset.UTC).toInstant();
        var endDate = startDate.plus(1, ChronoUnit.DAYS).minus(1, ChronoUnit.SECONDS);

        var heartRateRecordsEntity = heartRateRepository.findAllByUserIdAndRecordDateBetween(userId, startDate, endDate);

        return heartRateRecordsEntity.stream().map(HeartRateRecordEntity::toDomain).toList();
    }

    @Override
    public boolean delete(Long heartRateRecordId) {
        var maybeTemperatureRecord = heartRateRepository.findById(heartRateRecordId);
        if (maybeTemperatureRecord.isPresent()) {
            heartRateRepository.deleteById(heartRateRecordId);
            return true;
        }
        return false;
    }
}
