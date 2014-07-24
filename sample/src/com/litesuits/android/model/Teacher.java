package com.litesuits.android.model;

import com.litesuits.orm.db.annotation.Mapping;
import com.litesuits.orm.db.annotation.Mapping.Relation;
import com.litesuits.orm.db.annotation.Table;

import java.util.ArrayList;

/**
 * 
 * 老湿，和Man是多对多关系
 * @author MaTianyu
 * 2014-3-7上午10:42:55
 */
@Table("teacher")
public class Teacher extends Person{
    @Mapping(Relation.ManyToMany)
    private ArrayList<Man> list;

    public Teacher(String name, ArrayList<Man> list) {
        this.name = name;
        this.list = list;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Man> getList() {
        return list;
    }

    public void setList(ArrayList<Man> list) {
        this.list = list;
    }

    @Override
    public String toString() {
		StringBuilder sb = new StringBuilder("Teacher [id=" + id + ", name=" + name);
		if (list != null) {
			sb.append(", list=");
			for (Man m : list) {
				sb.append(m.getName() + ", ");
			}
		}
		return sb.toString();
	}
}