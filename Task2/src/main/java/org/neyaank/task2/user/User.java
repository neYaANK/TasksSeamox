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
import java.time.LocalDateTime;
import java.util.Set;

@ToString
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    //Strategy = Identity so the DB can generate IDs by itself
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
    private String verificationCode;
    private int failedAttempts = 0;
    private boolean isLocked = false;
    private LocalDateTime unlockTime;
    private LocalDateTime verificationStartTime;


    /**
     * Copy from another User for easier entity updating
     */
    public void copy(User source) {
        this.email = source.getEmail();
        this.password = source.getPassword();
        this.firstName = source.getFirstName();
        this.lastName = source.getLastName();
        this.birthDate = source.getBirthDate();
        this.phoneNumber = source.getPhoneNumber();
        this.verified = source.isVerified();
    }
    public void incrementFailed(){
        failedAttempts++;
    }
    public void resetFailed(){
        failedAttempts = 0;
    }

}
