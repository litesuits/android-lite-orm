package com.litesuits.orm.db.model;

import java.io.Serializable;
import java.util.List;

/**
 * 表结构，SQLite中的每一张表都有这样的属性。
 * 
 * @author mty
 * @date 2013-6-2下午11:17:40
 */
public class SQLiteTable implements Serializable {
	private static final long serialVersionUID = 6706520684759700566L;
	public static final String NAME = "name";
	public String type;

	public String name;
	public String tbl_name;
	public long rootpage;
	public String sql;
	public boolean isTableChecked;
	public List<String> columns;

	@Override
	public String toString() {
		return "Tables [type=" + type + ", name=" + name + ", tbl_name=" + tbl_name + ", rootpage=" + rootpage
				+ ", sql=" + sql + ", columns=" + columns + "]";
	}

}
