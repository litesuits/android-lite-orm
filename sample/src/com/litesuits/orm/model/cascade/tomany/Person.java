package com.litesuits.orm.model.cascade.tomany;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.enums.AssignType;

/**
 * @author MaTianyu
 * @date 2015-03-22
 */
public abstract class Person {

    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private long id;

    protected String name;

    protected Person(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return " @" + Integer.toHexString(hashCode()) + " , id='" + id + ", name=" + name;
    }
}
