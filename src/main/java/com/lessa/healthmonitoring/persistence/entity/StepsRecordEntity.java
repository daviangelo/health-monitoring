package com.lessa.healthmonitoring.persistence.entity;

import com.lessa.healthmonitoring.domain.StepsRecord;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "steps_record")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@EqualsAndHashCode
public class StepsRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence-generator")
    @SequenceGenerator(name = "sequence-generator", sequenceName = "steps_record_id_seq", allocationSize = 1)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Instant recordDate;

    @NotNull
    private Long numberOfSteps;

    public static StepsRecordEntity toEntity(StepsRecord domain) {
        var entity = new StepsRecordEntity();

        entity.setId(domain.id());

        if (domain.user() != null) {
            entity.setUser(UserEntity.toEntity(domain.user()));
        }
        entity.setRecordDate(domain.recordDate());
        entity.setNumberOfSteps(domain.numberOfSteps());

        return entity;
    }

    public StepsRecord toDomain() {
        return new StepsRecord(this.getId(), this.getUser().toDomain(), this.getRecordDate(),
                this.getNumberOfSteps());
    }


}
