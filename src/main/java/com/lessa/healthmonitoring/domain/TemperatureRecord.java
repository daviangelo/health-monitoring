package com.lessa.healthmonitoring.domain;

import lombok.*;

import java.io.Serializable;
import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
@Setter
public final class TemperatureRecord implements Serializable {

    private  Long id;
    private  User user;
    private  Instant recordDate;
    private Double temperature;
    private  TemperatureScale scale;

}
