package com.litesuits.orm.model.cascade;

import com.litesuits.orm.db.annotation.Mapping;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.Relation;
import com.litesuits.orm.model.Person;

/**
 * @author MaTianyu
 * @date 2015-03-22
 */
@Table("student")
public class Student extends Person {

    @Mapping(Relation.ManyToMany)
    private Teacher[] teachersArray;

    public Student(String name) {
        super(name);
    }

    public Teacher[] getTeachersArray() {
        return teachersArray;
    }

    public void setTeachersArray(Teacher[] teachersArray) {
        this.teachersArray = teachersArray;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (teachersArray != null) {
            sb.append(",  teachersArray=[");
            for (Teacher t : teachersArray) {
                sb.append(t.getName() + " @" + Integer.toHexString(t.hashCode()) + ", ");
            }
            sb.append("]  ");
        }
        return "Student{" +
                super.toString() +
                sb.toString() +
                "} ";
    }
}
