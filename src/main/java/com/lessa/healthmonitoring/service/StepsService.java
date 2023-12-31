package com.lessa.healthmonitoring.service;

import com.lessa.healthmonitoring.domain.StepsRecord;

import java.time.LocalDate;
import java.util.List;

public interface StepsService {

    StepsRecord recordSteps(Long userId, StepsRecord stepsRecord);

    List<StepsRecord> getStepsRecordsPerDay(Long userId, LocalDate date);

    Long getNumberOfStepsPerDay(Long userId, LocalDate date);

    boolean delete(Long stepsRecordId);

}
