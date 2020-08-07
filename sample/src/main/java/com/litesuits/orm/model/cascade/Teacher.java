package com.litesuits.orm.model.cascade;

import com.litesuits.orm.db.annotation.MapCollection;
import com.litesuits.orm.db.annotation.Mapping;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.Relation;
import com.litesuits.orm.model.Person;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author MaTianyu
 * @date 2015-03-22
 */
@Table("teacher")
public class Teacher extends Person {

    /**
     * 多对多：一个老师多个学生，一个学生多个老师
     * Mapping ManyToMany 表示Teacher 和 Student 是多对多关系
     * MapCollection 表示Queue的具体容器是ConcurrentLinkedQueue
     */
    @Mapping(Relation.ManyToMany)
    @MapCollection(ConcurrentLinkedQueue.class)
    private Queue<Student> studentLinkedQueue;

    private int age;

    public Teacher(String name, int age) {
        super(name);
        this.age = age;
    }

    public Queue<Student> getStudentLinkedQueue() {
        return studentLinkedQueue;
    }

    public void setStudentLinkedQueue(ConcurrentLinkedQueue<Student> studentLinkedQueue) {
        this.studentLinkedQueue = studentLinkedQueue;
    }

    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        if (studentLinkedQueue != null) {
            sb.append(",  studentLinkedQueue=[");
            for (Student t : studentLinkedQueue) {
                sb.append(t.getName()).append(" @").append(Integer.toHexString(t.hashCode())).append(", ");
            }
            sb.append("]  ");
        }
        return "Teacher{"
               + super.toString() +
               ", age=" + age +
               sb.toString() +
               "} ";
    }
}
