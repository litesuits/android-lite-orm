package com.litesuits.orm.test;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.Collection;

/**
 * @author MaTianyu
 * @date 14-4-29
 */
public class TestGetClass {
    public static void main(String[] args) {
        System.out.println(new DecimalFormat("#.00").format(4563.125434));
        try {
            Field fb = A.class.getDeclaredField("b");

            System.out.println(fb.getType());
            System.out.println(fb.getType().getComponentType());
            System.out.println(Collection.class.isAssignableFrom(fb.getType()));

            Field fl = A.class.getDeclaredField("l");
            System.out.println(fl.getType());
            System.out.println(Collection.class.isAssignableFrom(fl.getType()));
            System.out.println(fl.getType().getComponentType());
            System.out.println(getGenericType(fl));
            System.out.println(getGenericType(fl).getComponentType());

            Field fa = A.class.getDeclaredField("a");
            System.out.println(fa.getType());
            System.out.println(fa.getType().getComponentType());
            System.out.println(Collection.class.isAssignableFrom(fa.getType()));

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
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

    public static class A {
        B             b;
        Collection<B> l;
        B[]           a;
    }

    public static class B {

    }
}
