package com.litesuits.orm.db.model;

/**
 * @author MaTianyu
 * @date 14-7-24
 */
public class RelationKey {
    public String key1;
    public String key2;

    public boolean isOK() {
        return key1 != null && key2 != null;
    }

}
