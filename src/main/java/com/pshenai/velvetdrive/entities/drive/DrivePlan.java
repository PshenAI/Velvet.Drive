package com.pshenai.velvetdrive.entities.drive;

public enum DrivePlan {
    VELVET(15.0, 0.0),
    COSMOS(30.0, 10.0),
    OBSIDIAN(70.0, 20.0);

    private final Double space;
    private final Double price;

    DrivePlan(Double space, Double price) {
        this.space = space;
        this.price = price;
    }

    public Double getSpace() {
        return space;
    }

    public Double getPrice() {
        return price;
    }
}
