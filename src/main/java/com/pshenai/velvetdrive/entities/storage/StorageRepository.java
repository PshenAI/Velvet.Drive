package com.pshenai.velvetdrive.entities.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StorageRepository extends JpaRepository<Storage, Long> {

    Storage findByBucketName(String dirPath);
}
