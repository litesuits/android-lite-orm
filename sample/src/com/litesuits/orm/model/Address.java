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

    @PrimaryKey(AssignType.AUTO_INCREMENT)
    public long id;

    @NotNull
    @Unique
    public String address;

    public Address() {
    }

    public Address(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", address='" + address + '\'' +
                "} " + super.toString();
    }
}
