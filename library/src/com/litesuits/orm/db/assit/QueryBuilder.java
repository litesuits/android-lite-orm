package com.litesuits.orm.db.assit;

import com.litesuits.orm.db.TableManager;
import com.litesuits.orm.db.impl.SQLStatement;

import java.util.regex.Pattern;

/**
 * 查询构建
 *
 * @author mty
 * @date 2013-6-14下午3:47:16
 */
public class QueryBuilder {
    private static final Pattern limitPattern = Pattern.compile("\\s*\\d+\\s*(,\\s*\\d+\\s*)?");

    public static final String ASC = " ASC";
    public static final String DESC = " DESC";
    public static final String AND = " AND ";
    public static final String OR = " OR ";


    public static final String GROUP_BY = " GROUP BY ";
    public static final String HAVING = " HAVING ";
    public static final String ORDER_BY = " ORDER BY ";
    public static final String LIMIT = " LIMIT ";
    public static final String SELECT_COUNT = "SELECT COUNT(*) FROM ";
    public static final String SELECT = "SELECT ";
    public static final String DISTINCT = " DISTINCT ";
    public static final String ASTERISK = "*";
    public static final String FROM = " FROM ";
    public static final String EQUAL_HOLDER = "=?";
    public static final String COMMA_HOLDER = ",?";

    private Class clazz;
    private Class clazzMapping;
    private boolean distinct;
    private String[] columns;
    //private String where;
    //private Object[] whereArgs;
    private String group;
    private String having;
    private String order;
    private String limit;
    private WhereBuilder whereBuilder = new WhereBuilder();

    public QueryBuilder() {
    }

    public Class getQueryClass() {
        return clazz;
    }

    public QueryBuilder(Class claxx) {
        queryWho(claxx);
    }

    public static QueryBuilder create(Class claxx) {
        return new QueryBuilder(claxx);
    }

    public static QueryBuilder get(Class claxx) {
        return create(claxx);
    }

    public QueryBuilder queryWho(Class claxx) {
        this.clazz = claxx;
        return this;
    }

    public QueryBuilder where(WhereBuilder builder) {
        this.whereBuilder = builder;
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
    public QueryBuilder where(String where, Object[] whereArgs) {
        whereBuilder.where(where, whereArgs);
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
     * @param connect     NULL or "AND" or "OR"
     * @return this
     */
    public QueryBuilder appendWhere(String connect, String whereString, Object... value) {
        whereBuilder.appendWhere(connect, whereString, value);
        return this;
    }

    /**
     * build as " column = ? "
     */
    public QueryBuilder setWhereEquals(String column, Object value) {
        return appendWhere(null, column + EQUAL_HOLDER, value);
    }

    /**
     * build as " or column = ? "
     */
    public QueryBuilder orWhereEquals(String column, Object value) {
        return appendWhere(OR, column + EQUAL_HOLDER, value);
    }

    /**
     * build as " and column = ? "
     */
    public QueryBuilder andWhereEquals(String column, Object[] value) {
        return appendWhere(AND, column + EQUAL_HOLDER, value);
    }

    /**
     * build as " column in(?,?...) "
     */
    public QueryBuilder setWhereIn(String column, Object[] value) {
        return appendWhere(null, buildWhereIn(column, value.length), value);
    }

    /**
     * build as " or column in(?,?...) "
     */
    public QueryBuilder orWhereIn(String column, Object[] value) {
        return appendWhere(OR, buildWhereIn(column, value.length), value);
    }

    /**
     * build as " and column in(?,?...) "
     */
    public QueryBuilder andWhereIn(String column, Object[] value) {
        return appendWhere(AND, buildWhereIn(column, value.length), value);
    }


    /**
     * 需要返回的列，不填写默认全部，即select * 。
     *
     * @param columns 列名,注意不是对象的属性名。
     * @return
     */
    public QueryBuilder columns(String[] columns) {
        this.columns = columns;
        return this;
    }

    /**
     * 累积需要返回的列，不填写默认全部，即select * 。
     *
     * @param columns 列名,注意不是对象的属性名。
     * @return
     */
    public QueryBuilder appendColumns(String[] columns) {
        if (this.columns != null) {
            String[] newCols = new String[this.columns.length + columns.length];
            System.arraycopy(this.columns, 0, newCols, 0, this.columns.length);
            System.arraycopy(columns, 0, newCols, this.columns.length, columns.length);
            this.columns = newCols;
        } else {
            this.columns = columns;
        }
        return this;
    }

    /**
     * 唯一性保证
     *
     * @return
     */
    public QueryBuilder distinct(boolean distinct) {
        this.distinct = distinct;
        return this;
    }

    /**
     * GROUP BY 语句用于结合合计函数，根据一个或多个列对结果集进行分组。
     *
     * @param group
     * @return
     */
    public QueryBuilder groupBy(String group) {
        this.group = group;
        return this;
    }

    /**
     * 在 SQL 中增加 HAVING 子句原因是，WHERE 关键字无法与合计函数一起使用。
     *
     * @param having
     * @return
     */
    public QueryBuilder having(String having) {
        this.having = having;
        return this;
    }

    public QueryBuilder orderBy(String order) {
        this.order = order;
        return this;
    }


    public QueryBuilder appendOrderAscBy(String column) {
        if (order == null) {
            order = column + ASC;
        } else {
            order += ", " + column + ASC;
        }
        return this;
    }

    public QueryBuilder appendOrderDescBy(String column) {
        if (order == null) {
            order = column + DESC;
        } else {
            order += ", " + column + DESC;
        }
        return this;
    }

    public QueryBuilder limit(String limit) {
        this.limit = limit;
        return this;
    }

    public QueryBuilder queryMappingInfo(Class clazzMapping) {
        this.clazzMapping = clazzMapping;
        return this;
    }


    /**
     * 构建查询语句
     *
     * @return
     */
    public SQLStatement createStatement() {
        if (clazz == null) {
            throw new IllegalArgumentException("U Must Set A Query Entity Class By queryWho(Class) or " +
                    "QueryBuilder(Class)");
        }
        if (Checker.isEmpty(group) && !Checker.isEmpty(having)) {
            throw new IllegalArgumentException(
                    "HAVING仅允许在有GroupBy的时候使用(HAVING clauses are only permitted when using a groupBy clause)");
        }
        if (!Checker.isEmpty(limit) && !limitPattern.matcher(limit).matches()) {
            throw new IllegalArgumentException(
                    "invalid LIMIT clauses:" + limit);
        }

        StringBuilder query = new StringBuilder(120);

        query.append(SELECT);
        if (distinct) {
            query.append(DISTINCT);
        }
        if (!Checker.isEmpty(columns)) {
            appendColumns(query, columns);
        } else {
            query.append(ASTERISK);
        }
        query.append(FROM).append(getTableName()).append(whereBuilder.createWhereString(clazz));

        appendClause(query, GROUP_BY, group);
        appendClause(query, HAVING, having);
        appendClause(query, ORDER_BY, order);
        appendClause(query, LIMIT, limit);

        SQLStatement stmt = new SQLStatement();
        stmt.sql = query.toString();
        stmt.bindArgs = whereBuilder.transToStringArray();
        return stmt;
    }

    /**
     * Build a statement that returns a 1 by 1 table with a numeric value.
     * SELECT COUNT(*) FROM table;
     *
     * @return
     */
    public SQLStatement createStatementForCount() {
        StringBuilder query = new StringBuilder(120);
        query.append(SELECT_COUNT).append(getTableName()).append(whereBuilder.createWhereString(clazz));

        SQLStatement stmt = new SQLStatement();
        stmt.sql = query.toString();
        stmt.bindArgs = whereBuilder.transToStringArray();
        return stmt;
    }

    private String getTableName() {
        if (clazzMapping == null) {
            return TableManager.getTableName(clazz);
        } else {
            return TableManager.getMapTableName(clazz, clazzMapping);
        }
    }

    /**
     * 添加条件
     *
     * @param s
     * @param name
     * @param clause
     */
    private static void appendClause(StringBuilder s, String name, String clause) {
        if (!Checker.isEmpty(clause)) {
            s.append(name);
            s.append(clause);
        }
    }

    /**
     * 添加列，逗号分隔
     *
     * @param s
     * @param columns
     */
    private static void appendColumns(StringBuilder s, String[] columns) {
        int n = columns.length;

        for (int i = 0; i < n; i++) {
            String column = columns[i];

            if (column != null) {
                if (i > 0) {
                    s.append(",");
                }
                s.append(column);
            }
        }
        s.append(" ");
    }


    private String buildWhereIn(String column, int num) {
        StringBuilder sb = new StringBuilder(column).append(" IN (?");
        for (int i = 1; i < num; i++) {
            sb.append(COMMA_HOLDER);

        }
        return sb.append(")").toString();
    }

}
