package com.litesuits.orm.db.model;

import com.litesuits.orm.db.assit.Checker;

import java.util.HashMap;
import java.util.Map;

/**
 * help to build custom column value
 * {@link #columns}不能为NULL，你的SQL语句仅仅对在这个数组里面的列起作用。比如update，只更新这里面的列。
 * 列名column为map的key,key对应的value是强制赋给这个列对应的值.
 * 如果列column对应的value不为null,那么将会使用这里面的值优先写入数据库.
 * 如果列column对应的value为null,那么将会把实体该字段最新的值写入数据库.
 *
 * @author MaTianyu
 *         2014-8-7
 */
public class ColumnsValue {

    /**
     * your sql only affect column in this {@link #columns}.
     */
    public String[] columns;

    /**
     * can be null, if not this will mapping with {@link #columns} 1 by 1.
     * if not null, your columns well aways set value in {@link #values}.
     */
    //private Object[] values;

    private Map<String, Object> map = new HashMap<String, Object>();

    public ColumnsValue(Map<String, Object> map) {
        if (!Checker.isEmpty(map)) {
            columns = new String[map.size()];
            //values = new Object[map.size()];
            int i = 0;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                columns[i] = entry.getKey();
                //values[i] = entry.getValue();
                i++;
            }
            this.map = map;
        }
    }

    public ColumnsValue(String[] columns) {
        this.columns = columns;
        for (String key : columns) {
            map.put(key, null);
        }
    }

    public ColumnsValue(String[] columns, Object[] values) {
        this.columns = columns;
        //this.values = values;
        if (values != null) {
            if (columns.length != values.length) {
                throw new IllegalArgumentException("length of columns and values must be the same");
            }
            int i = 0;
            for (String key : columns) {
                map.put(key, values[i++]);
            }
        } else {
            for (String key : columns) {
                map.put(key, null);
            }
        }
    }

    public boolean checkColumns() {
        if (columns == null) {
            throw new IllegalArgumentException("columns must not be null");
        }
        //else if (values != null && columns.length != values.length) {
        //    throw new IllegalArgumentException("length of columns and values must be the same");
        //}
        return true;
    }

    /**
     * value 存在，强制更新使用value里面的值，
     * value 不存在，更新entity最新值。
     */
    //public boolean hasValues() {
    //    return values != null;
    //}

    public Object getValue(String key) {
        return map.get(key);
    }
}
