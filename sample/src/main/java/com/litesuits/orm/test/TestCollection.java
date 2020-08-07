/*
 * Copyright (C) 2013 litesuits.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.litesuits.orm.test;

import com.litesuits.orm.db.utils.FieldUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by matianyu on 16/4/2.
 */
public class TestCollection {
    public A[] aArray;
    public ArrayList<A> alist;

    public static void main(String[] args) {
        try {
            Field[] fList = TestCollection.class.getDeclaredFields();
            TestCollection tc = new TestCollection();
            for (Field f : fList) {
                System.out.println("Field: " + f);
                System.out.println(f.getType().isArray());
                System.out.println(Collection.class.isAssignableFrom(f.getType()));

                if (f.getType().isArray()) {
                    Object array = FieldUtil.get(f, tc);
                    System.out.println(Arrays.asList((Object[]) array));
                }
                Arrays.asList(tc.aArray);
                Arrays.asList(tc.alist);
                Arrays.asList("");
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static class A {

    }


}
