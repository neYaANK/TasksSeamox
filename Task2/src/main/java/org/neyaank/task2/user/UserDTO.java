/*
 * UserDTO.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.user;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserDTO {
    private Integer id;
    @NotBlank
    @Size(min = 8, max = 50)
    @Pattern(regexp = "(?=.*[A-Z])(?=.*[!@#$&*])(?=.*[0-9])(?=.*[a-z]).{8,}",
            message = "Password must contain at least one upper and lower case symbol," +
                    " one number and one symbol and consist of at least 8 symbols.")
    private String password;
    @NotBlank
    @Size(min = 3, max = 200)
    @Pattern(regexp = "[\\w.]+@[\\w.]+", message = "Email must be valid")
    private String email;
    @NotBlank
    @Size(min = 1, max = 200)
    @Pattern(regexp = "[a-zA-Z]+", message = "First name must contain only letters.")
    private String firstName;
    @NotBlank
    @Size(min = 1, max = 200)
    @Pattern(regexp = "[a-zA-Z]+", message = "Last name must contain only letters.")
    private String lastName;
    @NotNull
    @DateTimeFormat(pattern = "dd-mm-yyyy")
    private LocalDate birthDate;
    @NotBlank
    @Pattern(regexp = "\\+?[0-9]{2,20}", message = "Phone number must contain only numbers")
    private String phoneNumber;
    private boolean verified;

    @AssertTrue(message = "Birthdate can't be in the future")
    public boolean isValidBirthDate(){
        return birthDate.isBefore(LocalDate.now());
    }

    public UserDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.birthDate = user.getBirthDate();
        this.phoneNumber = user.getPhoneNumber();
        this.verified = user.isVerified();
    }
}
