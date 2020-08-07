package com.litesuits.orm.db.assit;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 辅助事务
 *
 * @author mty
 * @date 2013-6-15下午11:09:15
 */
public class CollSpliter {

    /**
     * 将 collection 拆分成 N 组ArrayList，每组 perSize 个元素，最后一组元素数量未知。
     *
     * {@link Spliter#oneSplit(ArrayList)}将被调用N次，N >= 1.
     *
     * @return sum of {@link Spliter#oneSplit(ArrayList)}
     */
    public static <T> int split(Collection<T> collection, int perSize, Spliter<T> spliter) throws Exception {
        ArrayList<T> list = new ArrayList<T>();
        int count = 0;
        if (collection.size() <= perSize) {
            list.addAll(collection);
            count += spliter.oneSplit(list);
        } else {
            int i = 0, j = 1;
            for (T data : collection) {
                if (i < j * perSize) {
                    list.add(data);
                } else {
                    count += spliter.oneSplit(list);
                    j++;
                    list.clear();
                    list.add(data);
                }
                i++;
            }
            if (list.size() > 0) {
                count += spliter.oneSplit(list);
            }
        }
        return count;
    }

    public interface Spliter<T> {
        int oneSplit(ArrayList<T> list) throws Exception;
    }
}