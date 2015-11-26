package com.litesuits.orm.db.assit;

import com.litesuits.orm.db.TableManager;

/**
 * @author MaTianyu
 * @date 2015-03-18
 */
public class WhereBuilder {
    public static final String NOTHING = "";
    public static final String WHERE = " WHERE ";
    public static final String EQUAL_HOLDER = "=?";
    public static final String NOT_EQUAL_HOLDER = "!=?";
    public static final String GREATER_THAN_HOLDER = ">?";
    public static final String LESS_THAN_HOLDER = "<?";
    public static final String COMMA_HOLDER = ",?";
    public static final String HOLDER = "?";
    public static final String AND = " AND ";
    public static final String OR = " OR ";
    public static final String NOT = " NOT ";
    public static final String DELETE = "DELETE FROM ";
    private static final String PARENTHESES_LEFT = "(";
    private static final String PARENTHESES_RIGHT = ")";
    private static final String IN = " IN ";

    protected String where;
    protected Object[] whereArgs;
    protected Class tableClass;

    public WhereBuilder(Class tableClass) {
        this.tableClass = tableClass;
    }

    public static WhereBuilder create(Class tableClass) {
        return new WhereBuilder(tableClass);
    }

    public static WhereBuilder create(Class tableClass, String where, Object[] whereArgs) {
        return new WhereBuilder(tableClass, where, whereArgs);
    }

    public WhereBuilder(Class tableClass, String where, Object[] whereArgs) {
        this.where = where;
        this.whereArgs = whereArgs;
        this.tableClass = tableClass;
    }

    public Class getTableClass() {
        return tableClass;
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
     * @param where     "id = ?";
     *                  "id in(?,?,?)";
     *                  "id LIKE %?"
     * @param whereArgs new String[]{"",""};
     *                  new Integer[]{1,2}
     * @return
     */
    public WhereBuilder and(String where, Object[] whereArgs) {
        return append(AND, where, whereArgs);
    }

    /**
     * @param where     "id = ?";
     *                  "id in(?,?,?)";
     *                  "id LIKE %?"
     * @param whereArgs new String[]{"",""};
     *                  new Integer[]{1,2}
     * @return
     */
    public WhereBuilder or(String where, Object[] whereArgs) {
        return append(OR, where, whereArgs);
    }

    /**
     * build as " AND "
     */
    public WhereBuilder and() {
        if (where != null) {
            where += AND;
        }
        return this;
    }

    /**
     * build as " OR "
     */
    public WhereBuilder or() {
        if (where != null) {
            where += OR;
        }
        return this;
    }

    /**
     * build as " NOT "
     */
    public WhereBuilder not() {
        if (where != null) {
            where += NOT;
        }
        return this;
    }

    /**
     * build as " column != ? "
     */
    public WhereBuilder noEquals(String column, Object value) {
        return append(null, column + NOT_EQUAL_HOLDER, value);
    }

    /**
     * build as " column > ? "
     */
    public WhereBuilder greaterThan(String column, Object value) {
        return append(null, column + GREATER_THAN_HOLDER, value);
    }

    /**
     * build as " column < ? "
     */
    public WhereBuilder lessThan(String column, Object value) {
        return append(null, column + LESS_THAN_HOLDER, value);
    }

    /**
     * build as " column = ? "
     */
    public WhereBuilder equals(String column, Object value) {
        return append(null, column + EQUAL_HOLDER, value);
    }

    /**
     * build as " or column = ? "
     */
    public WhereBuilder orEquals(String column, Object value) {
        return append(OR, column + EQUAL_HOLDER, value);
    }

    /**
     * build as " and column = ? "
     */
    public WhereBuilder andEquals(String column, Object value) {
        return append(AND, column + EQUAL_HOLDER, value);
    }

    /**
     * build as " column in(?,?...) "
     */
    public WhereBuilder in(String column, Object[] values) {
        return append(null, buildWhereIn(column, values.length), values);
    }

    /**
     * build as " or column in(?,?...) "
     */
    public WhereBuilder orIn(String column, Object[] values) {
        return append(OR, buildWhereIn(column, values.length), values);
    }

    /**
     * build as " and column in(?,?...) "
     */
    public WhereBuilder andIn(String column, Object[] values) {
        return append(AND, buildWhereIn(column, values.length), values);
    }


    /**
     * @param whereString "id = ?";
     *                    or "id in(?,?,?)";
     *                    or "id LIKE %?";
     *                    ...
     * @param value       new String[]{"",""};
     *                    or new Integer[]{1,2};
     *                    ...
     * @param connect     NULL or " AND " or " OR " or " NOT "
     * @return this
     */
    public WhereBuilder append(String connect, String whereString, Object... value) {
        if (where == null) {
            where = whereString;
            whereArgs = value;
        } else {
            if (connect != null) {
                where += connect;
            }
            where += whereString;
            Object[] newWhere = new Object[whereArgs.length + value.length];
            System.arraycopy(whereArgs, 0, newWhere, 0, whereArgs.length);
            System.arraycopy(value, 0, newWhere, whereArgs.length, value.length);
            this.whereArgs = newWhere;
        }
        return this;
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


    public String createWhereString() {
        if (where != null) {
            return WHERE + where;
        } else {
            return NOTHING;
        }
    }

    public SQLStatement createStatementDelete() {
        SQLStatement stmt = new SQLStatement();
        stmt.sql = DELETE + TableManager.getTableName(tableClass) + createWhereString();
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
        StringBuilder sb = new StringBuilder(column).append(IN).append(PARENTHESES_LEFT).append(HOLDER);
        for (int i = 1; i < num; i++) {
            sb.append(COMMA_HOLDER);
        }
        return sb.append(PARENTHESES_RIGHT).toString();
    }
}
