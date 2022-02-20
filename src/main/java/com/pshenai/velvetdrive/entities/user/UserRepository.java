package com.pshenai.velvetdrive.entities.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<DriveUser, Long> {

//    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false " +
//            "END FROM User u WHERE u.email = :email")
    boolean existsByEmail(String email);

//    @Query("SELECT u FROM User u WHERE u.email = :email")
    DriveUser findByEmail(String email);
}
