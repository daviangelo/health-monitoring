package com.lessa.healthmonitoring.persistence.entity;

import com.lessa.healthmonitoring.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "user")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@EqualsAndHashCode
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence-generator")
    @SequenceGenerator(name = "sequence-generator", sequenceName = "user_id_seq", allocationSize = 1)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    @Column(name = "date_of_birth", columnDefinition = "DATE")
    private LocalDate dateOfBirth;

    public static UserEntity toEntity(User domain) {
        var entity = new UserEntity();

        entity.setId(domain.id());
        entity.setName(domain.name());
        entity.setDateOfBirth(domain.dateOfBirth());

        return entity;
    }
    public User toDomain() {
        return new User(this.id, this.name, dateOfBirth);
    }




}
