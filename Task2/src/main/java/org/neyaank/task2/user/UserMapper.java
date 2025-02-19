/*
 * UserMapper.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class );

    UserDTO userToUserDTO(User user);
    @Mapping(target = "verificationCode", ignore = true)
    User userDTOToUser(UserDTO userDTO);
}
