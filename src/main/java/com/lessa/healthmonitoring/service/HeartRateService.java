package com.lessa.healthmonitoring.service;

import com.lessa.healthmonitoring.domain.HeartRateRecord;

import java.time.LocalDate;
import java.util.List;

public interface HeartRateService {

    HeartRateRecord recordHeartRate(Long userId, HeartRateRecord heartRateRecord);

    List<HeartRateRecord> getHeartRateRecordsPerDay(Long userId, LocalDate date);

    boolean delete(Long heartRateRecordId);

}
