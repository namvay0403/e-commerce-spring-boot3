package com.nam.e_commerce.service;

import com.nam.e_commerce.model.CustomUserDetail;
import com.nam.e_commerce.model.User;
import com.nam.e_commerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = Optional.ofNullable(userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found")));
        return user.map(CustomUserDetail::new).get();
    }
}
