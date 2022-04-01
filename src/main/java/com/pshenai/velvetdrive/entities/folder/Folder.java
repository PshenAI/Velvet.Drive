package com.pshenai.velvetdrive.entities.folder;

import com.pshenai.velvetdrive.entities.drive.Drive;
import com.pshenai.velvetdrive.entities.file.File;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
@Entity
@Table(name = "folders")
public class Folder {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private Long folderSize;
    private String backUrl;
    @ManyToOne
    @JoinColumn(name = "driveId")
    private Drive drive;
    @OneToMany(mappedBy = "folder", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<File> files = new ArrayList<>();

    public Folder(String name, Drive drive, String backUrl) {
        this.name = name;
        this.drive = drive;
        this.backUrl = backUrl;
    }

    public String getFilePath(String fileName){
        Optional<File> file = files.stream().filter(a -> a.getName().equals(fileName)).findFirst();
        if(file.isPresent()){
            return file.get().getPath();
        } else {
            return null;
        }
    }

    public void deleteAllFiles(){
        files.forEach(File::deleteByPath);
    }

    @Override
    public String toString() {
        return "Folder{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", drive=" + drive +
                '}';
    }
}
