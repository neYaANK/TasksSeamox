/*
 * UserAddressMapper.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserAddressMapper {
    UserAddressMapper INSTANCE = Mappers.getMapper(UserAddressMapper.class );

    UserAddressDTO toDTO(UserAddress user);
    @Mapping(target="user", ignore = true)
    UserAddress toAddress(UserAddressDTO userDTO);
}
