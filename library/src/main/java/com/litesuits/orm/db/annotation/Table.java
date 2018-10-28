package com.litesuits.orm.db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 为对象命名“表名”，如果没有设置，将以对象类名命名表。
 * @author mty
 * @date 2013-6-2下午8:04:46
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
	/**
	 * Table Name
	 */
	public String value();
}
