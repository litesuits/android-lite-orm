package com.litesuits.orm.db.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 冲突策略
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Conflict {
    public Strategy value();

    public static enum Strategy {
        ROLLBACK(" ROLLBACK "),
        ABORT(" ABORT "),
        FAIL(" FAIL "),
        IGNORE(" IGNORE "),
        REPLACE(" REPLACE ");

        Strategy(String sql) {
            this.sql = sql;
        }

        public String sql;

        public String getSql() {
            return sql;
        }
    }
}
