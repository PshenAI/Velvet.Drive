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
    private Integer maxSpace;
    private Integer availSpace;

    @OneToMany(mappedBy = "storage", cascade = CascadeType.ALL)
    private List<Drive> driveList = new ArrayList<>();

    public Storage(String dirPath, Integer maxSpace, Integer availSpace) {
        this.dirPath = dirPath;
        this.maxSpace = maxSpace;
        this.availSpace = availSpace;
    }

    @Override
    public String toString(){
        return this.getDirPath() + " " + this.getAvailSpace() + " " + this.getMaxSpace();
    }
}