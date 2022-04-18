package com.pshenai.velvetdrive.entities.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        DriveUser user = userService.findByEmail(email);
        if(user == null)
            throw new UsernameNotFoundException("User with such an email: " + email + " doesn't exist");

        List<GrantedAuthority> roles = List.of(
                new SimpleGrantedAuthority(user.getRole().toString()));

        return new User(user.getEmail(), user.getPassHash(), roles);
    }
}
