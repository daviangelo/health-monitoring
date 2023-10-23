package com.lessa.healthmonitoring.dto;

import com.lessa.healthmonitoring.domain.HeartRateRecord;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HeartRateRecordDto {

    private Long id;
    private Instant recordDate;
    private Integer beatsPerMinute;

    public static HeartRateRecordDto fromDomain(HeartRateRecord domain) {
        return new HeartRateRecordDto(domain.id(), domain.recordDate(), domain.beatsPerMinute());
    }

    public HeartRateRecord toDomain() {
        return new HeartRateRecord(this.id, null,  this.recordDate, this.beatsPerMinute);
    }
}
