package com.litesuits.orm.model.cascade;

import com.litesuits.orm.db.annotation.Mapping;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.Relation;
import com.litesuits.orm.model.Model;

import java.util.ArrayList;

/**
 * 学校
 *
 * @author MaTianyu
 * @date 2015-03-22
 */
@Table("school")
public class School extends Model {

    /**
     * 一个学校有多个班
     */
    @Mapping(Relation.OneToMany)
    public ArrayList<Classes> classesList;

    public School(String title) {
        super(title);
    }

    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        if (classesList != null) {
            sb.append(" classesList=[");
            for (Classes t : classesList) {
                sb.append("\n");
                sb.append(t.toString()).append(" @ ").append(Integer.toHexString(t.hashCode()));
            }
            sb.append("\n");
            sb.append("]  ");
        }
        return "School{" +
               super.toString() +
               sb.toString() +
               "} ";
    }
}
