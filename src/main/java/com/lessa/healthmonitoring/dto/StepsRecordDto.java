package com.lessa.healthmonitoring.dto;

import com.lessa.healthmonitoring.domain.StepsRecord;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StepsRecordDto {

    private Long id;
    private Instant recordDate;
    private Long numberOfSteps;

    public static StepsRecordDto fromDomain(StepsRecord domain) {
        return new StepsRecordDto(domain.id(), domain.recordDate(), domain.numberOfSteps());
    }

    public StepsRecord toDomain() {
        return new StepsRecord(this.id, null,  this.recordDate, this.numberOfSteps);
    }
}
