/*
 * UserAddress.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@ToString
@Getter
@Setter
@NoArgsConstructor
@Table(name = "user_addresses")
public class UserAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Integer id;
    private String country;
    private String zip;
    private String city;
    private String address;
    private String details;
    @Column(name = "is_primary")
    private boolean primary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;
}
