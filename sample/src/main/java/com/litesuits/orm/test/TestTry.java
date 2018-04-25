package com.litesuits.orm.test;

/**
 * @author MaTianyu @http://litesuits.com
 * @date 2015-10-12
 */
public class TestTry {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        for (int i = 0; i < 1000000; i++) {
            try {
                int j = i + 5 + i;
                Object o = new Object();
                String s = null;
                s.equals("");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }
        }

        long end = System.currentTimeMillis();

        System.out.println("Take Time: " + (end - start));
    }
    
}
