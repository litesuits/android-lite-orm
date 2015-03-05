Archiver（Lite Orm）
================

A fast, small, powerful ORM framework for Android. LiteOrm makes you do CRUD operarions on SQLite database with a sigle line of code efficiently.
LiteOrm(Archiver)是一个速度快、小巧却强大的android ORM框架类库，Archiver让你一行代码实现数据库的增删改查操作，以及实体关系的持久化和自动映射。


Archiver 特色：
---

- 轻量，专注，性能优先，线程无关，专注数据及其关系（关系是重点）存储和读取。
- 不需要工具辅助，实体不需要无参构造，不需要繁多注解，约定大约配置，一切多余的，都不需要。
- 使用极致简约：db.save(u);    db.delete(u);    db.deleteAll(U.class);

Archiver 功能：
---

- 丰富的基础性增删改查接口支持：save(insert or replace), insert, update, delete, query, mapping...以及各种边缘功能并支持冲突算法。
- 支持多库，每个数据库文件对应一个DataBase实例，它们共享实体信息，表信息独立。一般单数据库情况下，建议自己维护单例DataBase db = LiteOrm.newInstance(context, "dbname")，
减少开销。
- 查找时自动构建对象，亦不需要无参构造函数。
- 支持仅更新指定列，支持向指定列灵活（批量）赋值，将无视对象中该属性的真实值。
- 自动建表：开发者无需关心数据库以及表细节。
- 智能识别数据类型：分别转化为以sqlite支持的TEXT, REAL, INTEGER, BLOB几种数据类型存储。
- 智能列探测：如User这个类多加了一个sex标识性别，Archiver将自动添加该列入已经建好的表，以支持新属性。
- 实体关系持久化和关系映射：真正实现实体关系映射持久化以及恢复，而开发者只需要在实体的关联属性上标出关系类型即可。
- 序列化字段皆可存储：各种ArrayList、Vector等各种容器智能保存及读取。
- 丰富的约束性语法支持：NOT NULL, UNIQUE, DEFAULT, COLLATE, CHECK, PRIMARY KEY
- 更自由灵活的查询：columns, where, roder, limit, having group, etc.

正在实现的功能：
---

- 信息加密功能，防止破库后数据裸奔。
- 数据校验功能，防止破库后数据篡改。

目标：有用的android ORM 框架库
---

##关于基础功能
需求左右功能，然而需求千变万化，所以做产品做框架就有取舍。

当一个人为了挂壁画而去买电钻时，难道他买的不是墙上的洞吗？这确是没错的。

如果我能提供打洞兼挂壁画的服务，不是更方便更让用户满意吗？这确是没错的。

然而用户买点钻，仅仅为了挂壁画吗？他还可能用来做更多的事！这也是没错的。

所以我做框架时，电钻是一定提供的，但也会提供一些更直接、更专项的接口来更便捷的让开发者完成任务。

这是我做框架时取舍功能的基本原则。

##关于设计理念
简约的背后，往往是复杂。

为了省掉开发者存取数据时大量的繁杂重复劳动，我设计并实现了这款自动化ORM框架。

开发者只要引入我的Archiver Library，即可轻松愉快的使用全部功能。

Archiver不强制要求开发者为每个对象添加一个无参构造，这很傻很多余。

Archiver主线思路是约定大于配置，所以你可以用极少的注解完成复杂数据的存储。

Archiver各个方法都承担着自己的使命从不多余，且使用极致简约，你用一下，就明白了。

Archiver关注性能，代码每个模块我都有仔细琢磨，各个功能的实现绕过了android提供的直接接口而比较贴近底层的实现。

Archiver超级轻量、专注，你甚至看不到任何包括线程在内的其他功能存在，你根本不用担心增加你项目的负担，引入一个大包袱。

而这一切，就是为了还原面向对象本应有的体验，让增加的部分，并未增加。


关于作者（About Author）
-----
我的博客 ：[http://vmatianyu.cn](http://vmatianyu.cn/)

我的开源站点 ：[http://litesuits.com](http://litesuits.com/)

点击加入QQ群: [47357508](http://jq.qq.com/?_wv=1027&k=Z7l0Av)

我的论坛帖子
-----
[LiteHttp：极简且智能的 android HTTP 框架库 (专注于网络)](http://www.eoeandroid.com/thread-326584-1-1.html)

[LiteOrm：极简且智能的 android ORM 框架库 (专注数据库)](http://www.eoeandroid.com/thread-538203-1-1.html)

[LiteAsync：强势的 android 异步 框架库 (专注异步与并发)](http://www.eoeandroid.com/thread-538212-1-1.html)

[LiteCommon：丰富通用的android工具类库(专注于基础组件)](http://www.eoeandroid.com/thread-557246-1-1.html)

我的博客帖子
-----
[关于java的线程并发和锁的总结](http://www.vmatianyu.cn/summary-of-the-java-thread-concurrency-and-locking.html)

[android开发技术经验总结60条](http://www.vmatianyu.cn/summarization-of-technical-experience.html)

[聚划算android客户端1期教训总结](http://www.vmatianyu.cn/poly-effective-client-1-issues-lessons.html)

[移动互联网产品设计小结](http://www.vmatianyu.cn/summary-of-mobile-internet-product-design.html)
