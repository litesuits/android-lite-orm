package com.litesuits.android.model;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Mapping;
import com.litesuits.orm.db.annotation.Mapping.Relation;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.PrimaryKey.AssignType;
import com.litesuits.orm.db.annotation.Table;

import java.util.ArrayList;

/**
 * 公司：多个 {@link Man} 在同一公司里，一个 {@link Man} 不能在多个公司。
 *
 * @author MaTianyu
 *         2014-3-7上午10:55:04
 */
@Table("company")
public class Company extends BaseModel {

    @PrimaryKey(AssignType.BY_MYSELF)
    @Column("_name")
    private String name;

    @Mapping(Relation.OneToMany)
    private ArrayList<Man> list;

    public Company() {
    }

    public Company(String name, ArrayList<Man> list) {
        this.name = name;
        this.list = list;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Company [ name=" + name);
        if (list != null) {
            sb.append(", list=");
            for (Man m : list) {
                sb.append(m.getName() + ", ");
            }
        }
        return sb.toString();
    }
}
