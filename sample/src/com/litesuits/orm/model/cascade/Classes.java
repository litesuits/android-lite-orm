package com.litesuits.orm.model.cascade;

import com.litesuits.orm.db.annotation.Mapping;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.model.cascade.tomany.Student;
import com.litesuits.orm.model.cascade.tomany.Teacher;

import java.util.LinkedList;
import java.util.Stack;

/**
 * 班级
 *
 * @author MaTianyu
 * @date 2015-03-22
 */
@Table("class")
public class Classes extends Model {

    @Mapping(Mapping.Relation.ManyToMany)
    private Stack<Teacher> teacherStack;

    @Mapping(Mapping.Relation.OneToMany)
    private LinkedList<Student> studentLinkedList;

    public Classes(String title) {
        super(title);
    }

    public Stack<Teacher> getTeacherStack() {
        return teacherStack;
    }

    public void setTeacherStack(Stack<Teacher> teacherStack) {
        this.teacherStack = teacherStack;
    }

    public LinkedList<Student> getStudentLinkedList() {
        return studentLinkedList;
    }

    public void setStudentLinkedList(LinkedList<Student> studentLinkedList) {
        this.studentLinkedList = studentLinkedList;
    }

    @Override
    public String toString() {
        return "Class{" +
                super.toString() +
                "teacherStack=" + teacherStack +
                ", studentLinkedList=" + studentLinkedList +
                "} ";
    }
}
