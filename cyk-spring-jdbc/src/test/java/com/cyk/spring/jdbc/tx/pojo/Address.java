package com.cyk.spring.jdbc.tx.pojo;

public class Address {

    public int id;
    public int userId;
    public String address;
    public int zip;

    public Address() {
    }

    public Address(int userId, String address, int zip) {
        this.userId = userId;
        this.address = address;
        this.zip = zip;
    }
}
