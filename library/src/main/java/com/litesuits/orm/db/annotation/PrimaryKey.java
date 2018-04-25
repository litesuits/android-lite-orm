package com.litesuits.orm.db.annotation;

import com.litesuits.orm.db.enums.AssignType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 主键，这是一个模型里必须有的,是对象的唯一标识。
 * 没有主键将会报错，一个表只有一个主关键字，它有两种类型：
 * 1.主键值自定义，适用于已有唯一ID的对象。
 * 2.主键值系统定义，适用于没有唯一ID的对象，将使用递增的数字作为值賦予它。
 * 
 * @author mty
 * @date 2013-6-2下午7:01:47
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PrimaryKey {
    AssignType value();
}
