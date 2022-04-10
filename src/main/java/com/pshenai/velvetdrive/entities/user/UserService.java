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
    public void addUser(DriveUser driveUser) {
        if (!userRepository.existsByEmail(driveUser.getEmail())){
            userRepository.save(driveUser);
        }
    }

    @Transactional
    public void updateCredentials(Long id, String newPassHash, String newName){
        DriveUser user = userRepository.getById(id);
        if(!newPassHash.equals("")){
            user.setPassHash(newPassHash);
        }
        if(!newName.equals("") && !newName.equals(user.getFullName())){
            user.setFullName(newName);
        }
        userRepository.save(user);
    }

    @Transactional
    public DriveUser findByEmail(String email){
        return userRepository.findByEmail(email);
    }
}
