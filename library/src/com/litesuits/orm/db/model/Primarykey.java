package com.litesuits.orm.db.model;


import com.litesuits.orm.db.annotation.PrimaryKey;

import java.lang.reflect.Field;

/**
 * 主键
 * 
 * @author mty
 * @date 2013-6-9上午1:09:33
 */
public class Primarykey extends Property {
    private static final long serialVersionUID = 2304252505493855513L;

    public PrimaryKey.AssignType assign;

    public Primarykey(Property p, PrimaryKey.AssignType assign) {
        this(p.column, p.field, assign);
    }

    public Primarykey(String column, Field field, PrimaryKey.AssignType assign) {
		super(column, field);
		this.assign = assign;
	}

	public boolean isAssignedBySystem() {
		return assign == PrimaryKey.AssignType.AUTO_INCREMENT;
	}

	public boolean isAssignedByMyself() {
		return assign == PrimaryKey.AssignType.BY_MYSELF;
	}
}
