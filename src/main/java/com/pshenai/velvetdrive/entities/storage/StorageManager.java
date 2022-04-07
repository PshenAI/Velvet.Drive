package com.pshenai.velvetdrive.entities.storage;

import com.pshenai.velvetdrive.entities.drive.Drive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class StorageManager {

    private final StorageService storageService;
    private List<Storage> storages;

    @Autowired
    public StorageManager(StorageService storageService) {
        this.storageService = storageService;
    }

    public void setDriveStorage(Drive drive) throws NullPointerException{
        storages = storageService.getAllStorages();
        Storage res;
        Optional<Storage> optionalStorage = storages.stream().filter(s -> {
            return (s.getSpaceMax() - s.getSpaceTaken()) >= 102400L;
        }).findFirst();
        if(optionalStorage.isEmpty()){
            throw new NullPointerException("Unable to find available storage!");
        }
        res = optionalStorage.get();
        drive.setStorage(res);
        res.setSpaceTaken(res.getSpaceTaken() + 102400L);
        storageService.updateStorageSpace(res.getBucketName(), res.getSpaceTaken());
    }

    @Override
    public String toString(){
        return storages.toString();
    }

}
