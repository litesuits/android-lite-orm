package com.litesuits.orm.db.utils;

import com.litesuits.orm.db.annotation.MapCollection;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Date;

/**
 * 类工具
 *
 * @author mty
 * @date 2013-6-10下午8:00:46
 */
public class ClassUtil {

    /**
     * 判断类是否是基础数据类型
     * 目前支持11种
     * 在{@link com.litesuits.orm.db.utils.DataUtil#injectDataToObject} 中注入也有体现
     */
    public static boolean isBaseDataType(Class<?> clazz) {
        return clazz.isPrimitive() || clazz.equals(String.class) || clazz.equals(Boolean.class)
               || clazz.equals(Integer.class) || clazz.equals(Long.class) || clazz.equals(Float.class)
               || clazz.equals(Double.class) || clazz.equals(Byte.class) || clazz.equals(Character.class)
               || clazz.equals(Short.class) || clazz.equals(Date.class) || clazz.equals(byte[].class)
               || clazz.equals(Byte[].class);
    }

    /**
     * 根据类获取对象：不再必须一个无参构造
     */
    public static <T> T newInstance(Class<T> claxx)
            throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<?>[] cons = claxx.getDeclaredConstructors();
        for (Constructor<?> c : cons) {
            Class[] cls = c.getParameterTypes();
            if (cls.length == 0) {
                c.setAccessible(true);
                return (T) c.newInstance();
            } else {
                Object[] objs = new Object[cls.length];
                for (int i = 0; i < cls.length; i++) {
                    objs[i] = getDefaultPrimiticeValue(cls[i]);
                }
                c.setAccessible(true);
                return (T) c.newInstance(objs);
            }
        }
        return null;
    }

    public static Object newCollection(Class<?> claxx) throws IllegalAccessException, InstantiationException {
        return claxx.newInstance();
    }

    public static Object newCollectionForField(Field field) throws IllegalAccessException, InstantiationException {
        MapCollection coll = field.getAnnotation(MapCollection.class);
        if (coll == null) {
            return field.getType().newInstance();
        } else {
            return coll.value().newInstance();
        }
    }

    public static Object newArray(Class<?> claxx, int size) {
        return Array.newInstance(claxx, size);
    }


    public static Object getDefaultPrimiticeValue(Class clazz) {
        if (clazz.isPrimitive()) {
            return clazz == boolean.class ? false : 0;
        }
        return null;
    }

    public static boolean isCollection(Class claxx) {
        return Collection.class.isAssignableFrom(claxx);
    }

    public static boolean isArray(Class claxx) {
        return claxx.isArray();
    }

}
