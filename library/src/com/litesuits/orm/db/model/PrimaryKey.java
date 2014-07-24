package com.litesuits.orm.db.model;

import com.litesuits.orm.db.annotation.PrimaryKey.AssignType;

import java.lang.reflect.Field;

/**
 * 主键
 * 
 * @author mty
 * @date 2013-6-9上午1:09:33
 */
public class PrimaryKey extends Property {
	private static final long serialVersionUID = 2304252505493855513L;

	public AssignType assign;

	public PrimaryKey(Property p, AssignType assign) {
		this(p.column, p.field, assign);
	}

	public PrimaryKey(String column, Field field, AssignType assign) {
		super(column, field);
		this.assign = assign;
	}

	public boolean isAssignedBySystem() {
		return assign == AssignType.AUTO_INCREMENT;
	}

	public boolean isAssignedByMyself() {
		return assign == AssignType.BY_MYSELF;
	}
}
