package com.pshenai.velvetdrive.entities.file;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    boolean existsByPath(String path);
    File findByPath(String path);
    void deleteByPath(String path);
}
