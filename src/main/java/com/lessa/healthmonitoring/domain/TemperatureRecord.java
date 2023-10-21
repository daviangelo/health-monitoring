package com.lessa.healthmonitoring.domain;

import java.time.Instant;

public record TemperatureRecord(Long id, User user, Instant recordDate, Double temperature, TemperatureScale scale) {
}
