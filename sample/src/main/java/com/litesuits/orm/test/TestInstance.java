package com.litesuits.orm.test;

/**
 * @author MaTianyu
 * @date 14-7-29
 */
public class TestInstance {
    public static void main(String [] args){
        A a = new A();
        B b = new B();
        C c = new C();
        System.out.println(a instanceof A);
        System.out.println(a instanceof B);
        System.out.println(a instanceof C);
        System.out.println(b instanceof A);
        System.out.println(b instanceof B);
        System.out.println(b instanceof C);
        System.out.println(c instanceof A);
        System.out.println(c instanceof B);
        System.out.println(c instanceof C);

        System.out.println( C.class.getName().equals(c.getClass().getName()));
        System.out.println( B.class.getName().equals(c.getClass().getName()));
        System.out.println( A.class.getName().equals(c.getClass().getName()));
    }

    public static class A{

    }
    public static class B extends A{

    }
    public static class C extends B{

    }
}
