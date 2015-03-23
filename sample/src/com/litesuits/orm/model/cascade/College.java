package com.litesuits.orm.model.cascade;

import com.litesuits.orm.db.annotation.Table;

/**
 * @author MaTianyu
 * @date 2015-03-22
 */
@Table("college")
public class College extends Model {
    public College(String title) {
        super(title);
    }
}
