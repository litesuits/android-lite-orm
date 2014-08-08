package com.litesuits.orm.db.assit;

import com.litesuits.orm.db.TableManager;
import com.litesuits.orm.db.annotation.*;
import com.litesuits.orm.db.annotation.PrimaryKey.AssignType;
import com.litesuits.orm.db.impl.SQLStatement;
import com.litesuits.orm.db.model.*;
import com.litesuits.orm.db.model.MapInfo.MapTable;
import com.litesuits.orm.db.utils.ClassUtil;
import com.litesuits.orm.db.utils.DataUtil;
import com.litesuits.orm.db.utils.FieldUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

public class SQLBuilder {

    private static final int INSERT  = 1;
    private static final int REPLACE = 2;
    //private static final int UPDATE  = 3;

    /**
     * 构建【获取SQLite全部表】sql语句
     *
     * @return
     */
    public static SQLStatement buildTableObtainAll() {
        return new SQLStatement("SELECT * FROM sqlite_master WHERE type='table' ORDER BY name", null);
    }

    /**
     * 构建【获取SQLite全部表】sql语句
     *
     * @return
     */
    public static SQLStatement buildColumnsObtainAll(String table) {
        return new SQLStatement("PRAGMA table_info([" + table + "])", null);
    }

    /**
     * 构建【获取最新插入的数据的主键】sql语句
     *
     * @return
     */
    public static SQLStatement buildGetLastRowId(EntityTable table) {
        return new SQLStatement("SELECT MAX(" + table.key.column + ") FROM " + table.name, null);
    }

    /**
     * 构建【表】sql语句
     *
     * @param table
     * @return
     */
    public static SQLStatement buildCreateTable(EntityTable table) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE ");
        if (table.getAnnotation(Temporary.class) != null) sb.append("TEMP ");
        sb.append("TABLE IF NOT EXISTS ").append(table.name).append(" ( ");
        boolean hasKey = false;
        if (table.key != null) {
            hasKey = true;
            if (table.key.assign == AssignType.AUTO_INCREMENT) {
                sb.append(table.key.column).append(" ").append(DataUtil.INTEGER)
                        .append(" PRIMARY KEY AUTOINCREMENT ");
            } else {
                sb.append(table.key.column).append(" PRIMARY KEY ");
            }
        }
        if (!Checker.isEmpty(table.pmap)) {
            boolean isF = true;
            for (Entry<String, Property> en : table.pmap.entrySet()) {
                if (isF) {
                    isF = false;
                    if (hasKey) sb.append(", ");
                } else {
                    sb.append(", ");
                }
                sb.append(en.getKey());
                if (en.getValue() == null) {
                    sb.append(" ").append(DataUtil.TEXT);
                } else {
                    Field f = en.getValue().field;
                    sb.append(" ");
                    sb.append(DataUtil.getSQLDataType(f));

                    if (f.getAnnotation(NotNull.class) != null) {
                        sb.append(" NOT NULL ");
                    }
                    if (f.getAnnotation(Default.class) != null) {
                        sb.append(" DEFAULT ");
                        sb.append(f.getAnnotation(Default.class).value());
                    }
                    if (f.getAnnotation(Unique.class) != null) {
                        sb.append(" UNIQUE ");
                    }
                    if (f.getAnnotation(Conflict.class) != null) {
                        sb.append(" ON CONFLICT");
                        sb.append(f.getAnnotation(Conflict.class).value().getSql());
                    }

                    if (f.getAnnotation(Check.class) != null) {
                        sb.append(" CHECK (");
                        sb.append(f.getAnnotation(Check.class).value());
                        sb.append(") ");
                    }
                    if (f.getAnnotation(Collate.class) != null) {
                        sb.append(" COLLATE ");
                        sb.append(f.getAnnotation(Collate.class).value());
                    }

                }
            }
        }
        sb.append(" )");
        return new SQLStatement(sb.toString(), null);
    }

    /**
     * 构建 insert 语句
     */
    public static SQLStatement buildInsertSql(Object entity, ConflictAlgorithm algorithm) {
        return buildInsertSql(entity, true, INSERT, algorithm);
    }

    /**
     * 构建批量 insert all 语句，sql不绑定值，执行时时会遍历绑定值。
     */
    public static SQLStatement buildInsertAllSql(Object entity, ConflictAlgorithm algorithm) {
        return buildInsertSql(entity, false, INSERT, algorithm);
    }

    /**
     * 构建 replace 语句
     */
    public static SQLStatement buildReplaceSql(Object entity) {
        return buildInsertSql(entity, true, REPLACE, null);
    }

    /**
     * 构建批量 replace all 语句，sql不绑定值，执行时时会遍历绑定值。
     */
    public static SQLStatement buildReplaceAllSql(Object entity) {
        return buildInsertSql(entity, false, REPLACE, null);
    }

    /**
     * 构建 insert SQL语句
     *
     * @param entity    实体
     * @param needValue 构建批量sql不需要赋值，执行时临时遍历赋值
     * @param type      {@link #INSERT}  or {@link #REPLACE}
     * @param algorithm {@link ConflictAlgorithm}
     * @return
     */
    private static SQLStatement buildInsertSql(Object entity, boolean needValue,
                                               int type, ConflictAlgorithm algorithm) {
        SQLStatement stmt = new SQLStatement();
        try {
            EntityTable table = TableManager.getTable(entity);
            StringBuilder sql = new StringBuilder(128);
            switch (type) {
                case INSERT:
                    sql.append("INSERT");
                    if (algorithm != null) {
                        sql.append(algorithm.getAlgorithm()).append("INTO ");
                    } else {
                        sql.append(" INTO ");
                    }
                    break;
                case REPLACE:
                    sql.append("REPLACE INTO ");
                    break;
                default:
                    sql.append("INSERT");
                    if (algorithm != null) {
                        sql.append(algorithm.getAlgorithm()).append("INTO ");
                    } else {
                        sql.append(" INTO ");
                    }
            }
            sql.append(table.name);
            sql.append(" ( ");
            sql.append(table.key.column);
            // 分两部分构建SQL语句，用一个for循环完成SQL构建和值的反射获取，以提高效率。
            StringBuilder value = new StringBuilder();
            value.append(" ) VALUES ( ?");
            int size = 1, i = 0;
            if (!Checker.isEmpty(table.pmap)) size += table.pmap.size();
            Object[] args = null;
            if (needValue) {
                args = new Object[size];
                args[i++] = FieldUtil.getAssignedKeyObject(table.key, entity);
            }
            if (!Checker.isEmpty(table.pmap)) {
                for (Entry<String, Property> en : table.pmap.entrySet()) {
                    // 后构造列名和占位符
                    sql.append(",").append(en.getKey());
                    value.append(",?");
                    // 构造列值
                    if (needValue) args[i] = FieldUtil.get(en.getValue().field, entity);
                    i++;
                }
            }
            sql.append(value).append(" )");
            stmt.bindArgs = args;
            stmt.sql = sql.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stmt;
    }

    /**
     * 构建 update 语句
     */
    public static SQLStatement buildUpdateSql(Object entity, ColumnsValue cvs, ConflictAlgorithm algorithm) {
        return buildUpdateSql(entity, cvs, algorithm, true);
    }

    /**
     * 构建批量 update all 语句，sql不绑定值，执行时时会遍历绑定值。
     */
    public static SQLStatement buildUpdateAllSql(Object entity, ColumnsValue cvs, ConflictAlgorithm algorithm) {
        return buildUpdateSql(entity, cvs, algorithm, false);
    }

    /**
     * 构建 update SQL语句
     *
     * @param entity    实体
     * @param cvs       更新的列,为NULL则更新全部
     * @param algorithm {@link ConflictAlgorithm}
     * @param needValue 构建批量sql不需要赋值，执行时临时遍历赋值
     * @return
     */
    private static SQLStatement buildUpdateSql(Object entity, ColumnsValue cvs,
                                               ConflictAlgorithm algorithm, boolean needValue) {
        SQLStatement stmt = new SQLStatement();
        try {
            EntityTable table = TableManager.getTable(entity);
            StringBuilder sql = new StringBuilder(128);
            sql.append("UPDATE");
            if (algorithm != null) {
                sql.append(algorithm.getAlgorithm());
            } else {
                sql.append(" ");
            }
            sql.append(table.name);
            sql.append(" SET ");
            // 分两部分构建SQL语句，用一个for循环完成SQL构建和值的反射获取，以提高效率。
            int size = 1, i = 0;
            Object[] args = null;
            if (cvs != null && cvs.checkColumns()) {
                if (needValue) {
                    size += cvs.columns.length;
                    args = new Object[size];
                }
                boolean hasVal = cvs.hasValues();
                for (; i < cvs.columns.length; i++) {
                    if (i > 0) sql.append(",");
                    sql.append(cvs.columns[i]).append("=?");
                    if (needValue) {
                        if (hasVal) args[i] = cvs.values[i];
                        if (args[i] == null) args[i] = FieldUtil.get(table.pmap.get(cvs.columns[i]).field, entity);
                    }
                }
            } else if (!Checker.isEmpty(table.pmap)) {
                if (needValue) {
                    size += table.pmap.size();
                    args = new Object[size];
                }
                for (Entry<String, Property> en : table.pmap.entrySet()) {
                    if (i > 0) sql.append(",");
                    sql.append(en.getKey()).append("=?");
                    if (needValue) args[i] = FieldUtil.get(en.getValue().field, entity);
                    i++;
                }
            } else if (needValue) {
                args = new Object[size];
            }
            if (needValue) args[size - 1] = FieldUtil.getAssignedKeyObject(table.key, entity);
            sql.append(" WHERE ").append(table.key.column).append("=?");
            stmt.sql = sql.toString();
            stmt.bindArgs = args;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stmt;
    }

    /**
     * 构建删除sql语句
     *
     * @param entity
     * @return
     */
    public static SQLStatement buildDeleteSql(Object entity) {
        SQLStatement stmt = new SQLStatement();
        try {
            EntityTable table = TableManager.getTable(entity);
            if (table.key != null) {
                stmt.sql = "DELETE FROM " + table.name + " WHERE " + table.key.column + " = ?";
                stmt.bindArgs = new String[]{String.valueOf(FieldUtil.get(table.key.field, entity))};
            } else if (!Checker.isEmpty(table.pmap)) {
                StringBuilder sb = new StringBuilder();
                sb.append("DELETE FROM ")
                        .append(table.name)
                        .append(" WHERE ");
                Object[] args = new Object[table.pmap.size()];
                int i = 0;
                for (Entry<String, Property> en : table.pmap.entrySet()) {
                    if (i == 0) {
                        sb.append(en.getKey()).append("=?");
                    } else {
                        sb.append(" and ").append(en.getKey()).append("=?");
                    }
                    args[i++] = FieldUtil.get(en.getValue().field, entity);
                }
                stmt.sql = sb.toString();
                stmt.bindArgs = args;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stmt;
    }

    /**
     * 构建批量删除sql语句
     *
     * @param collection
     * @return
     */
    public static SQLStatement buildDeleteSql(Collection<?> collection) {
        SQLStatement stmt = new SQLStatement();
        try {
            StringBuilder sb = new StringBuilder(256);
            EntityTable table = null;
            Object[] args = new Object[collection.size()];
            int i = 0;
            for (Object entity : collection) {
                if (i == 0) {
                    table = TableManager.getTable(entity);
                    sb.append("DELETE FROM ").append(table.name).append(" WHERE ")
                            .append(table.key.column).append(" IN (");
                    sb.append("?");
                } else {
                    sb.append(",?");
                }
                args[i++] = FieldUtil.get(table.key.field, entity);
            }
            sb.append(")");
            stmt.sql = sb.toString();
            stmt.bindArgs = args;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stmt;
    }

    /**
     * 构建全部删除sql语句
     *
     * @param claxx
     * @return
     */
    public static SQLStatement buildDeleteAllSql(Class<?> claxx) {
        SQLStatement stmt = new SQLStatement();
        EntityTable table = TableManager.getTable(claxx);
        stmt.sql = "DELETE FROM " + table.name;
        return stmt;
    }

    /**
     * 构建部分删除sql语句
     *
     * @param claxx
     * @return
     */
    public static SQLStatement buildDeleteSql(Class<?> claxx, long start, long end, String orderAscColumn) {
        SQLStatement stmt = new SQLStatement();
        EntityTable table = TableManager.getTable(claxx);
        String key = table.key.column;
        String orderBy = Checker.isEmpty(orderAscColumn) ? key : orderAscColumn;
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM ").append(table.name)
                .append(" WHERE ").append(key)
                .append(" IN ( SELECT ").append(key)
                .append(" FROM ").append(table.name)
                .append(" ORDER BY ").append(orderBy)
                .append(" ASC LIMIT ").append(start)
                .append(",").append(end)
                .append(")");
        stmt.sql = sb.toString();
        return stmt;
    }

    /**
     * 构建添加列语句
     *
     * @param tableName
     * @param column
     * @return
     */
    public static SQLStatement buildAddColumnSql(String tableName, String column) {
        SQLStatement stmt = new SQLStatement();
        stmt.sql = "ALTER TABLE " + tableName + " ADD " + column;
        return stmt;
    }

    /**
     * 构建关系映射语句
     *
     * @param claxx
     * @return
     */
    public static MapInfo buildDelAllMappingSql(Class claxx) {
        EntityTable table1 = TableManager.getTable(claxx);
        if (!Checker.isEmpty(table1.mappingList)) {
            try {
                MapInfo mapInfo = new MapInfo();
                for (MapProperty map : table1.mappingList) {
                    EntityTable table2 = TableManager.getTable(getTypeByRelation(map));
                    // add map table info
                    String mapTableName = TableManager.getMapTableName(table1, table2);
                    MapTable mi = new MapTable(mapTableName, table1.name, table2.name);
                    mapInfo.addTable(mi);

                    // add delete mapping sql to map info
                    SQLStatement st = buildMappingDeleteAllSql(table1, table2);
                    mapInfo.addDelOldRelationSQL(st);
                }
                return mapInfo;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 构建关系映射语句
     *
     * @param entity
     * @return
     */
    public static MapInfo buildMappingSql(Object entity, boolean insertNew) {
        EntityTable table1 = TableManager.getTable(entity);
        if (!Checker.isEmpty(table1.mappingList)) {
            try {
                Object key1 = FieldUtil.get(table1.key.field, entity);
                if (key1 == null) return null;
                MapInfo mapInfo = new MapInfo();
                for (MapProperty map : table1.mappingList) {
                    EntityTable table2 = TableManager.getTable(getTypeByRelation(map));
                    // add map table info
                    String mapTableName = TableManager.getMapTableName(table1, table2);
                    MapTable mi = new MapTable(mapTableName, table1.name, table2.name);
                    mapInfo.addTable(mi);

                    // add delete mapping sql to map info
                    SQLStatement st = buildMappingDeleteSql(key1, table1, table2);
                    mapInfo.addDelOldRelationSQL(st);

                    if (insertNew) {
                        // also insert new mapping relation
                        Object mapObject = FieldUtil.get(map.field, entity);
                        if (mapObject != null) {
                            if (map.isToMany()) {
                                st = buildMappingToManySql(key1, table1, table2, mapObject);
                                if (st != null) mapInfo.addNewRelationSQL(st);
                            } else {
                                st = buildMappingToOneSql(key1, table1, table2, mapObject);
                                if (st != null) mapInfo.addNewRelationSQL(st);
                            }
                        }
                    }
                }
                return mapInfo;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static Class getTypeByRelation(MapProperty mp) {
        Class calxx;
        if (mp.isToMany()) {
            Class c = mp.field.getType();
            if (ClassUtil.isCollection(c)) {
                calxx = FieldUtil.getGenericType(mp.field);
            } else {
                throw new RuntimeException("OneToMany and ManyToMany Relation, You must use collection object");
            }
        } else {
            calxx = mp.field.getType();
        }
        return calxx;
    }

    private static SQLStatement buildMappingDeleteAllSql(EntityTable table1, EntityTable table2)
            throws IllegalArgumentException, IllegalAccessException {
        if (table2 != null) {
            String mapTableName = TableManager.getMapTableName(table1, table2);
            SQLStatement stmt = new SQLStatement();
            stmt.sql = "DELETE FROM " + mapTableName;
            return stmt;
        }
        return null;
    }

    private static SQLStatement buildMappingDeleteSql(Object key1, EntityTable table1, EntityTable table2)
            throws IllegalArgumentException, IllegalAccessException {
        if (table2 != null) {
            String mapTableName = TableManager.getMapTableName(table1, table2);
            SQLStatement stmt = new SQLStatement();
            stmt.sql = "DELETE FROM " + mapTableName + " WHERE " + table1.name + " = ?";
            stmt.bindArgs = new Object[]{key1};
            return stmt;
        }
        return null;
    }

    private static SQLStatement buildMappingToManySql(Object key1, EntityTable table1, EntityTable table2, Object obj)
            throws IllegalArgumentException, IllegalAccessException {
        if (obj instanceof Collection<?>) {
            String mapTableName = TableManager.getMapTableName(table1, table2);
            Collection<?> coll = (Collection<?>) obj;
            if (!coll.isEmpty()) {
                boolean isF = true;
                StringBuilder values = new StringBuilder(128);
                ArrayList<Object> list = new ArrayList<Object>();
                for (Object o : coll) {
                    Object key2 = FieldUtil.getAssignedKeyObject(table2.key, o);
                    if (key2 != null) {
                        if (isF) {
                            values.append("(?,?)");
                            isF = false;
                        } else values.append(",(?,?)");
                        list.add(key1);
                        list.add(key2);
                    }
                }
                Object[] args = list.toArray();
                if (!Checker.isEmpty(args)) {
                    StringBuilder sql = new StringBuilder(256);
                    sql.append("INSERT INTO ")
                            .append(mapTableName)
                            .append(" (")
                            .append(table1.name).append(",")
                            .append(table2.name)
                            .append(") VALUES ").append(values);
                    SQLStatement stmt = new SQLStatement();
                    stmt.sql = sql.toString();
                    stmt.bindArgs = args;
                    return stmt;
                }
            }

        } else if (obj instanceof Object[]) {
            Object[] objs = (Object[]) obj;
            List<Object> list = Arrays.asList(objs);
            return buildMappingToManySql(key1, table1, table2, list);
        } else {
            throw new RuntimeException("OneToMany and ManyToMany Relation, You must use array or collection object");
        }
        return null;
    }

    private static SQLStatement buildMappingToOneSql(Object key1, EntityTable table1, EntityTable table2, Object obj)
            throws IllegalArgumentException, IllegalAccessException {
        Object key2 = FieldUtil.getAssignedKeyObject(table2.key, obj);
        if (key2 != null) {
            String mapTableName = TableManager.getMapTableName(table1, table2);
            StringBuilder sql = new StringBuilder(128);
            sql.append("INSERT INTO ")
                    .append(mapTableName)
                    .append(" (")
                    .append(table1.name).append(",")
                    .append(table2.name)
                    .append(") VALUES ").append("(?,?)");
            SQLStatement stmt = new SQLStatement();
            stmt.sql = sql.toString();
            stmt.bindArgs = new Object[]{key1, key2};
            return stmt;
        }
        return null;
    }

    /**
     * 构建查询关系映射语句
     */
    public static SQLStatement buildQueryRelationSql(Class class1, Class class2, List<String> key1List,
                                                     List<String> key2List) {
        final EntityTable table1 = TableManager.getTable(class1);
        final EntityTable table2 = TableManager.getTable(class2);
        QueryBuilder builder = new QueryBuilder(class1).queryMappingInfo(class2);
        ArrayList<String> keyList = new ArrayList<String>();
        StringBuilder sb = null;
        if (!Checker.isEmpty(key1List)) {
            sb = new StringBuilder();
            sb.append(table1.name);
            sb.append(" IN ( ");
            for (int i = 0, size = key1List.size(); i < size; i++) {
                if (i == 0) {
                    sb.append("?");
                } else {
                    sb.append(",?");
                }
            }
            sb.append(" ) ");
            keyList.addAll(key1List);
        }
        if (!Checker.isEmpty(key2List)) {
            if (sb == null) sb = new StringBuilder();
            else sb.append(" AND ");

            sb.append(table2.name);
            sb.append(" IN (");
            for (int i = 0, size = key2List.size(); i < size; i++) {
                if (i == 0) {
                    sb.append("?");
                } else {
                    sb.append(",?");
                }
            }
            sb.append(")");
            keyList.addAll(key2List);
        }
        if (sb != null) builder.where(sb.toString(), keyList.toArray(new String[0]));
        return builder.createStatement();
    }
}
