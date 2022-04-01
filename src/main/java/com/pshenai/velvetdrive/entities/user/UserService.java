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
                           UserRole role, String fullName) {
        if (userRepository.existsByEmail(email)){
            return false;
        }

        DriveUser user = new DriveUser(email, passHash, role, fullName);
        userRepository.save(user);

        return true;
    }

    @Transactional
    public boolean addUser(DriveUser driveUser) {
        if (userRepository.existsByEmail(driveUser.getEmail())){
            return false;
        }
        userRepository.save(driveUser);
        return true;
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
