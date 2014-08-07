package com.litesuits.orm.db.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * help to build custom column value
 * 
 * @author MaTianyu
 * 2014-8-7
 */
public interface ColumnValue {

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface CustomValueBuilder {}

	@CustomValueBuilder
	public CharSequence buildValue();

}
