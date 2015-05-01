package com.litesuits.orm.db.utils;

import android.database.Cursor;
import com.litesuits.android.log.Log;
import com.litesuits.orm.db.assit.Checker;
import com.litesuits.orm.db.model.EntityTable;
import com.litesuits.orm.db.model.Property;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Date;

/**
 * SQLite支持的数据类型
 * 类型描述摘自sqlite官网 http://sqlite.org/datatype3.html
 *
 * @author mty
 * @date 2013-6-10下午5:28:10
 */
public class DataUtil implements Serializable {
    public static final String TAG = DataUtil.class.getSimpleName();
    private static final long serialVersionUID = 6668874253056236676L;
    /**
     * NULL. The value is a NULL value.
     */
    public static final String NULL = " NULL ";
    /**
     * INTEGER. The value is a signed integer, stored in 1, 2, 3, 4, 6, or 8
     * bytes depending on the magnitude of the value.
     */
    public static final String INTEGER = " INTEGER ";
    /**
     * REAL. The value is a floating point value, stored as an 8-byte IEEE
     * floating point number.
     */
    public static final String REAL = " REAL ";
    /**
     * TEXT. The value is a text string, stored using the database encoding
     * (UTF-8, UTF-16BE or UTF-16LE).
     */
    public static final String TEXT = " TEXT ";
    /**
     * BLOB. The value is a blob of data, stored exactly as it was input.
     */
    public static final String BLOB = " BLOB ";

    /**
     * Value returned by {@link #getType(Object)} if the specified column is null
     */
    public static final int FIELD_TYPE_NULL = 0;

    /**
     * Value returned by {@link #getType(Object)} if the specified column type is
     * integer or long
     */
    public static final int FIELD_TYPE_LONG = 1;

    /**
     * Value returned by {@link #getType(Object)} if the specified column type is
     * float or double
     */
    public static final int FIELD_TYPE_REAL = 2;

    /**
     * Value returned by {@link #getType(Object)} if the specified column type is
     * string
     */
    public static final int FIELD_TYPE_STRING = 3;

    /**
     * Value returned by {@link #getType(Object)} if the specified column type is
     * blob
     */
    public static final int FIELD_TYPE_BLOB = 4;

    /**
     * Value returned by {@link #getType(Object)} if the specified column type is
     * date
     */
    public static final int FIELD_TYPE_DATE = 5;

    /**
     * Value returned by {@link #getType(Object)} if the specified column type is
     * serializable
     */
    public static final int FIELD_TYPE_SERIALIZABLE = 6;

    /**
     * Returns data type of the given object.
     * <p>
     * Returned column types are
     * <ul>
     * <li>{@link #FIELD_TYPE_NULL}</li>
     * <li>{@link #FIELD_TYPE_LONG}</li>
     * <li>{@link #FIELD_TYPE_REAL}</li>
     * <li>{@link #FIELD_TYPE_STRING}</li>
     * <li>{@link #FIELD_TYPE_BLOB}</li>
     * <li>{@link #FIELD_TYPE_DATE}</li>
     * <li>{@link #FIELD_TYPE_SERIALIZABLE}</li>
     * </ul>
     * </p>
     */
    public static int getType(Object obj) {
        if (obj == null) {
            return FIELD_TYPE_NULL;
        } else if (obj instanceof CharSequence || obj instanceof Boolean || obj instanceof Character) {
            return FIELD_TYPE_STRING;
        } else if (obj instanceof Float || obj instanceof Double) {
            return FIELD_TYPE_REAL;
        } else if (obj instanceof Number) {
            return FIELD_TYPE_LONG;
        } else if (obj instanceof Date) {
            return FIELD_TYPE_DATE;
        } else if (obj instanceof byte[]) {
            return FIELD_TYPE_BLOB;
        } else if (obj instanceof Serializable) {
            return FIELD_TYPE_SERIALIZABLE;
        } else {
            return FIELD_TYPE_NULL;
        }
    }

    public static String getSQLDataType(Field f) {
        Class<?> type = f.getType();
        if (type == String.class) {
            return TEXT;
        } else if (type == boolean.class || type == Boolean.class) {
            return TEXT;
        } else if (type == double.class || type == Double.class) {
            return REAL;
        } else if (type == float.class || type == Float.class) {
            return REAL;
        } else if (type == long.class || type == Long.class) {
            return INTEGER;
        } else if (type == int.class || type == Integer.class) {
            return INTEGER;
        } else if (type == short.class || type == Short.class) {
            return INTEGER;
        } else if (type == byte.class || type == Byte.class) {
            return INTEGER;
        } else if (type == byte[].class || type == Byte[].class) {
            return BLOB;
        } else if (type == char.class || type == Character.class) {
            return TEXT;
        } else if (type == Date.class) {
            return INTEGER;
        } else if (Serializable.class.isAssignableFrom(f.getType())) {
            return BLOB;
        } else {
            return TEXT;
        }
    }

    /**
     * 将Cursor的数据注入模型
     * 支持11种基本类型，见{@link ClassUtil#isBaseDataType(Class)} ()}
     * 同时支持序列化对象
     *
     * @param c      数据库Cursor
     * @param entity 实体对象
     */
    public static void injectDataToObject(Cursor c, Object entity, EntityTable table) throws Exception {
        Field f;
        Property p;
        for (int i = 0, size = c.getColumnCount(); i < size; i++) {
            String col = c.getColumnName(i);
            p = null;
            if (!Checker.isEmpty(table.pmap)) {
                p = table.pmap.get(col);
            }
            if (p == null && table.key != null && col.equals(table.key.column)) {
                p = table.key;
            }
            if (p == null) {
                if (Log.isPrint)
                    Log.w(TAG, "数据库字段[" + col + "]已在实体中被移除");
                continue;
            }
            f = p.field;
            f.setAccessible(true);
            Class<?> type = f.getType();
            if (type == String.class) {
                f.set(entity, c.getString(i));
            } else if (type == boolean.class || type == Boolean.class) {
                f.set(entity, Boolean.parseBoolean(c.getString(i)));
            } else if (type == double.class || type == Double.class) {
                f.set(entity, c.getDouble(i));
            } else if (type == float.class || type == Float.class) {
                f.set(entity, c.getFloat(i));
            } else if (type == long.class || type == Long.class) {
                f.set(entity, c.getLong(i));
            } else if (type == int.class || type == Integer.class) {
                f.set(entity, c.getInt(i));
            } else if (type == short.class || type == Short.class) {
                f.set(entity, c.getShort(i));
            } else if (type == byte.class || type == Byte.class) {
                if (c.getString(i) != null) {
                    f.set(entity, Byte.parseByte(c.getString(i)));
                }
            } else if (type == byte[].class || type == Byte[].class) {
                f.set(entity, c.getBlob(i));
            } else if (type == char.class || type == Character.class) {
                String value = c.getString(i);
                if (!Checker.isEmpty(value)) {
                    f.set(entity, value.charAt(0));
                }
            } else if (type == Date.class) {
                f.set(entity, new Date(c.getLong(i)));
                //            } else if (c.getType(i) == Cursor.FIELD_TYPE_BLOB) {
            } else {
                byte[] bytes = c.getBlob(i);
                if (bytes != null) {
                    //序列化的对象
                    f.set(entity, byteToObject(bytes));
                }
            }
        }
    }

    /**
     * byte[] 转为 对象
     */
    public static Object byteToObject(byte[] bytes) throws Exception {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
            return ois.readObject();
        } finally {
            if (ois != null)
                ois.close();
        }
    }

    /**
     * 对象 转为 byte[]
     */
    public static byte[] objectToByte(Object obj) throws IOException {
        ObjectOutputStream oos = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            return bos.toByteArray();
        } finally {
            if (oos != null)
                oos.close();
        }
    }

}
