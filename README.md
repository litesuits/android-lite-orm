Archiver（Lite Orm）
================

A fast, small, powerful ORM framework for Android. LiteOrm makes you do CRUD operarions on SQLite database with a sigle line of code efficiently.
LiteOrm(Archiver)是一个速度快、小巧却强大的android ORM框架类库，Archiver让你一行代码实现数据库的增删改查操作，以及实体关系的持久化和自动映射。

极简且智能的android orm 框架库
---

- 坚持轻量级、专一化、性能优先策略。专注于数据存储和实体关系操作，绝不做无关事情。
- 极简的增删改查操作：如 LiteOrm.via(context).delete(user);
- 亦或者自己维护单例DataBase db = LiteOrm.newInstance(this, "dbname") ;  db.query(Man.class);
- 智能建表和列：开发者无需关心数据库以及表细节。
- 自动识别数据类型：分别转化为以sqlite支持的TEXT, REAL, INTEGER, BLOB几种数据类型存储。
- 智能列检测：如User这个类多加了一个sex标识性别，Archiver将自动添加该列入已经建好的表，以支持新属性。
- 实体关系持久化和关系读取完成映射：真正实现实体关系映射，爽，而开发者只需要在实体的关联属性上标出关系类型即可。
- 可序列化字段皆可存储：各种ArrayList、Vector等各种容器智能保存及读取。
- 丰富的约束性语法支持：NOT NULL, UNIQUE, DEFAULT, COLLATE, CHECK, PRIMARY KEY
- 更自由的查询：columns, where, roder, limit, having group, etc.


##个人开源站点：http://litesuits.com/

因为刚对外开源，请恕我没有详细文档(github上有samples)，可能会有没预料到得bug，希望踊跃提建议和问题哦。
QQ交流群： 47357508    欢迎大牛入群交流，入群可以写明你的目的或研究方向~

一篇EOE发的帖子  
【LiteHttp：智能的android http框架】http://www.eoeandroid.com/thread-326584-1-1.html
