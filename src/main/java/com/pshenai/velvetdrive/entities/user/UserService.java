package com.pshenai.velvetdrive.entities.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public boolean addUser(String email, String passHash,
                           UserRole role, String name,
                           String surname) {
        if (userRepository.existsByEmail(email))
            return false;

        DriveUser user = new DriveUser(email, passHash, role, name, surname);
        userRepository.save(user);

        return true;
    }

    @Transactional
    public boolean addUser(DriveUser driveUser) {
        userRepository.save(driveUser);
        return true;
    }

    @Transactional
    public DriveUser findByEmail(String email){
        return userRepository.findByEmail(email);
    }
}
