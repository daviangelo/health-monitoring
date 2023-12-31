package com.lessa.healthmonitoring.domain;

import java.io.Serializable;
import java.time.Instant;

public record StepsRecord (Long id, User user, Instant recordDate, Long numberOfSteps) implements Serializable {

}
