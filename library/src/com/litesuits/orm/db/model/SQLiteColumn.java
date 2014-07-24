package com.litesuits.orm.db.model;

import java.io.Serializable;


/**
 * 列结构
 * @author mty
 * @date 2013-6-7上午1:17:49
 */
public class SQLiteColumn implements Serializable{
	private static final long serialVersionUID = 8822000632819424751L;
	public long cid;
	public String name;
	public String type;
	public short notnull;
	public String dflt_value;
	public short pk;
	
	@Override
	public String toString() {
		return "Column [cid=" + cid + ", name=" + name + ", type=" + type + ", notnull=" + notnull + ", dflt_value="
				+ dflt_value + ", pk=" + pk + "]";
	}
	
}
