/*
 * V1__create_users_table.sql
 * Copyright (c) 2025 Artem Nersesian
 */

create table users(
    user_id int generated always as identity not null primary key,
    email varchar(255) unique not null,
    password varchar(255) not null,
    first_name varchar(255) not null,
    last_name varchar(255) not null,
    birth_date date not null,
    phone_number varchar(50) not null,
    verified boolean not null,
    verification_code varchar(255),
    failed_attempts int not null,
    is_locked boolean not null,
    unlock_time timestamp without time zone
)