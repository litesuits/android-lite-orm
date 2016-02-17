#LiteOrm：Android高性能数据库框架
---

A fast, small, powerful ORM framework for Android. LiteOrm makes you do CRUD operarions on SQLite database with a sigle line of code efficiently.

English Intro ：[Readme](README-en.md)

LiteOrm是一个小巧、强大、比系统自带数据库操作性能快1倍的 android ORM 框架类库，开发者一行代码实现数据库的增删改查操作，以及实体关系的持久化和自动映射。

汉语简介 ：[Readme](README-cn.md)

QQ群： [新群 42960650][1] ， [一群（满） 47357508][2]

---

## 1. 初步认识
自动化且比系统自带数据库操作快1倍！

LiteOrm是android上的一款数据库（ORM）框架库。速度快、体积小、性能高。开发者基本一行代码实现数据库的增删改查操作，以及实体关系的持久化和自动映射。

10万条数插入对比系统API
![10万条数插入对比系统API](http://litesuits.com/imgs/lite-vs-system.png)

100 000条数据测试
![100 000条数据测试](http://litesuits.com/imgs/lite-10w-test.png)


###设计原则 ：
---

- 轻量、专注、性能优先、线程无关，专注数据及其关系存储和操作。
- 无需工具辅助，不需要无参构造，不需要繁多注解，约定优于配置。
- 使用极致简约，例如：db.save(u); db.query(U.class); db.deleteAll(U.class);


###功能特点 :
---

- 支持多库：每个数据库文件对应一个LiteOrm管理类实例。
- SD卡存储：可以将DB文件放在你认为合理的位置。
- 自动建表：开发者无需关心数据库以及表细节。
- 库文件操作：新建、打开、删除、释放一个数据库文件。
- 独立操作：使用 LiteOrm 的 single 实例，可与 cascade 方式平滑切换，性能高，仅处理该对象数据，其关系、和关联对象忽略；
- 级联操作：使用 LiteOrm 的 cascade 实例，可与 single 方式平滑切换，全递归，该对象数据，及其关系、和关联对象都被处理；
- 关系存储和恢复：真正实现实体关系映射持久化以及恢复，只需在实体的关联属性上标出关系类型即可。
- 智能列探测：App升级或者Model改变，新加了属性字段，该字段将被探测到并加入数据库中，因此无需担心新字段不被存储。
- 丰富API支持：save(replace), insert, update, delete, query, mapping, etc。
- 自动识别类型：分别转化为以sqlite支持的TEXT, REAL, INTEGER, BLOB几种数据类型存储。
- 自动构建对象，通过反射和探测构造函数参数等hack手法新建对象，大多情况下亦不需要无参构造函数。
- 更新指定列，可灵活、强制、批量赋值，强制赋值将无视被操作对象的真实值。
- 存储序列化字段：Date、ArrayList、Vector等各种容器智能保存及读取。
- 约束性语法支持：NOT NULL, UNIQUE, DEFAULT, COLLATE, CHECK, PRIMARY KEY，支持冲突算法。
- 灵活的查询和删除：columns, where, roder, limit, having group, etc。

## 2. 快速起步：初始化应保持单例
一个数据库对应一个LiteOrm的实例，如果一个App只有一个数据库，那么LiteOrm应该是全局单例的。
如果多次新建LiteOrm实例，系统会提示你应该关闭之前的数据库，也可能会引起其他未知错误。

保持单例：
```java
static LiteOrm liteOrm;

if (liteOrm == null) {
    liteOrm = LiteOrm.newSingleInstance(this, "liteorm.db");
}
liteOrm.setDebugged(true); // open the log
```

## 3. 基本注解
新建一个Test Model，将其作为操作对象：

```java
@Table("test_model")
public class TestModel {

    // 指定自增，每个对象需要有一个主键
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int id;

    // 非空字段
    @NotNull
    private String name;

    //忽略字段，将不存储到数据库
    @Ignore
    private String password;

    // 默认为true，指定列名
    @Default("true")
    @Column("login")
    private Boolean isLogin;
}
```

LiteOrm将为开发者建一个名为“test_model”的数据库表，其字段为：id   name   login。
建表语句：CREATE TABLE IF NOT EXISTS test_model (id INTEGER PRIMARY KEY AUTOINCREMENT ,name TEXT, login TEXT DEFAULT true)
更多注解关注其他篇章或直接参看Samples。

## 4. 常用操作
直接操作对象即可，LiteOrm会为你完成探测、建表等工作。

- 保存（插入or更新）
```java
School school = new School("hello");
liteOrm.save(school);
```

- 插入
```java
Book book = new Book("good");
liteOrm.insert(book, ConflictAlgorithm.Abort);
```

- 更新
```java
book.setIndex(1988);
book.setAuthor("hehe");
liteOrm.update(book);
```

- 更新指定列
```java
// 把所有书的author强制批量改为liter
HashMap<String, Object> bookIdMap = new HashMap<String, Object>();
bookIdMap.put(Book.COL_AUTHOR, "liter");
liteOrm.update(bookList, new ColumnsValue(bookIdMap), ConflictAlgorithm.Fail);
```

```java
// 仅 author 这一列更新为该对象的最新值。
//liteOrm.update(bookList, new ColumnsValue(new String[]{Book.COL_AUTHOR}, null), ConflictAlgorithm.Fail);
```

- 查询
```java
List list = liteOrm.query(Book.class);
OrmLog.i(TAG, list);
```

- 查找 使用WhereBuilder
```java
List<Student> list = liteOrm.query(new QueryBuilder<Student>(Student.class)
        .where(Person.COL_NAME + " LIKE ?", new String[]{"%0"})
        .whereAppendAnd()
        .whereAppend(Person.COL_NAME + " LIKE ?", new String[]{"%s%"}));
OrmLog.i(TAG, list);
```

- 查询 根据ID
```java
Student student = liteOrm.queryById(student1.getId(), Student.class);
OrmLog.i(TAG, student);
```

- 查询 任意
```java
List<Book> books = liteOrm.query(new QueryBuilder<Book>(Book.class)
        .columns(new String[]{"id", "author", Book.COL_INDEX})
        .distinct(true)
        .whereGreaterThan("id", 0)
        .whereAppendAnd()
        .whereLessThan("id", 10000)
        .limit(6, 9)
        .appendOrderAscBy(Book.COL_INDEX));
OrmLog.i(TAG, books);
```

- 删除 实体
```java
// 删除 student-0
liteOrm.delete(student0);
```

- 删除 指定数量
```java
// 按id升序，删除[2, size-1]，结果：仅保留第一个和最后一个
// 最后一个参数可为null，默认按 id 升序排列
liteOrm.delete(Book.class, 2, bookList.size() - 1, "id");
```

- 删除 使用WhereBuilder
```java
// 删除 student-1
liteOrm.delete(new WhereBuilder(Student.class)
        .where(Person.COL_NAME + " LIKE ?", new String[]{"%1%"})
        .and()
        .greaterThan("id", 0)
        .and()
        .lessThan("id", 10000));
```

- 删除全部
```java
// 连同其关联的classes，classes关联的其他对象一带删除
liteOrm.deleteAll(School.class);
liteOrm.deleteAll(Book.class);


// 顺带测试：连库文件一起删掉
liteOrm.deleteDatabase();
// 顺带测试：然后重建一个新库
liteOrm.openOrCreateDatabase();
// 满血复活
```

## 关于作者（About Author）
-----
我的博客 ：[http://vmatianyu.cn](http://vmatianyu.cn/)

我的开源站点 ：[http://litesuits.com](http://litesuits.com/)


  [1]: http://jq.qq.com/?_wv=1027&k=YsLkC6
  [2]: http://jq.qq.com/?_wv=1027&k=anQacU
  [3]: http://litesuits.com/imgs/lite-vs-system.png
