package com.lessa.healthmonitoring.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
public class User implements Serializable {

    private Long id;
    private String name;
    private Date dateOfBirth;
}
