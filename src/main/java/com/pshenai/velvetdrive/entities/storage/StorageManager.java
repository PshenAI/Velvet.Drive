package com.pshenai.velvetdrive.entities.storage;

import com.pshenai.velvetdrive.entities.drive.Drive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    public Boolean setDriveStorage(Drive drive) throws NullPointerException{
        storages = storageService.getAllStorages();
        Storage res;
        Optional<Storage> optionalStorage = storages.stream().filter(s -> {
            if(s.getAvailSpace() < drive.getSpaceMax()){
                return false;
            }
            return true;
        }).findFirst();
        if(optionalStorage.isEmpty()){
            throw new NullPointerException("Unable to find available storage!");
        }
        res = optionalStorage.get();
        drive.setStorage(res);
        res.setAvailSpace(res.getAvailSpace() - drive.getSpaceMax());
        storageService.updateStorage(res.getDirPath(), res.getMaxSpace(), res.getAvailSpace());
        System.out.println(res.getAvailSpace());
        return true;
    }

    @Override
    public String toString(){
        return storages.toString();
    }

}
