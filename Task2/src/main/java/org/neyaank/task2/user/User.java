/*
 * User.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@ToString
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    //Strategy = Identity due to:
    //https://stackoverflow.com/questions/50659505/spring-boot-project-fails-to-run-because-of-schema-validation-missing-sequence
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer id;
    private String email;
    private String password;
    @Column(name="first_name")
    private String firstName;
    @Column(name="last_name")
    private String lastName;
    @Column(name="birth_date")
    private LocalDate birthDate;
    @Column(name="phone_number")
    private String phoneNumber;
    private boolean verified;


}
