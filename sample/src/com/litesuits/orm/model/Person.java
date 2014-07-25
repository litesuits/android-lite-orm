package com.litesuits.orm.model;

import com.litesuits.orm.db.annotation.*;
import com.litesuits.orm.db.annotation.PrimaryKey.AssignType;

/**
 * Wifi 和Man 是一对一关系
 * 
 * @author MaTianyu
 * 2014-3-7上午10:39:45
 */
@Table("wife")
public class Person extends  BaseModel{
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    @Column("_id")
    protected long id;

    @Unique
    @NotNull
    @Conflict(Conflict.Strategy.FAIL)
    public String name;

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                "} " + super.toString();
    }
}
