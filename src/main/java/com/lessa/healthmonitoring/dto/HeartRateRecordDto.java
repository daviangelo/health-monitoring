package com.lessa.healthmonitoring.dto;

import com.lessa.healthmonitoring.domain.HeartRateRecord;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HeartRateRecordDto {

    private Long id;

    @NotNull
    private Instant recordDate;

    @NotNull
    @Size(min = 0, message="Beats per minute must have at least \\{{min}\\ }}")
    private Integer beatsPerMinute;

    public static HeartRateRecordDto fromDomain(HeartRateRecord domain) {
        return new HeartRateRecordDto(domain.id(), domain.recordDate(), domain.beatsPerMinute());
    }

    public HeartRateRecord toDomain() {
        return new HeartRateRecord(this.id, null,  this.recordDate, this.beatsPerMinute);
    }
}
