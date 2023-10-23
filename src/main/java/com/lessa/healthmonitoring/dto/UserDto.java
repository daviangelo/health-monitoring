package com.lessa.healthmonitoring.dto;

import com.lessa.healthmonitoring.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
public final class UserDto {

    private  Long id;
    private  String name;
    private  LocalDate dateOfBirth;

    public static UserDto fromDomain(User domain) {
        return new UserDto(domain.id(), domain.name(), domain.dateOfBirth());
    }

    public User toDomain() {
        return new User(this.id, this.name, this.dateOfBirth);
    }

}
