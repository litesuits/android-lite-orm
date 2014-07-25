package com.litesuits.orm.model;

import com.litesuits.orm.db.annotation.Mapping;
import com.litesuits.orm.db.annotation.Mapping.Relation;
import com.litesuits.orm.db.annotation.Table;

/**
 * Wifi 和Man 是一对一关系
 * 
 * @author MaTianyu
 * 2014-3-7上午10:39:45
 */
@Table("wife")
public class Wife extends Person {
    public String des = "about wife";

    @Mapping(Relation.OneToOne)
    public Man man;

    public Wife() {
    }

    public Wife(String name, Man man) {
        this.name = name;
        this.man = man;
    }
}
