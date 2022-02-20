package com.pshenai.velvetdrive.entities.user;

import com.pshenai.velvetdrive.entities.drive.Drive;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class DriveUser {

    @Id
    @GeneratedValue
    private Long id;

    private String email;
    private String passHash;
    @Enumerated(EnumType.STRING)
    private UserRole role;
    private String name;
    private String surname;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "driveId")
    private Drive drive;

    public DriveUser(String email, String passHash, UserRole role, String name, String surname) {
        this.email = email;
        this.passHash = passHash;
        this.role = role;
        this.name = name;
        this.surname = surname;
    }
}
