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
        User userResult = userService.registerUser(user);
        UserDTO result = new UserDTO(userResult);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateUser(@PathVariable int id,
                                     @Valid @RequestBody UserDTO userDto) {
        User user = userMapper.userDTOToUser(userDto);
        User userResult = userService.updateUser(id, user);
        UserDTO result = new UserDTO(userResult);
        return ResponseEntity.ok(result);
    }
    @GetMapping("/{id}")
    public ResponseEntity getUser(@PathVariable int id) {
        User user = userService.findUserById(id);
        UserDTO result = userMapper.userToUserDTO(user);
        return ResponseEntity.ok(result);
    }

}
