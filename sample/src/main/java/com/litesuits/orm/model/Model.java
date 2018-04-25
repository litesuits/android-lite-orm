package com.litesuits.orm.model;

/**
 * @author MaTianyu
 * @date 2015-03-22
 */
public abstract class Model {

    private long id;

    private String title;

    public Model(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "  id = " + id + ", title = " + title + "  ";
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
