package com.litesuits.orm.model.single;

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
@Table("boss")
public class Boss extends Person{

    public String address = "默认地址";
    public String phone = "";

    @Mapping(Relation.ManyToMany)
    private ArrayList<Man> list;

    public Boss(String name, ArrayList<Man> list) {
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
        StringBuilder sb = new StringBuilder("Boss [id=" + id + ", name=" + name + ", phone=" + phone+ ", " +
                "address=" + address);
        if (list != null) {
            sb.append(", list=");
            for (Man m : list) {
                sb.append(m.getName() + ", ");
            }
        }
		return sb.toString();
	}
}