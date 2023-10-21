package com.lessa.healthmonitoring.domain;

import java.time.Instant;

public record HeartRateRecord(Long id, User user, Instant date, Integer beatsPerMinute) {
}
