package com.litesuits.orm.db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * collate可应用于数据库定义或列定义以定义排序规则，或应用于字符串表达式以应用排序规则投影。
 * When SQLite compares two strings, it uses a collating sequence or collating function (two words for the same thing) to determine which string is greater or if the two strings are equal. SQLite has three built-in collating functions: BINARY, NOCASE, and RTRIM.
 * <p/>
 * BINARY - Compares string data using memcmp(), regardless of text encoding.
 * NOCASE - The same as binary, except the 26 upper case characters of ASCII are folded to their lower case equivalents before the comparison is performed. Note that only ASCII characters are case folded. SQLite does not attempt to do full UTF case folding due to the size of the tables required.
 * RTRIM - The same as binary, except that trailing space characters are ignored.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Collate {
    public String value();
}
