package com.litesuits.orm.kvdb;

import java.util.List;

/**
 * 数据操作定义
 * 
 * @author mty
 * @date 2013-6-2上午2:37:56
 */
public interface DataCache<K, V> {
	
	public Object save(K key, V data);

	public Object delete(K key);

	public Object update(K key, V data);

	public List<V> query(String arg);
}
