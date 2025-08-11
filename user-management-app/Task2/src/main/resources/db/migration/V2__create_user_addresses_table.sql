/*
 * V2__create_user_addresses_table.sql
 * Copyright (c) 2025 Artem Nersesian
 */

create table user_addresses(
    address_id int generated always as identity not null primary key,
    country varchar(3) not null,
    zip varchar(20) not null,
    city varchar(100) not null,
    address varchar(255) not null,
    details varchar(255) not null,
    user_id int,
    is_primary boolean not null,
    CONSTRAINT fk_user FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE
)