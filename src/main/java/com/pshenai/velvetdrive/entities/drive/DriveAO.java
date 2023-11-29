package com.pshenai.velvetdrive.entities.drive;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DriveAO {

    private String folder;
    private boolean noFile;
    private boolean bigFile;
    private boolean folderList;
    private boolean duplicateFolder;
    private boolean wrongName;
    private String keyName;

    public String getFolder() {
        return folder == null ? "Default" : folder;
    }

    public String getKeyName() {
        return keyName == null ? "Default" : keyName;
    }
}
