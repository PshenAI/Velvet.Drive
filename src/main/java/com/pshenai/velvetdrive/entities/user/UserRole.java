package com.pshenai.velvetdrive.entities.user;

public enum UserRole {
    ADMIN, USER;

    @Override
    public String toString() {
        return "ROLE_" + name();
    }
}
