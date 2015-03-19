package com.litesuits.orm.db.assit;

import com.litesuits.orm.db.TableManager;
import com.litesuits.orm.db.impl.SQLStatement;

/**
 * @author MaTianyu
 * @date 2015-03-18
 */
public class WhereBuilder {
    public static final String BLANK = " ";
    public static final String NOTHING = "";
    public static final String WHERE = " WHERE ";
    public static final String EQUAL_HOLDER = "=?";
    public static final String COMMA_HOLDER = ",?";
    public static final String AND = " AND ";
    public static final String OR = " OR ";
    public static final String DELETE = "DELETE FROM ";

    private String where;
    private Object[] whereArgs;

    public WhereBuilder() {
    }

    public static WhereBuilder create() {
        return new WhereBuilder();
    }

    public static WhereBuilder create(String where, Object[] whereArgs) {
        return new WhereBuilder(where, whereArgs);
    }

    public WhereBuilder(String where, Object[] whereArgs) {
        this.where = where;
        this.whereArgs = whereArgs;
    }

    /**
     * @param where     "id = ?";
     *                  "id in(?,?,?)";
     *                  "id LIKE %?"
     * @param whereArgs new String[]{"",""};
     *                  new Integer[]{1,2}
     * @return
     */
    public WhereBuilder where(String where, Object[] whereArgs) {
        this.where = where;
        this.whereArgs = whereArgs;
        return this;
    }

    /**
     * @param whereString "id = ?";
     *                    or "id in(?,?,?)";
     *                    or "id LIKE %?";
     *                    ...
     * @param value       new String[]{"",""};
     *                    or new Integer[]{1,2};
     *                    ...
     * @param connect     NULL or " AND " or " OR "
     * @return this
     */
    public WhereBuilder appendWhere(String connect, String whereString, Object... value) {
        if (where == null || connect == null) {
            where = whereString;
            whereArgs = value;
        } else {
            where += connect + whereString;
            Object[] newWhere = new Object[whereArgs.length + value.length];
            System.arraycopy(whereArgs, 0, newWhere, 0, whereArgs.length);
            System.arraycopy(value, 0, newWhere, whereArgs.length, value.length);
            this.whereArgs = newWhere;
        }
        return this;
    }

    /**
     * build as " column = ? "
     */
    public WhereBuilder setWhereEquals(String column, Object value) {
        return appendWhere(null, column + EQUAL_HOLDER, value);
    }

    /**
     * build as " or column = ? "
     */
    public WhereBuilder orWhereEquals(String column, Object value) {
        return appendWhere(OR, column + EQUAL_HOLDER, value);
    }

    /**
     * build as " and column = ? "
     */
    public WhereBuilder andWhereEquals(String column, Object value) {
        return appendWhere(AND, column + EQUAL_HOLDER, value);
    }

    /**
     * build as " column in(?,?...) "
     */
    public WhereBuilder setWhereIn(String column, Object[] value) {
        where = null;
        whereArgs = null;
        return appendWhere(null, buildWhereIn(column, value.length), value);
    }

    /**
     * build as " or column in(?,?...) "
     */
    public WhereBuilder orWhereIn(String column, Object[] value) {
        return appendWhere(OR, buildWhereIn(column, value.length), value);
    }

    /**
     * build as " and column in(?,?...) "
     */
    public WhereBuilder andWhereIn(String column, Object[] value) {
        return appendWhere(AND, buildWhereIn(column, value.length), value);
    }

    public String[] transToStringArray() {
        if (whereArgs != null && whereArgs.length > 0) {
            if (whereArgs instanceof String[]) {
                return (String[]) whereArgs;
            }
            String[] arr = new String[whereArgs.length];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = String.valueOf(whereArgs[i]);
            }
            return arr;
        }
        return null;
    }


    public String createWhereString(Class claxx) {
        if (where != null) {
            return WHERE + where;
        } else {
            return NOTHING;
        }
    }

    public SQLStatement createStatementDelete(Class claxx) {
        SQLStatement stmt = new SQLStatement();
        stmt.sql = DELETE + TableManager.getTableName(claxx) + createWhereString(claxx);
        stmt.bindArgs = transToStringArray();
        return stmt;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public Object[] getWhereArgs() {
        return whereArgs;
    }

    public void setWhereArgs(Object[] whereArgs) {
        this.whereArgs = whereArgs;
    }

    private String buildWhereIn(String column, int num) {
        StringBuilder sb = new StringBuilder(column).append(" IN (?");
        for (int i = 1; i < num; i++) {
            sb.append(COMMA_HOLDER);

        }
        return sb.append(") ").toString();
    }
}
