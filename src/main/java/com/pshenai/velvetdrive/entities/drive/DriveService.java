package com.pshenai.velvetdrive.entities.drive;

import com.pshenai.velvetdrive.entities.user.DriveUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DriveService {

    private final DriveRepository driveRepository;

    public DriveService(DriveRepository driveRepository) {
        this.driveRepository = driveRepository;
    }

    @Transactional
    public void updateDrivePlan(DrivePlan drivePlan, Drive drive) {
        DrivePlan oldPlan = drive.getDrivePlan();
        drive.setDrivePlan(drivePlan);
        drive.setSpaceLeft(drivePlan.getSpace() - (oldPlan.getSpace() - drive.getSpaceLeft()));
    }
}
