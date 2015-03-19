package com.litesuits.orm.model;

import com.litesuits.orm.db.annotation.*;
import com.litesuits.orm.db.annotation.PrimaryKey.AssignType;

/**
 * 一个 {@link Man}可以有多个地址
 * 
 * @author MaTianyu
 * 2014-3-7上午10:53:09
 */
@Table("address")
public class Address extends BaseModel{

    public static final String COL_ID = "_id";
    public static final String COL_ADDRESS = "_address";
    public static final String COL_CITY= "_city";

    @PrimaryKey(AssignType.AUTO_INCREMENT)
    @Column(COL_ID)
    public long id;

    @NotNull
    @Column(COL_ADDRESS)
    public String address;

    @Column(COL_CITY)
    public String city;

    //public Address() {
    //}

    public Address(String address, String city) {
        this.address = address;
        this.city = city;
    }

    public Address(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}
