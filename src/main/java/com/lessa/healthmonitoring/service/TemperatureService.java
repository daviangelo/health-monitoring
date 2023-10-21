package com.lessa.healthmonitoring.service;

import com.lessa.healthmonitoring.domain.TemperatureRecord;
import com.lessa.healthmonitoring.domain.TemperatureScale;

import java.time.LocalDate;
import java.util.List;

public interface TemperatureService {

    TemperatureRecord recordTemperature(TemperatureRecord temperatureRecord);

    List<TemperatureRecord> getTemperatureRecordsPerDay(Long userId, LocalDate date, TemperatureScale scale);

    void delete(TemperatureRecord temperatureRecord);

}
