package com.lessa.healthmonitoring.domain;

import java.time.LocalDate;

public record User (Long id, String name, LocalDate dateOfBirth) {
}
