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

    @PostMapping
    public ResponseEntity registerUser(@Valid @RequestBody UserDTO userDto) {
        User user = new User(userDto);
        //Not sure if I should create another UserDTO for
        //input that won't have id field or stick to this impl
        user.setId(null);
        User userResult = userService.registerUser(user);
        UserDTO result = new UserDTO(userResult);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateUser(@PathVariable int id,
                                     @Valid @RequestBody UserDTO userDto) {
        User user = new User(userDto);
        user.setId(null);
        User userResult = userService.updateUser(id, user);
        UserDTO result = new UserDTO(userResult);
        return ResponseEntity.ok(result);
    }

}
