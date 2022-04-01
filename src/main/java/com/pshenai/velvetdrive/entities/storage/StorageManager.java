package com.pshenai.velvetdrive.entities.storage;

import com.pshenai.velvetdrive.entities.drive.Drive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Component
public class StorageManager {

    private StorageService storageService;
    private List<Storage> storages;

    @Autowired
    public StorageManager(StorageService storageService) {
        this.storageService = storageService;
    }

    public void setDriveStorage(Drive drive) throws NullPointerException{
        storages = storageService.getAllStorages();
        Storage res;
        Optional<Storage> optionalStorage = storages.stream().filter(s -> {
            if(s.getAvailSpace() < drive.getSpaceLeft()){
                return false;
            }
            return true;
        }).findFirst();
        if(optionalStorage.isEmpty()){
            throw new NullPointerException("Unable to find available storage!");
        }
        res = optionalStorage.get();
        File drivePath = new File(res.getDirPath() + drive.getDriveUser().getEmail());
        if(!drivePath.mkdir()){
            System.out.println("Directory problem.");
        }
        drive.setStorage(res);
        drive.setDrivePath(drivePath.getPath());
        res.setAvailSpace(res.getAvailSpace() - drive.getSpaceLeft());
        storageService.updateStorage(res.getDirPath(), res.getMaxSpace(), res.getAvailSpace());
    }

    @Override
    public String toString(){
        return storages.toString();
    }

}
