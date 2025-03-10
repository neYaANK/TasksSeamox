/*
 * UserController.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.user;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @GetMapping
    public ResponseEntity getUsers(@RequestParam(name = "page") int page,
                                   @RequestParam(name = "pageSize") int pageSize) {
        if(page < 1 || pageSize < 1) {
            return ResponseEntity.status(400).body("Values can't be less than 1");
        }
        // page-1 so it is more obvious to a user (page 1 is a first page and not second)
        Pageable pageable = PageRequest.of(page-1, pageSize,
                Sort.by("id").ascending());
        List<User> res = userService.findAll(pageable);

        return ResponseEntity.ok(res);
    }

}
