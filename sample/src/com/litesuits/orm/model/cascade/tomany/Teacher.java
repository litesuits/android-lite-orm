package com.litesuits.orm.model.cascade.tomany;

import com.litesuits.orm.db.annotation.Mapping;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.Relation;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author MaTianyu
 * @date 2015-03-22
 */
@Table("teacher")
public class Teacher extends Person {

    @Mapping(Relation.ManyToMany)
    private ConcurrentLinkedQueue<Student> studentLinkedQueue;

    public Teacher(String name) {
        super(name);
    }

    public ConcurrentLinkedQueue<Student> getStudentLinkedQueue() {
        return studentLinkedQueue;
    }

    public void setStudentLinkedQueue(ConcurrentLinkedQueue<Student> studentLinkedQueue) {
        this.studentLinkedQueue = studentLinkedQueue;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (studentLinkedQueue != null) {
            sb.append(",  studentLinkedQueue=[");
            for (Student t : studentLinkedQueue) {
                sb.append(t.getName() + " @" + Integer.toHexString(t.hashCode()) + ", ");
            }
            sb.append("]  ");
        }
        return "Teacher{" +
                super.toString() +
                sb.toString() +
                "} ";
    }
}
