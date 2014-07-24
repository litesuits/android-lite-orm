package com.litesuits.android.model;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.PrimaryKey.AssignType;
import com.litesuits.orm.db.annotation.Table;

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
