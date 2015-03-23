package com.litesuits.orm.model.cascade;

import com.litesuits.orm.db.annotation.Table;

/**
 * 学校
 *
 * @author MaTianyu
 * @date 2015-03-22
 */
@Table("school")
public class School extends Model {
    public School(String title) {
        super(title);
    }
}
