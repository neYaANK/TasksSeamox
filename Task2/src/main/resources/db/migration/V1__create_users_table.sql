/*
 * V1__create_users_table.sql
 * Copyright (c) 2025 Artem Nersesian
 */

create table users(
    user_id integer not null primary key GENERATED ALWAYS AS IDENTITY,
    email varchar(255) not null,
    password varchar(255) not null,
    first_name varchar(255) not null,
    last_name varchar(255) not null,
    birth_date date not null,
    phone_number varchar(50),
    verified boolean not null
)