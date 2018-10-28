package com.litesuits.orm.db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 联合唯一性约束
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueCombine {
    /**
     * 使用相同int值将归为一组[联合唯一]属性
     */
    public int value();
}
