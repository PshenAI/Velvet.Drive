package com.pshenai.velvetdrive.entities.drive;

public enum DrivePlan {
    VELVET(10240L, 0.0),
    COSMOS(30720L, 10.0),
    OBSIDIAN(51200L, 20.0);

    private final Long space;
    private final Double price;

    DrivePlan(Long space, Double price) {
        this.space = space;
        this.price = price;
    }

    public Long getSpace() {
        return space;
    }

    public Double getPrice() {
        return price;
    }
}
