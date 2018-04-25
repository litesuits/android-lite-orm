package com.litesuits.orm.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

/**
 * @author MaTianyu
 * @date 2015-03-23
 */
public class TestNext {
    public static void main(String[] args) {
        ArrayList<String> list = new ArrayList<String>();
        Stack<String> stack = new Stack<String>();
        list.add("a");
        list.add("b");
        list.add("c");
        list.add("d");
        stack.add("a");
        stack.add("b");
        Iterator it = list.iterator();
        System.out.println(it.next());
        while (it.hasNext()) {
            System.out.println(it.next());
        }
        //System.out.println(it.next());

        //it = stack.iterator();
        //System.out.println(it.next());
        //System.out.println(it.next());
        //System.out.println(it.next());
    }
}
