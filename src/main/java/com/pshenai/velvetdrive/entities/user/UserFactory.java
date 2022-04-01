package com.pshenai.velvetdrive.entities.user;

import com.pshenai.velvetdrive.entities.drive.Drive;
import com.pshenai.velvetdrive.entities.folder.Folder;
import com.pshenai.velvetdrive.entities.storage.StorageManager;
import com.pshenai.velvetdrive.entities.drive.DrivePlan;
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

    public DriveUser createUser(String email, String passHash,
                           UserRole role, String fullName){
        DriveUser user = new DriveUser(email, passHash, role, fullName);
        Drive drive = new Drive(user, DrivePlan.VELVET);
        drive.setSpaceLeft(drive.getDrivePlan().getSpace());
        Folder defFolder = new Folder("Default",drive,"images/backs/default.jpg");
        drive.getFolderList().add(defFolder);
        storageManager.setDriveStorage(drive);
        user.setDrive(drive);

        userService.addUser(user);
        return user;
    }
}
