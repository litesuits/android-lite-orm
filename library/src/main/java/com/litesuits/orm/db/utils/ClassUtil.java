package com.litesuits.orm.db.utils;

import com.litesuits.orm.db.annotation.MapCollection;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * 类工具
 *
 * @author mty
 * @date 2013-6-10下午8:00:46
 */
public class ClassUtil {
    private static final Map<Class, ClassFactory> CLASS_FACTORIES = new HashMap();

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
        ClassFactory<T> factory = CLASS_FACTORIES.get(claxx);
        if (factory == null) {
            factory = ClassFactory.get(claxx);
            CLASS_FACTORIES.put(claxx, factory);
        }
        return factory.newInstance();
    }

    public static Object newCollection(Class<?> claxx) throws IllegalAccessException, InstantiationException {
        return claxx.newInstance();
    }

    public static Object newCollectionForField(Field field) throws IllegalAccessException, InstantiationException {
        MapCollection coll = field.getAnnotation(MapCollection.class);
        if (coll == null) {
            final Class rawType = field.getType();
            if (rawType.isInterface()) {
                if (List.class.isAssignableFrom(rawType)) {
                    return new ArrayList<>();
                } else if (SortedSet.class.isAssignableFrom(rawType)) {
                    return new TreeSet<>();
                } else if (Set.class.isAssignableFrom(rawType)) {
                    return new LinkedHashSet<>();
                } else if (Queue.class.isAssignableFrom(rawType)) {
                    return new ArrayDeque<>();
                } else {
                    throw new IllegalAccessException("The type " + rawType.getName() + " cannot be instantiated");
                }
            } else {
                return rawType.newInstance();
            }
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
