package com.litesuits.orm.model;

import com.litesuits.orm.db.annotation.*;
import com.litesuits.orm.db.enums.AssignType;
import com.litesuits.orm.db.enums.Relation;
import com.litesuits.orm.model.cascade.Book;

import java.util.ArrayList;

/**
 * @author MaTianyu @http://litesuits.com
 * @date 2015-12-13
 */
@Table("user")
public class User {

    // 指定自增，每个对象需要有一个主键
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    @Column("_id") // 指定列名
    private int id;

    @NotNull // 非空字段
    private String name;

    //忽略字段，将不存储到数据库
    @Ignore
    private String password;

    // 指定一对多关系
    @Mapping(Relation.OneToMany)
    public ArrayList<Book> bookList;

    @Default("true")// 默认为true
    private Boolean isLogin;

    // 指定数据库列名，防止SQL关键词冲突
    @Column("_index")
    @Check("index > 0 ") // 值需>0
    @NotNull
    private int index;

    // 联合唯一
    @UniqueCombine(1)// 和其他UniqueCombine value=1的字段（who）联合唯一
    private int mark;

    // 联合唯一
    @UniqueCombine(1)// 和其他UniqueCombine value=1的字段（mark）联合唯一
    private String who;

}
