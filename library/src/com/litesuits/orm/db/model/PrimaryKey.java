package com.litesuits.orm.db.model;


import java.lang.reflect.Field;

/**
 * 主键
 * 
 * @author mty
 * @date 2013-6-9上午1:09:33
 */
public class PrimaryKey extends Property {
    private static final long serialVersionUID = 2304252505493855513L;

    public com.litesuits.orm.db.annotation.PrimaryKey.AssignType assign;

    public PrimaryKey(Property p, com.litesuits.orm.db.annotation.PrimaryKey.AssignType assign) {
        this(p.column, p.field, assign);
    }

    public PrimaryKey(String column, Field field, com.litesuits.orm.db.annotation.PrimaryKey.AssignType assign) {
		super(column, field);
		this.assign = assign;
	}

	public boolean isAssignedBySystem() {
		return assign == com.litesuits.orm.db.annotation.PrimaryKey.AssignType.AUTO_INCREMENT;
	}

	public boolean isAssignedByMyself() {
		return assign == com.litesuits.orm.db.annotation.PrimaryKey.AssignType.BY_MYSELF;
	}
}
