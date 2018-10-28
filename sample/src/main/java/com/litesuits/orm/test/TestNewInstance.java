package com.litesuits.orm.test;

import com.litesuits.orm.db.annotation.MapCollection;
import com.litesuits.orm.db.utils.ClassUtil;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author MaTianyu
 * @date 14-4-29
 */
public class TestNewInstance {
    public static void main(String[] args) {
        try {
            Field absList = A.class.getDeclaredField("absList");
            Class c1 = absList.getType();
            Class c2 = (Class) ((ParameterizedType) absList.getGenericType()).getActualTypeArguments()[0];

            System.out.println("容器类型： " + c1);
            System.out.println("承载类： " + c2);

            A a = new A();
            absList.set(a, ClassUtil.newCollectionForField(absList));
            Collection ca = a.absList;
            ca.add("hello ");
            ca.add(" a test ");
            System.out.println(a);
            System.out.println("------------------\n\n");

            Field ab = A.class.getDeclaredField("ab");
            c1 = ab.getType();
            c2 = (Class) ((ParameterizedType) ab.getGenericType()).getActualTypeArguments()[0];
            System.out.println("容器类型： " + c1);
            System.out.println(Collection.class.isAssignableFrom(c1));
            System.out.println(c1.isAssignableFrom(Collection.class));
            System.out.println("承载类： " + c2);

            ab.set(a, c1.newInstance());
            ca = a.ab;
            ca.add(c2.newInstance());
            ca.add(c2.newInstance());
            System.out.println(a);
            System.out.println("------------------\n\n");


            Field ac = A.class.getDeclaredField("ac");
            c1 = ac.getType();
            c2 = ac.getType().getComponentType();
            System.out.println("容器类型： " + c1);
            System.out.println(c1.isArray());
            System.out.println(Collection.class.isAssignableFrom(c1));
            System.out.println("承载类： " + c2);
            System.out.println(ac.getType().getComponentType());
            //Constructor cons = c1.getConstructor(int.class);
            //System.out.println("cons : " + cons);
            //ac.set(a, cons.newInstance(2));
            C o1 = (C) c2.newInstance();
            C o2 = (C) c2.newInstance();
            Object array = Array.newInstance(c2, 10);
            Array.set(array, 0, o1);
            Array.set(array, 1, o2);
            ac.set(a, array);
            Object[] oa = a.ac;
            System.out.println("A is ： " + a);
            System.out.println("------------------\n\n");


            Field cc = A.class.getDeclaredField("cc");
            c1 = cc.getType();
            c2 = (Class) ((ParameterizedType) cc.getGenericType()).getActualTypeArguments()[0];

            System.out.println("容器类型： " + c1);
            System.out.println(Collection.class.isAssignableFrom(c1));
            System.out.println("承载类： " + c2);

            cc.set(a, c1.newInstance());
            ca = a.cc;
            ca.add(c2.newInstance());
            ca.add(c2.newInstance());

            System.out.println(a);
            System.out.println("------------------\n\n");


            List<C> l = new ArrayList<C>();
            l.add(new C());
            Collection cl = l;
            Type type = l.getClass().getGenericSuperclass();

            System.out.println(((ParameterizedType) type).getActualTypeArguments()[0]);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取域的泛型类型，如果不带泛型返回null
     */
    public static Class<?> getGenericType(Field f) {
        Type type = f.getGenericType();
        if (type instanceof ParameterizedType) {
            type = ((ParameterizedType) type).getActualTypeArguments()[0];
            if (type instanceof Class<?>)
                return (Class<?>) type;
        } else if (type instanceof Class<?>)
            return (Class<?>) type;
        return null;
    }

    public static class A {
        @MapCollection(LinkedList.class)
        List<String> absList;

        C[] ac;
        ArrayList<B> ab;
        LinkedList<B> bl;
        Vector<B> vb;
        ConcurrentLinkedQueue<C> cc;
        LinkedBlockingDeque<C> lc;

        @Override public String toString() {
            return "A{" +
                   "absList=" + absList +
                   ", ac=" + Arrays.toString(ac) +
                   ", ab=" + ab +
                   ", bl=" + bl +
                   ", vb=" + vb +
                   ", cc=" + cc +
                   ", lc=" + lc +
                   '}';
        }
    }

    public static class B {
        @Override
        public String toString() {
            return "b";
        }
    }

    public static class C {
        @Override
        public String toString() {
            return "c";
        }
    }
}
