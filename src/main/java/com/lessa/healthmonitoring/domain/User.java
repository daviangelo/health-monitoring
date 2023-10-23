package com.lessa.healthmonitoring.domain;

import java.io.Serializable;
import java.time.LocalDate;

public record User (Long id, String name, LocalDate dateOfBirth) implements Serializable {
}
