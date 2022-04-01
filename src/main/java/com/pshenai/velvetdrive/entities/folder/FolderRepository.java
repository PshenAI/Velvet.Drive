package com.pshenai.velvetdrive.entities.folder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {

    Optional<Folder> findById(Long id);
    Boolean existsByName(String name);
}
