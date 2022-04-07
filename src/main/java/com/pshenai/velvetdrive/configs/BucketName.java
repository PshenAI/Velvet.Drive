package com.pshenai.velvetdrive.configs;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BucketName {
    MAIN_BUCKET("velvet-drive-storage");
    private final String bucketName;
}
