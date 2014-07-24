package com.litesuits.orm.db.assit;

import com.litesuits.orm.db.impl.SQLStatement;
import com.litesuits.orm.db.utils.TableUtil;

import java.util.regex.Pattern;

/**
 * 查询构建
 * 
 * @author mty
 * @date 2013-6-14下午3:47:16
 */
public class QueryBuilder {
	private static final Pattern limitPattern = Pattern.compile("\\s*\\d+\\s*(,\\s*\\d+\\s*)?");
	private Class<?> clazz;
	private Class<?> clazzMapping;
	private boolean distinct;
	private String where;
	private String[] columns;
	private String group;
	private String having;
	private String order;
	private String limit;
	private String[] whereArgs;

	public QueryBuilder(Class<?> claxx) {
		this.clazz = claxx;
	}

	public static QueryBuilder get(Class<?> claxx) {
		QueryBuilder builder = new QueryBuilder(claxx);
		return builder;
	}

	public QueryBuilder where(String where, String[] whereArgs) {
		this.where = where;
		this.whereArgs = whereArgs;
		return this;
	}

	/**
	 * 需要返回的列，不填写默认全部，即select * 。
	 * 
	 * @param columns
	 * 列名,注意不是对象的属性名。
	 * @return
	 */
	public QueryBuilder columns(String[] columns) {
		this.columns = columns;
		return this;
	}

	/**
	 * 累积需要返回的列，不填写默认全部，即select * 。
	 * @param columns 列名,注意不是对象的属性名。
	 * @return
	 */
	public QueryBuilder appendColumns(String[] columns) {
		int oldSize = this.columns == null ? 0 : this.columns.length;
		int newsize = columns == null ? 0 : columns.length;
		String[] newCol = new String[oldSize + newsize];

		System.arraycopy(this.columns, 0, newCol, 0, oldSize);
		System.arraycopy(columns, 0, newCol, oldSize, newsize);

		this.columns = newCol;
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
		if (Checker.isEmpty(group) && !Checker.isEmpty(having)) { throw new IllegalArgumentException(
				"HAVING仅允许在有GroupBy的时候使用(HAVING clauses are only permitted when using a groupBy clause)"); }
		if (!Checker.isEmpty(limit) && !limitPattern.matcher(limit).matches()) { throw new IllegalArgumentException(
				"invalid LIMIT clauses:" + limit); }

		StringBuilder query = new StringBuilder(120);

		query.append("SELECT");
		if (distinct) {
			query.append(" DISTINCT ");
		}
		if (!Checker.isEmpty(columns)) {
			appendColumns(query, columns);
		} else {
			query.append(" * ");
		}
		query.append("FROM ");

		query.append(getTableName());
		appendClause(query, " WHERE ", where);
		appendClause(query, " GROUP BY ", group);
		appendClause(query, " HAVING ", having);
		appendClause(query, " ORDER BY ", order);
		appendClause(query, " LIMIT ", limit);

		SQLStatement stmt = new SQLStatement();
		stmt.sql = query.toString();
		stmt.bindArgs = whereArgs;
		return stmt;
	}
    private String getTableName(){
        if(clazzMapping == null)
            return TableUtil.getTableName(clazz);
        else
            return TableUtil.getMapTableName(clazz, clazzMapping);
    }
	/**
	 * Build a statement that returns a 1 by 1 table with a numeric value.
	 * SELECT COUNT(*) FROM table;
	 * @return
	 */
	public SQLStatement createStatementForCount() {
		SQLStatement stmt = new SQLStatement();
		stmt.sql = "SELECT COUNT(*) FROM " + getTableName();
		return stmt;
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
					s.append(", ");
				}
				s.append(column);
			}
		}
		s.append(' ');
	}
}
