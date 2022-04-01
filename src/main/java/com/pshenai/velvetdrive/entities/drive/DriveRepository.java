package com.pshenai.velvetdrive.entities.drive;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriveRepository extends JpaRepository<Drive, Long> {
}
