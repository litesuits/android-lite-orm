package com.litesuits.orm.db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 忽略字段，如果属性被设置忽略，那么将在增、改的时候忽略它。
 * @author mty
 * @date 2013-6-2下午8:10:13
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Ignore {}
