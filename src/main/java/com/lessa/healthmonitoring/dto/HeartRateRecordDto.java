package com.lessa.healthmonitoring.dto;

import java.time.Instant;

public record HeartRateRecordDto(Long id, Instant recordDate, Integer beatsPerMinute) {
}
