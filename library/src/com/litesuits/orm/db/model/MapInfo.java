package com.litesuits.orm.db.model;

import com.litesuits.orm.db.assit.Checker;
import com.litesuits.orm.db.assit.SQLStatement;

import java.util.ArrayList;

/**
 * 映射表类
 *
 * @author MaTianyu
 *         2014-3-8上午3:20:20
 */
public class MapInfo {

    public static class MapTable {
        public MapTable(String name, String col1, String col2) {
            this.name = name;
            this.column1 = col1;
            this.column2 = col2;
        }

        public String name;
        public String column1;
        public String column2;
    }

    public ArrayList<MapTable> tableList;
    public ArrayList<SQLStatement> mapNewRelationSQL;
    public ArrayList<SQLStatement> delOldRelationSQL;

    public boolean addTable(MapTable table) {
        if (table.name == null)
            return false;
        if (tableList == null) {
            tableList = new ArrayList<MapTable>();
        }
        //for (MapTable mt : tableList) {
        //    if (mt.name.equals(table.name)) return false;
        //}
        return tableList.add(table);
    }

    public boolean addNewRelationSQL(SQLStatement st) {
        if (mapNewRelationSQL == null) {
            mapNewRelationSQL = new ArrayList<SQLStatement>();
        }
        return mapNewRelationSQL.add(st);
    }

    public boolean addNewRelationSQL(ArrayList<SQLStatement> list) {
        if (mapNewRelationSQL == null) {
            mapNewRelationSQL = new ArrayList<SQLStatement>();
        }
        return mapNewRelationSQL.addAll(list);
    }

    public boolean addDelOldRelationSQL(SQLStatement st) {
        if (delOldRelationSQL == null) {
            delOldRelationSQL = new ArrayList<SQLStatement>();
        }
        return delOldRelationSQL.add(st);
    }

    public boolean isEmpty() {
        return Checker.isEmpty(tableList)
               || Checker.isEmpty(mapNewRelationSQL) && Checker.isEmpty(delOldRelationSQL);
    }
}