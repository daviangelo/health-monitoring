package com.lessa.healthmonitoring.service;

import com.lessa.healthmonitoring.domain.TemperatureRecord;
import com.lessa.healthmonitoring.domain.TemperatureScale;

import java.time.LocalDate;
import java.util.List;

public interface TemperatureService {

    TemperatureRecord recordTemperature(Long userId, TemperatureRecord temperatureRecord);

    List<TemperatureRecord> getTemperatureRecordsPerDay(Long userId, LocalDate date, TemperatureScale scale);

    boolean delete(Long temperatureRecordId);

}
