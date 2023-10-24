package com.lessa.healthmonitoring.dto;

import com.lessa.healthmonitoring.domain.TemperatureRecord;
import com.lessa.healthmonitoring.domain.TemperatureScale;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemperatureRecordDto {

    private Long id;

    @NotNull
    private Instant recordDate;

    @NotNull
    private Double temperature;

    @NotNull
    private TemperatureScale scale;

    public static TemperatureRecordDto fromDomain(TemperatureRecord domain) {
        return new TemperatureRecordDto(domain.getId(), domain.getRecordDate(), domain.getTemperature(), domain.getScale());
    }

    public TemperatureRecord toDomain() {
        return new TemperatureRecord(this.getId(), null,  this.getRecordDate(), this.getTemperature(), this.getScale());
    }

}
