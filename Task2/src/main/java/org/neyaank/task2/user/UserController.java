/*
 * UserController.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    public ResponseEntity registerUser(@Valid @RequestBody UserDTO userDto) {
        User user = userMapper.userDTOToUser(userDto);
        user.setId(null);
        User userResult = userService.registerUser(user);
        UserDTO result = new UserDTO(userResult);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateUser(@PathVariable int id,
                                     @Valid @RequestBody UserDTO userDto) {
        User user = userMapper.userDTOToUser(userDto);
        user.setId(null);
        User userResult = userService.updateUser(id, user);
        UserDTO result = new UserDTO(userResult);
        return ResponseEntity.ok(result);
    }

}
