package com.litesuits.orm.db.utils;

import com.litesuits.orm.db.annotation.Ignore;
import com.litesuits.orm.db.model.PrimaryKey;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

/**
 * 域工具
 *
 * @author mty
 * @date 2013-6-10下午6:36:29
 */
public class FieldUtil {

    /**
     * 判断域是否被忽略
     *
     * @param f
     * @return
     */
    public static boolean isIgnored(Field f) {
        return f.getAnnotation(Ignore.class) != null;
    }

    /**
     * 检测非法：static final 或者 加了{@link Ignore} 注解
     *
     * @param f
     * @return
     */
    public static boolean isInvalid(Field f) {
        return (Modifier.isStatic(f.getModifiers()) && Modifier.isFinal(f.getModifiers()))
                || isIgnored(f) || f.isSynthetic();
    }

    public static boolean isLong(Field field) {
        return field.getType() == long.class || field.getType() == Long.class;
    }

    public static boolean isInteger(Field field) {
        return field.getType() == int.class || field.getType() != Integer.class;
    }

    /**
     * 判断是否序列化
     *
     * @param f
     * @return
     */
    public static boolean isSerializable(Field f) {
        Class<?>[] cls = f.getType().getInterfaces();
        for (Class<?> c : cls) {
            if (Serializable.class == c) { return true; }
        }
        return false;
    }

    /**
     * 设置域的值
     *
     * @param f
     * @param obj
     * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    public static Object set(Field f, Object obj, Object value) throws IllegalArgumentException, IllegalAccessException {
        f.setAccessible(true);
        f.set(obj, value);
        return f.get(obj);
    }

    /**
     * 获取域的值
     *
     * @param f
     * @param obj
     * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    public static Object get(Field f, Object obj) throws IllegalArgumentException, IllegalAccessException {
        f.setAccessible(true);
        return f.get(obj);
    }

    /**
     * 获取域的泛型类型，如果不带泛型返回null
     *
     * @param f
     * @return
     */
    public static Class<?> getGenericType(Field f) {
        Type type = f.getGenericType();
        if (type instanceof ParameterizedType) {
            type = ((ParameterizedType) type).getActualTypeArguments()[0];
            if (type instanceof Class<?>) return (Class<?>) type;
        } else if (type instanceof Class<?>) return (Class<?>) type;
        return null;
    }

    /**
     * 获取数组的类型
     *
     * @param f
     * @return
     */
    public static Class<?> getComponentType(Field f) {
        return f.getType().getComponentType();
    }


    public static Object getAssignedKeyObject(PrimaryKey key, Object entity) throws IllegalArgumentException,
            IllegalAccessException {
        Object obj = get(key.field, entity);
        if (key.isAssignedByMyself()
                || (key.isAssignedBySystem() && obj != null && ((Number) obj).longValue() > 0)) { return obj; }
        return null;
    }

    public static boolean setKeyValueIfneed(Object entity, PrimaryKey key, Object keyObj, long rowID)
            throws IllegalArgumentException, IllegalAccessException {
        if (key != null && key.isAssignedBySystem()
                && (keyObj == null || ((Number) keyObj).longValue() < 1)) {
            FieldUtil.set(key.field, entity, rowID);
            return true;
        }
        return false;
    }

    public static List<Field> getAllDeclaredFields(Class<?> claxx) {
        // find all field.
        LinkedList<Field> fieldList = new LinkedList<Field>();
        while (claxx != null) {
            Field[] fs = claxx.getDeclaredFields();
            for (int i = 0; i < fs.length; i++) {
                Field f = fs[i];
                if (!isInvalid(f)) {
                    fieldList.addLast(f);
                }
            }
            claxx = claxx.getSuperclass();
        }
        return fieldList;
    }
}
