package com.litesuits.orm.db.model;

import com.litesuits.orm.db.utils.DataUtil;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * 属性
 * @author mty
 * @date 2013-6-9上午1:09:17
 */
public class Property implements Serializable{
	
	private static final long serialVersionUID = 1542861322620643038L;
	public String column;
	public Field field;
	public int classType = DataUtil.CLASS_TYPE_NONE;

	public Property(){
	}

	public Property(String column, Field field){
		this.column = column;
		this.field = field;
	}

	@Override
	public String toString() {
		return "Property [column=" + column + ", field=" + field.getName() + "]";
	}
	
}
