package com.lessa.healthmonitoring.service;

import com.lessa.healthmonitoring.domain.HeartRateRecord;

import java.time.LocalDate;
import java.util.List;

public interface HeartRateService {

    HeartRateRecord recordHeartRate(HeartRateRecord heartRateRecord);

    List<HeartRateRecord> getHeartRateRecordsPerDay(Long userId, LocalDate date);

    void delete(Long heartRateRecordId);

}
