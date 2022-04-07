package com.pshenai.velvetdrive.entities.storage;

import com.pshenai.velvetdrive.entities.drive.Drive;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "storages")
public class Storage {

    @Id
    @GeneratedValue
    private Long id;

    private String bucketName;
    private Long spaceMax;
    private Long spaceTaken;

    @OneToMany(mappedBy = "storage", cascade = CascadeType.PERSIST)
    private List<Drive> driveList = new ArrayList<>();

    public Storage(String bucketName, Long spaceMax, Long spaceTaken) {
        this.bucketName = bucketName;
        this.spaceMax = spaceMax;
        this.spaceTaken = spaceTaken;
    }

    @Override
    public String toString(){
        return this.getBucketName() + " " + this.getSpaceTaken() + " " + this.getSpaceMax();
    }
}
