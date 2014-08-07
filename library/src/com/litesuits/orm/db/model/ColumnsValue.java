package com.litesuits.orm.db.model;

/**
 * help to build custom column value
 * {@link #values}可为NULL，但若不为NULL则和{@link #columns}一一对应，数组大小必须一致。
 * {@link #columns}不能为NULL，你的SQL语句仅仅对在这个数组里面的列起作用。比如update，只更新这里面的列。
 * 如果{@link #values}非NULL，那么你的SQL将永远使用这里面的值，设置给对应列。比如update，这里的值优先被写入数据库。
 *
 * @author MaTianyu
 *         2014-8-7
 */
public class ColumnsValue {

    /**
     * your sql only affect column in {@link #columns}.
     */
    public String[] columns;

    /**
     * can be null, if not this will mapping with {@link #columns} 1 by 1.
     * if not null, your columns well aways set value in {@link #values}.
     */
    public Object[] values;

    public ColumnsValue(String[] columns) {
        this.columns = columns;
    }

    public ColumnsValue(String[] columns, Object[] values) {
        this.columns = columns;
        this.values = values;
    }

    public boolean checkColumns() {
        if (columns == null) {
            throw new IllegalArgumentException("columns must not be null");
        } else if (values != null && columns.length != values.length) {
            throw new IllegalArgumentException("length of columns and values must be the same");
        }
        return true;
    }

    public boolean hasValues() {
        return values != null;
    }
}
