package com.lessa.healthmonitoring.service;

import com.lessa.healthmonitoring.domain.StepsRecord;

import java.time.LocalDate;
import java.util.List;

public interface StepsService {

    StepsRecord recordSteps(StepsRecord stepsRecord);

    List<StepsRecord> getStepsRecordsPerDay(Long userId, LocalDate date);

    Long numberOfStepsPerDay(Long userId, LocalDate date);

    void delete(Long stepsRecordId);

}
