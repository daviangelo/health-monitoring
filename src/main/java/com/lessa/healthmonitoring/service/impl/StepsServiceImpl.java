package com.lessa.healthmonitoring.service.impl;

import com.lessa.healthmonitoring.domain.StepsRecord;
import com.lessa.healthmonitoring.persistence.repository.StepsRepository;
import com.lessa.healthmonitoring.persistence.repository.UserRepository;
import com.lessa.healthmonitoring.service.StepsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StepsServiceImpl implements StepsService {

    private final StepsRepository stepsRepository;
    private final UserRepository userRepository;

    @Override
    public StepsRecord recordSteps(Long userId, StepsRecord stepsRecord) {
        return null;
    }

    @Override
    public List<StepsRecord> getStepsRecordsPerDay(Long userId, LocalDate date) {
        return null;
    }

    @Override
    public Long getNumberOfStepsPerDay(Long userId, LocalDate date) {
        return null;
    }

    @Override
    public void delete(Long stepsRecordId) {

    }
}
