/*
 * UserAddressDTO.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.user;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Locale;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAddressDTO {
    private static Set<String> ISO_COUNTRIES =
            Locale.getISOCountries(Locale.IsoCountryCode.PART1_ALPHA3);
    private Integer id;
    private String country;
    @NotBlank
    @Size(min = 1, max = 20)
    private String zip;
    @Size(min = 1, max = 20)
    @Pattern(regexp = "[a-zA-Z]+", message = "Must contain only characters")
    private String city;
    @Size(min = 1, max = 100)
    @NotBlank
    private String address;
    @Size(max = 250)
    private String details;
    private boolean primary;
    @AssertTrue(message = "must be a valid ISO 3166 country")
    public boolean isISOCountry(){
        return ISO_COUNTRIES.contains(country);
    }
}
