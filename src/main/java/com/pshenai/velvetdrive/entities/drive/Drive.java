package com.pshenai.velvetdrive.entities.drive;

import com.pshenai.velvetdrive.entities.folder.Folder;
import com.pshenai.velvetdrive.entities.storage.Storage;
import com.pshenai.velvetdrive.entities.user.DriveUser;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private String drivePath;
    private DrivePlan drivePlan;
    private Double spaceLeft;
    @OneToMany(mappedBy = "drive", cascade = CascadeType.PERSIST)
    private List<Folder> folderList = new ArrayList<>();

    public Drive(DriveUser driveUser, DrivePlan drivePlan) {
        this.driveUser = driveUser;
        this.drivePlan = drivePlan;
    }

    public Boolean folderExistsByName(String folderName){
        return folderList.stream().anyMatch(a -> a.getName().equals(folderName));
    }

    public Folder getFolderByName(String folderName){
        Optional<Folder> folder = folderList.stream().filter(a -> a.getName().equals(folderName)).findFirst();
        if(folder.isPresent()){
            return folder.get();
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return "Drive{" +
                "id=" + id +
                ", driveUser=" + driveUser.getEmail() +
                ", storage=" + storage +
                ", drivePath=" + drivePath +
                ", spaceLeft=" + spaceLeft +
                '}';
    }
}
