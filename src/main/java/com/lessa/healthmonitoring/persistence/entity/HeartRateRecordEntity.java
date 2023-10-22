package com.lessa.healthmonitoring.persistence.entity;

import com.lessa.healthmonitoring.domain.HeartRateRecord;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "heart_rate_record")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@EqualsAndHashCode
public class HeartRateRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence-generator")
    @SequenceGenerator(name = "sequence-generator", sequenceName = "heart_rate_record_id_seq", allocationSize = 1)
    private Long id;


    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Instant recordDate;

    @NotNull
    private Integer beatsPerMinute;

    public static HeartRateRecordEntity toEntity(HeartRateRecord domain) {
        var entity = new HeartRateRecordEntity();

        entity.setId(domain.id());
        if (domain.user() != null) {
            entity.setUser(UserEntity.toEntity(domain.user()));
        }
        entity.setRecordDate(domain.recordDate());
        entity.setBeatsPerMinute(domain.beatsPerMinute());

        return entity;
    }

    public HeartRateRecord toDomain() {
        return new HeartRateRecord(this.id, this.user.toDomain(), this.recordDate, this.beatsPerMinute);
    }
}
