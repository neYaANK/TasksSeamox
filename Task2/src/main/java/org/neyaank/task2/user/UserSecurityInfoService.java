/*
 * UserSecurityInfoService.java
 * Copyright (c) 2025 Artem Nersesian
 */

package org.neyaank.task2.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.neyaank.task2.errorhandling.UserNotVerifiedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSecurityInfoService implements UserDetailsService {
    private final UserRepository repository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Security: loadUserByUsername: Loading user by email {}", username);
        User user = repository.findByEmail(username).orElseThrow(()->
                        new UsernameNotFoundException("Not found user with email " + username));
        if (!user.isVerified()){
            throw new UserNotVerifiedException("Can't login user " + user.getEmail()
                    + " because user is not verified");
        }
        return new UserSecurityInfo(user);
    }

}
