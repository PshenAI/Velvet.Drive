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

    private String dirPath;
    private Long maxSpace;
    private Long availSpace;

    @OneToMany(mappedBy = "storage", cascade = CascadeType.PERSIST)
    private List<Drive> driveList = new ArrayList<>();

    public Storage(String dirPath, Long maxSpace, Long availSpace) {
        this.dirPath = dirPath;
        this.maxSpace = maxSpace;
        this.availSpace = availSpace;
    }

    @Override
    public String toString(){
        return this.getDirPath() + " " + this.getAvailSpace() + " " + this.getMaxSpace();
    }
}
