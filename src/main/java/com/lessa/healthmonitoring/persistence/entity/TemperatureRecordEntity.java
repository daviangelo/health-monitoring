package com.lessa.healthmonitoring.persistence.entity;

import com.lessa.healthmonitoring.domain.TemperatureRecord;
import com.lessa.healthmonitoring.domain.TemperatureScale;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "temperature_record")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@EqualsAndHashCode
public class TemperatureRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence-generator")
    @SequenceGenerator(name = "sequence-generator", sequenceName = "temperature_record_id_seq", allocationSize = 1)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Instant recordDate;

    @NotNull
    private Double temperature;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TemperatureScale scale;

    public static TemperatureRecordEntity toEntity(TemperatureRecord domain) {
        var entity = new TemperatureRecordEntity();

        entity.setId(domain.getId());
        if (domain.getUser() != null) {
            entity.setUser(UserEntity.toEntity(domain.getUser()));
        }
        entity.setRecordDate(domain.getRecordDate());
        entity.setTemperature(domain.getTemperature());
        entity.setScale(domain.getScale());

        return entity;
    }

    public TemperatureRecord toDomain() {
        return new TemperatureRecord(this.getId(), this.getUser().toDomain(), this.getRecordDate(),
                this.getTemperature(), this.getScale());
    }


}
