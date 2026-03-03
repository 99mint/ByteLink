package com.mint.bytelink.security;

import com.mint.bytelink.entity.User;
import com.mint.bytelink.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public CustomUserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        log.debug("Loading user details for username: {}", username);

        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found during authentication: {}", username);
                    return new UsernameNotFoundException("User Not found");
                });

        log.debug("User details loaded successfully for username: {}", username);

        return new CustomUserDetails(user);
    }
}
