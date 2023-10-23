package com.lessa.healthmonitoring.domain;

import java.io.Serializable;
import java.time.Instant;

public record HeartRateRecord(Long id, User user, Instant recordDate, Integer beatsPerMinute) implements Serializable {
}
