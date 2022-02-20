package com.pshenai.velvetdrive.entities.user;

import com.pshenai.velvetdrive.entities.drive.Drive;
import com.pshenai.velvetdrive.entities.storage.StorageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserFactory {

    private final StorageManager storageManager;
    private final UserService userService;

    @Autowired
    public UserFactory(UserService userService, StorageManager storageManager) {
        this.userService = userService;
        this.storageManager = storageManager;
    }

    public Boolean createUser(String email, String passHash,
                              UserRole role, String name, String surname){
        DriveUser user = new DriveUser(email, passHash, role, name, surname);
        Drive drive = new Drive(user, 20, 20);
        storageManager.setDriveStorage(drive);
        user.setDrive(drive);
        return userService.addUser(user);
    }
}
