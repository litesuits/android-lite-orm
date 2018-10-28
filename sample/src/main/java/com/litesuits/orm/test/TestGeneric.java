package com.litesuits.orm.test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * @author MaTianyu
 * @date 2015-03-22
 */
public class TestGeneric {

    public static void main(String[] args) {
        System.out.println("".getClass());
        System.out.println(byte.class);
        System.out.println(int.class);
        System.out.println(byte[].class);
        System.out.println(int[].class);
        System.out.println(Integer[].class);
        System.out.println(Integer.class);

        B<C> b = new B<C>(C.class);

        System.out.println(b.a().c());
    }

    static abstract class A<T> {
        public abstract T a();
    }

    static class B<T> extends A<T> {
        Class<T> claxx;

        B(Class<T> claxx) {
             this.claxx = claxx;
        }

        @Override
        public T a() {
            Type[] ts = getClass().getGenericInterfaces();
            Type t = getClass().getGenericSuperclass();
            System.out.println(Arrays.toString(ts));
            System.out.println(t);
            ts = ((ParameterizedType) t).getActualTypeArguments();
            t = ts[0];
            System.out.println(Arrays.toString(ts));
            System.out.println(t);

            ts = claxx.getGenericInterfaces();
            t = claxx.getGenericSuperclass();
            System.out.println(Arrays.toString(ts));
            System.out.println(t);
            //System.out.println();
            try {
                return claxx.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    static class C {
        public String c() {
            System.out.println("CCC");
            return "hello C";
        }

    }

}
