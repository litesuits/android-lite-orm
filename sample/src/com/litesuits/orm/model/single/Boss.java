package com.litesuits.orm.model.single;

import com.litesuits.orm.db.annotation.Mapping;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.Relation;

import java.util.ArrayList;

/**
 * 老湿，和Man是多对多关系
 *
 * @author MaTianyu
 *         2014-3-7上午10:42:55
 */
@Table("boss")
public class Boss extends Person {
    public String address;
    public String phone;

    @Mapping(Relation.ManyToMany)
    private ArrayList<Man> list;

    public Boss() {

    }

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

    public String getAddress() {
        return address;
    }

    public Boss setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public Boss setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Boss [id=" + id + ", name=" + name + ", phone=" + phone + ", " +
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