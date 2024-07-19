package com.tpe.domain.enums;

public enum RoleType {

    ADMIN("Admin"),
    CUSTOMER("Customer");


    private final String name;

    RoleType(String name) {
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
