package com.lessa.healthmonitoring.service;

import com.lessa.healthmonitoring.domain.TemperatureRecord;

import java.time.LocalDate;
import java.util.List;

public interface TemperatureService {

    TemperatureRecord recordTemperature(TemperatureRecord temperatureRecord);

    List<TemperatureRecord> getTemperatureRecordsPerDay(Long userId, LocalDate date);

    void delete(TemperatureRecord temperatureRecord);

}
