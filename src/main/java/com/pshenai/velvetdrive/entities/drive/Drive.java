package com.pshenai.velvetdrive.entities.drive;

import com.pshenai.velvetdrive.entities.storage.Storage;
import com.pshenai.velvetdrive.entities.user.DriveUser;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "drives")
public class Drive {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(mappedBy = "drive")
    private DriveUser driveUser;
    @ManyToOne
    @JoinColumn(name = "storageId")
    private Storage storage;
    private Integer spaceMax;
    private Integer spaceLeft;

//    private List<Folder> folderList = new ArrayList<>;

    public Drive(DriveUser driveUser, Integer spaceMax, Integer spaceLeft) {
        this.driveUser = driveUser;
        this.spaceMax = spaceMax;
        this.spaceLeft = spaceLeft;
    }
}
