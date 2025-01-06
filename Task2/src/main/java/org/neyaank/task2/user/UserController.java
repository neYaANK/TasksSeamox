/*
 * UserController.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @PostMapping
    public ResponseEntity registerUser(@RequestBody UserDTO userDto) {
        User user = new User(userDto);
        //Not sure if I should create another UserDTO for
        //input that won't have id field or stick to this impl
        user.setId(null);
        User result = userService.registerUser(user);
        return ResponseEntity.ok(result);
    }
    @PutMapping("/{id}")
    public ResponseEntity updateUser(@PathVariable int id, @RequestBody UserDTO userDto) {
        User user = new User(userDto);
        user.setId(null);
        User result = userService.updateUser(id, user);
        return ResponseEntity.ok(result);
    }

}
