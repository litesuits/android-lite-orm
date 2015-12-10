package com.litesuits.orm.model.cascade;

import com.litesuits.orm.db.annotation.Mapping;
import com.litesuits.orm.db.annotation.UniqueCombine;
import com.litesuits.orm.db.enums.Relation;
import com.litesuits.orm.model.Model;

/**
 * @author MaTianyu
 * @date 2015-03-24
 */
public class Book extends Model {

    /**
     * 和 author 联合唯一
     */
    @UniqueCombine(1)
    private int index;

    /**
     * 和 index 联合唯一
     */
    @UniqueCombine(1)
    private String author;

    /**
     * 书和学生：多对一关系
     */
    @Mapping(Relation.ManyToOne)
    public Student student;

    public Book(String title) {
        super(title);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "Book{" +
               super.toString() +
               "index=" + index +
               ", author='" + author + '\'' +
               "} ";
    }
}
