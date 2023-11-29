package com.pshenai.velvetdrive.entities.file;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FileAO {

    private String file;
    private String folder;

    public String getFile() {
        return file == null ? "Default" : file;
    }

    public String getFolder() {
        return folder == null ? "Default" : folder;
    }

}
