package com.litesuits.orm.model.cascade;

import com.litesuits.orm.db.annotation.UniqueCombine;

/**
 * @author MaTianyu
 * @date 2015-03-24
 */
public class Book extends Model {

    @UniqueCombine(1)
    private int year;

    @UniqueCombine(1)
    private String author;

    public Book(String title) {
        super(title);
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
