package com.lessa.healthmonitoring.domain;

import java.time.Instant;

public record StepsRecord (Long id, User user, Instant date, Long numberOfSteps) {

}
