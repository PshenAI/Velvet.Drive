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
    public List<Storage> getAllStorages(){
        return storageRepository.findAll();
    }

    @Transactional
    public void updateStorageSpace(String bucketName, Long spaceTaken) {
        Storage storage = storageRepository.findByBucketName(bucketName);
        if (storage == null)
            return;

        storage.setSpaceTaken(spaceTaken);

        storageRepository.save(storage);
    }
}
