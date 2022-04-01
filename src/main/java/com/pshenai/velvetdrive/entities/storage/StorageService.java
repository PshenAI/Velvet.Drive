package com.pshenai.velvetdrive.entities.storage;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StorageService {

    private final StorageRepository storageRepository;

    public StorageService(StorageRepository storageRepository) {
        this.storageRepository = storageRepository;
    }

    @Transactional
    public void addStorage(Storage storage){
        storageRepository.save(storage);
    }

    @Transactional
    public void addStorages(List<Storage> storages){
        storages.forEach(a -> storageRepository.save(a));
    }

    @Transactional
    public List<Storage> getAllStorages(){
        List<Storage> result = storageRepository.findAll();
        return result;
    }

    @Transactional
    public void updateStorage(String dirPath, Long maxSpace, Long availSpace) {
        Storage storage = storageRepository.findByDirPath(dirPath);
        if (storage == null)
            return;

        storage.setDirPath(dirPath);
        storage.setAvailSpace(availSpace);
        storage.setMaxSpace(maxSpace);

        storageRepository.save(storage);
    }
}
