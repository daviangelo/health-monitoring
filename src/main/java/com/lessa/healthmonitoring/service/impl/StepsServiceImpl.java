package com.lessa.healthmonitoring.service.impl;

import com.lessa.healthmonitoring.domain.StepsRecord;
import com.lessa.healthmonitoring.persistence.entity.StepsRecordEntity;
import com.lessa.healthmonitoring.persistence.repository.StepsRepository;
import com.lessa.healthmonitoring.persistence.repository.UserRepository;
import com.lessa.healthmonitoring.service.StepsService;
import com.lessa.healthmonitoring.service.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StepsServiceImpl implements StepsService {

    private final StepsRepository stepsRepository;
    private final UserRepository userRepository;

    @Override
    public StepsRecord recordSteps(Long userId, StepsRecord stepsRecord) {
        var maybeUser = userRepository.findById(userId);

        if (maybeUser.isEmpty()) {
            throw new UserNotFoundException("User with id " + userId + " not found to record steps.");
        } else {
            var userEntity = maybeUser.get();
            var stepsRecordEntity = StepsRecordEntity.toEntity(stepsRecord);
            stepsRecordEntity.setUser(userEntity);
            return stepsRepository.save(stepsRecordEntity).toDomain();
        }
    }

    @Override
    public List<StepsRecord> getStepsRecordsPerDay(Long userId, LocalDate date) {
        var startDate = date.atStartOfDay(ZoneOffset.UTC).toInstant();
        var endDate = startDate.plus(1, ChronoUnit.DAYS).minus(1, ChronoUnit.SECONDS);

        var stepsRecordsEntity = stepsRepository.findAllByUserIdAndRecordDateBetween(userId, startDate, endDate);

        return stepsRecordsEntity.stream().map(StepsRecordEntity::toDomain).toList();
    }

    @Override
    public Long getNumberOfStepsPerDay(Long userId, LocalDate date) {
        return getStepsRecordsPerDay(userId, date).stream().mapToLong(StepsRecord::numberOfSteps).sum();
    }

    @Override
    public boolean delete(Long stepsRecordId) {
        var maybeTemperatureRecord = stepsRepository.findById(stepsRecordId);
        if (maybeTemperatureRecord.isPresent()) {
            stepsRepository.deleteById(stepsRecordId);
            return true;
        }
        return false;
    }
}
