package com.litesuits.orm.db.assit;

import com.litesuits.orm.db.TableManager;

import java.util.regex.Pattern;

/**
 * 查询构建
 *
 * @author mty
 * @date 2013-6-14下午3:47:16
 */
public class QueryBuilder<T> {
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
    public static final String COMMA = ",";

    protected Class<T> clazz;
    protected Class clazzMapping;
    protected boolean distinct;
    protected String[] columns;
    protected String group;
    protected String having;
    protected String order;
    protected String limit;
    protected WhereBuilder whereBuilder;

    public Class<T> getQueryClass() {
        return clazz;
    }

    public QueryBuilder(Class<T> claxx) {
        this.clazz = claxx;
        whereBuilder = new WhereBuilder(claxx);
    }

    public static <T> QueryBuilder<T> create(Class<T> claxx) {
        return new QueryBuilder<T>(claxx);
    }

    public QueryBuilder<T> where(WhereBuilder builder) {
        this.whereBuilder = builder;
        return this;
    }

    public WhereBuilder getwhereBuilder() {
        return whereBuilder;
    }

    /**
     * @param where     "id = ?";
     *                  "id in(?,?,?)";
     *                  "id LIKE %?"
     * @param whereArgs new String[]{"",""};
     *                  new Integer[]{1,2}
     */
    public QueryBuilder<T> where(String where, Object... whereArgs) {
        whereBuilder.where(where, whereArgs);
        return this;
    }

    /**
     * @param where     "id = ?";
     *                  "id in(?,?,?)";
     *                  "id LIKE %?"
     * @param whereArgs new String[]{"",""};
     *                  new Integer[]{1,2}
     */
    public QueryBuilder<T> whereAppend(String where, Object... whereArgs) {
        whereBuilder.append(null, where, whereArgs);
        return this;
    }

    /**
     * build as " AND " + where
     *
     * @param where     "id = ?";
     *                  "id in(?,?,?)";
     *                  "id LIKE %?"
     * @param whereArgs new String[]{"",""};
     *                  new Integer[]{1,2}
     */
    public QueryBuilder<T> whereAnd(String where, Object... whereArgs) {
        whereBuilder.and(where, whereArgs);
        return this;
    }

    /**
     * build as " OR " + where
     *
     * @param where     "id = ?";
     *                  "id in(?,?,?)";
     *                  "id LIKE %?"
     * @param whereArgs new String[]{"",""};
     *                  new Integer[]{1,2}
     */
    public QueryBuilder<T> whereOr(String where, Object... whereArgs) {
        whereBuilder.or(where, whereArgs);
        return this;
    }

    /**
     * build as where+" AND "
     */
    public QueryBuilder<T> whereAppendAnd() {
        whereBuilder.and();
        return this;
    }

    /**
     * build as where+" OR "
     */
    public QueryBuilder<T> whereAppendOr() {
        whereBuilder.or();
        return this;
    }

    /**
     * build as where+" NOT "
     */
    public QueryBuilder<T> whereAppendNot() {
        whereBuilder.not();
        return this;
    }

    /**
     * build as where+" column != ? "
     */
    public QueryBuilder<T> whereNoEquals(String column, Object value) {
        whereBuilder.noEquals(column, value);
        return this;
    }

    /**
     * build as where+" column > ? "
     */
    public QueryBuilder<T> whereGreaterThan(String column, Object value) {
        whereBuilder.greaterThan(column, value);
        return this;
    }

    /**
     * build as where+" column < ? "
     */
    public QueryBuilder<T> whereLessThan(String column, Object value) {
        whereBuilder.lessThan(column, value);
        return this;
    }

    /**
     * build as where+" column = ? "
     */
    public QueryBuilder<T> whereEquals(String column, Object value) {
        whereBuilder.equals(column, value);
        return this;
    }

    /**
     * build as where+" column IN(?, ?, ?...)"
     */
    public QueryBuilder<T> whereIn(String column, Object... values) {
        whereBuilder.in(column, values);
        return this;
    }

    /**
     * 需要返回的列，不填写默认全部，即select * 。
     *
     * @param columns 列名,注意不是对象的属性名。
     */
    public QueryBuilder<T> columns(String[] columns) {
        this.columns = columns;
        return this;
    }

    /**
     * 累积需要返回的列，不填写默认全部，即select * 。
     *
     * @param columns 列名,注意不是对象的属性名。
     */
    public QueryBuilder<T> appendColumns(String[] columns) {
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
     */
    public QueryBuilder<T> distinct(boolean distinct) {
        this.distinct = distinct;
        return this;
    }

    /**
     * GROUP BY 语句用于结合合计函数，根据一个或多个列对结果集进行分组。
     */
    public QueryBuilder<T> groupBy(String group) {
        this.group = group;
        return this;
    }

    /**
     * 在 SQL 中增加 HAVING 子句原因是，WHERE 关键字无法与合计函数一起使用。
     */
    public QueryBuilder<T> having(String having) {
        this.having = having;
        return this;
    }

    public QueryBuilder<T> orderBy(String order) {
        this.order = order;
        return this;
    }


    public QueryBuilder<T> appendOrderAscBy(String column) {
        if (order == null) {
            order = column + ASC;
        } else {
            order += ", " + column + ASC;
        }
        return this;
    }

    public QueryBuilder<T> appendOrderDescBy(String column) {
        if (order == null) {
            order = column + DESC;
        } else {
            order += ", " + column + DESC;
        }
        return this;
    }

    public QueryBuilder<T> limit(String limit) {
        this.limit = limit;
        return this;
    }

    public QueryBuilder<T> limit(int start, int length) {
        this.limit = start + COMMA + length;
        return this;
    }

    public QueryBuilder<T> queryMappingInfo(Class clazzMapping) {
        this.clazzMapping = clazzMapping;
        return this;
    }


    /**
     * 构建查询语句
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
        query.append(FROM).append(getTableName());

        query.append(whereBuilder.createWhereString());

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
     */
    public SQLStatement createStatementForCount() {
        StringBuilder query = new StringBuilder(120);
        query.append(SELECT_COUNT).append(getTableName());
        SQLStatement stmt = new SQLStatement();
        if (whereBuilder != null) {
            query.append(whereBuilder.createWhereString());
            stmt.bindArgs = whereBuilder.transToStringArray();
        }
        stmt.sql = query.toString();
        return stmt;
    }

    public String getTableName() {
        if (clazzMapping == null) {
            return TableManager.getTableName(clazz);
        } else {
            return TableManager.getMapTableName(clazz, clazzMapping);
        }
    }

    /**
     * 添加条件
     */
    private static void appendClause(StringBuilder s, String name, String clause) {
        if (!Checker.isEmpty(clause)) {
            s.append(name);
            s.append(clause);
        }
    }

    /**
     * 添加列，逗号分隔
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
