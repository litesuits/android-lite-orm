package com.litesuits.orm.test;

/**
 * @author MaTianyu
 * @date 2015-03-22
 */
public class TestClassType {

    public static void main(String[] args) {
        System.out.println("".getClass().isPrimitive());
        System.out.println(byte[].class.isPrimitive());
        System.out.println(int[].class.isPrimitive());
        System.out.println(Integer[].class.isPrimitive());
        System.out.println(Integer.class.isPrimitive());
    }
}
