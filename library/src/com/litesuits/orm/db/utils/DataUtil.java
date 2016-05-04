package com.litesuits.orm.db.utils;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.os.Build;
import android.util.Log;
import com.litesuits.orm.log.OrmLog;
import com.litesuits.orm.db.assit.Checker;
import com.litesuits.orm.db.model.EntityTable;
import com.litesuits.orm.db.model.Property;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

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


    public static final int CLASS_TYPE_NONE = 0;
    public static final int CLASS_TYPE_STRING = 1;
    public static final int CLASS_TYPE_BOOLEAN = 2;
    public static final int CLASS_TYPE_DOUBLE = 3;
    public static final int CLASS_TYPE_FLOAT = 4;
    public static final int CLASS_TYPE_LONG = 5;
    public static final int CLASS_TYPE_INT = 6;
    public static final int CLASS_TYPE_SHORT = 7;
    public static final int CLASS_TYPE_BYTE = 8;
    public static final int CLASS_TYPE_BYTE_ARRAY = 9;
    public static final int CLASS_TYPE_CHAR = 10;
    public static final int CLASS_TYPE_DATE = 11;
    public static final int CLASS_TYPE_SERIALIZABLE = 12;
    public static final int CLASS_TYPE_UNKNOWN = 13;


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

    public static String getSQLDataType(int classType) {
        switch (classType) {
            case CLASS_TYPE_STRING:
            case CLASS_TYPE_BOOLEAN:
            case CLASS_TYPE_CHAR:
                return TEXT;
            case CLASS_TYPE_DOUBLE:
            case CLASS_TYPE_FLOAT:
                return REAL;
            case CLASS_TYPE_LONG:
            case CLASS_TYPE_INT:
            case CLASS_TYPE_SHORT:
            case CLASS_TYPE_BYTE:
            case CLASS_TYPE_DATE:
                return INTEGER;
            case CLASS_TYPE_BYTE_ARRAY:
            case CLASS_TYPE_SERIALIZABLE:
            default:
                return BLOB;
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
            //long start = System.nanoTime();

            String col = c.getColumnName(i);
            p = null;
            if (!Checker.isEmpty(table.pmap)) {
                p = table.pmap.get(col);
            }
            if (p == null && table.key != null && col.equals(table.key.column)) {
                p = table.key;
            }
            if (p == null) {
                if (OrmLog.isPrint) {
                    OrmLog.w(TAG, "数据库字段[" + col + "]已在实体中被移除");
                }
                continue;
            }
            f = p.field;
            f.setAccessible(true);
            //Log.i(TAG, "parse pre after  " + ((System.nanoTime() - start) / 1000));
            //start = System.nanoTime();
            switch (p.classType) {
                case CLASS_TYPE_STRING:
                    f.set(entity, c.getString(i));
                    break;
                case CLASS_TYPE_BOOLEAN:
                    f.set(entity, Boolean.parseBoolean(c.getString(i)));
                    break;
                case CLASS_TYPE_LONG:
                    f.set(entity, c.getLong(i));
                    break;
                case CLASS_TYPE_INT:
                    f.set(entity, c.getInt(i));
                    break;
                case CLASS_TYPE_DOUBLE:
                    f.set(entity, c.getDouble(i));
                    break;
                case CLASS_TYPE_FLOAT:
                    f.set(entity, c.getFloat(i));
                    break;
                case CLASS_TYPE_SHORT:
                    f.set(entity, c.getShort(i));
                    break;
                case CLASS_TYPE_BYTE:
                    if (c.getString(i) != null) {
                        f.set(entity, Byte.parseByte(c.getString(i)));
                    }
                    break;
                case CLASS_TYPE_BYTE_ARRAY:
                    f.set(entity, c.getBlob(i));
                    break;
                case CLASS_TYPE_CHAR:
                    String value = c.getString(i);
                    if (!Checker.isEmpty(value)) {
                        f.set(entity, value.charAt(0));
                    }
                    break;
                case CLASS_TYPE_DATE:
                    Long time = c.getLong(i);
                    if (time != null) {
                        f.set(entity, new Date(time));
                    }
                    break;
                case CLASS_TYPE_SERIALIZABLE:
                    byte[] bytes = c.getBlob(i);
                    if (bytes != null) {
                        //序列化的对象
                        f.set(entity, byteToObject(bytes));
                    }
                    break;
                default:
                    break;
            }
            //Log.i(TAG, "parse set after  " + ((System.nanoTime() - start) / 1000));
        }
    }

    public static int getFieldClassType(Field f) {
        Class type = f.getType();
        if (CharSequence.class.isAssignableFrom(type)) {
            return CLASS_TYPE_STRING;
        } else if (boolean.class.isAssignableFrom(type) || Boolean.class.isAssignableFrom(type)) {
            return CLASS_TYPE_BOOLEAN;
        } else if (double.class.isAssignableFrom(type) || Double.class.isAssignableFrom(type)) {
            return CLASS_TYPE_DOUBLE;
        } else if (float.class.isAssignableFrom(type) || Float.class.isAssignableFrom(type)) {
            return CLASS_TYPE_FLOAT;
        } else if (long.class.isAssignableFrom(type) || Long.class.isAssignableFrom(type)) {
            return CLASS_TYPE_LONG;
        } else if (int.class.isAssignableFrom(type) || Integer.class.isAssignableFrom(type)) {
            return CLASS_TYPE_INT;
        } else if (short.class.isAssignableFrom(type) || Short.class.isAssignableFrom(type)) {
            return CLASS_TYPE_SHORT;
        } else if (byte.class.isAssignableFrom(type) || Byte.class.isAssignableFrom(type)) {
            return CLASS_TYPE_BYTE;
        } else if (byte[].class.isAssignableFrom(type) || Byte[].class.isAssignableFrom(type)) {
            return CLASS_TYPE_BYTE_ARRAY;
        } else if (char.class.isAssignableFrom(type) || Character.class.isAssignableFrom(type)) {
            return CLASS_TYPE_CHAR;
        } else if (Date.class.isAssignableFrom(type)) {
            return CLASS_TYPE_DATE;
        } else if (Serializable.class.isAssignableFrom(type)) {
            return CLASS_TYPE_SERIALIZABLE;
        }
        return CLASS_TYPE_UNKNOWN;
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


    public static List<?> arrayToList(Object[] array) {
        return Arrays.asList(array);
    }

    public static Object[] arrayToList(Collection<?> coll) {
        return coll.toArray();
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static <T> T[] concatAll(T[] first, T[]... rest) {
        int totalLength = first.length;
        for (T[] array : rest) {
            totalLength += array.length;
        }
        T[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (T[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

}
