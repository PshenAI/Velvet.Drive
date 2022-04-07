package com.pshenai.velvetdrive.entities.file;

import com.amazonaws.services.s3.AmazonS3;
import com.pshenai.velvetdrive.configs.BucketName;
import com.pshenai.velvetdrive.entities.folder.Folder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "files")
public class File {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private Long fileSize;
    private String path;
    private String backUrl;
    @ManyToOne
    @JoinColumn(name = "folderId")
    private Folder folder;

    public File(String name, Long fileSize, String path, String backUrl, Folder folder) {
        this.name = name;
        this.fileSize = fileSize;
        this.path = path;
        this.backUrl = backUrl;
        this.folder = folder;
    }

    @Override
    public String toString() {
        return "File{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", fileSize=" + fileSize +
                ", path='" + path + '\'' +
                '}';
    }
}