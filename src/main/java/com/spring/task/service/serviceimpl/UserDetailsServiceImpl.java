package com.spring.task.service.serviceimpl;

import com.spring.task.entity.User;
import com.spring.task.payload.response.CustomUserDetails;
import com.spring.task.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Entering in loadUserByUsername Method...");

        User user = userRepository.findByEmail(username).orElseThrow(()-> {
            logger.error("Username not found: " + username);
            return new UsernameNotFoundException("could not found user..!!");
        });
        logger.info("User Authenticated Successfully..!!!");

        return new CustomUserDetails(user);
    }
}