package com.litesuits.orm.test;

import com.litesuits.orm.db.assit.CollSpliter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

/**
 * @author MaTianyu @http://litesuits.com
 * @date 2015-12-02
 */
public class TestSpliter {
    public static void main(String[] args) throws Exception {
        Collection coll = new LinkedList();
        for (int i = 0; i < 9; i++) {
            coll.add("a " + i);
        }

        CollSpliter.split(coll, 3, new CollSpliter.Spliter() {
            @Override public int oneSplit(ArrayList list) throws Exception {
                System.out.println("-------  " + list.size());
                for (Object o : list) {
                    System.out.println(o);
                }
                return 0;
            }
        });
    }
}
