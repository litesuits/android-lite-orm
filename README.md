Lite Orm
================

A fast, small, powerful ORM framework for Android. LiteOrm makes you do CRUD operarions on SQLite database with a sigle line of code efficiently.

#### Goal：simple, powerful, and most userful android ORM framework library. 


Principles Principles:
---

- Lightweight, focus, performance priority, unrelated threads, focused data and relational storage and manipulation.
- No tools to assist, no constructor with no arguments, no many annotations, convention over configuration.
- Use extreme simplicity, such as: db.save (u); db.query (U.class); db.deleteAll (U.class);


Features Features:
---

- Support for multi-database: a database file corresponds to a LiteOrm management class instances.
- SD card storage: DB files can be placed in the position you think is reasonable.
- Automatically build tables: Developers do not care about the database and table details.
- Relational storage and recovery: real entity-relational mapping persistence and recovery, just mark the relationship type to the associated attributes of the entity.
- Independent and cascading: You can smooth handoff, independent operation of high performance, save only the object data; cascade operation more powerful, associated objects and relationships kept together.
- Intelligent Column Detection: App Model upgrade or change, added a new property field, which will be detected and added to the database, so no need to worry about the new fields will not be stored.
- Rich API support: save (replace), insert, update, delete, query, mapping, etc.
- Automatic Identification Type: respectively into with sqlite support TEXT, REAL, INTEGER, BLOB data type stores several.
- Automatically build an object, the new object is detected by the reflection and constructor parameters hack techniques, most cases nor requires no-argument constructor.
- Updates the designated column can be flexible, force, mass assignment, the assignment will be forced to disregard the real value of the object to be operated.
- Store a sequence of fields: Date, ArrayList, Vector, and other containers of smart save and read.
- Binding syntax supports: NOT NULL, UNIQUE, DEFAULT, COLLATE, CHECK, PRIMARY KEY, support conflict algorithms.
- Flexible query and delete: columns, where, roder, limit, having group, etc.

Futures:
---

- Information encryption to prevent data streaking after breaking library.
- Data validation features to prevent tampering after breaking the library.


## On basic functions
About demand function, the demand knows no boundaries, it is impossible to meet all the needs of the perfect, we do have a choice framework.

When a person to buy a drill to hang murals, Is he not a hole in the wall to buy it?

If you can provide holes and hang the mural service, not more convenient that lets the user satisfaction?

Back to think about the user to buy a drill, just to hang mural it? It can also be used to do more things.

When do the framework, the basic function is to be provided, but it can also provide a more direct, more specialized and more convenient interface to allow developers to complete the task.

About ## design
Behind the simplicity is often complex.

To save a developer when accessing large amounts of complex data duplication, design and implementation of this automated ORM framework.

LiteOrm most cases does not require the developer for each object, add a constructor with no arguments, a lot more than it looks silly.

LiteOrm main idea is that the agreement is greater than the configuration, so you can use very few notes complete storage complex data.

LiteOrm each method must assume their mission never superfluous, and use extreme simplicity.

LiteOrm concerned about performance, the code for each module I have to digest, bypassing implement various functions provides a direct interface to android and more closely tied to the underlying implementation.

LiteOrm super lightweight, focused, you do not even see any other features include threads, including the existence, you do not have to worry about the increased burden on your project, the introduction of a large burden.

And all of this is due to the reduction of the object-oriented experience, so part of the increase, not increased.


Lite Orm 中文
================

LiteOrm是一个速度快、小巧、强大的android ORM框架类库，让你一行代码实现数据库的增删改查操作，以及实体关系的持久化和自动映射。

#### 目标：简单、强大、最有用的android ORM 框架库


Principles 原则：
---

- 轻量、专注、性能优先、线程无关，专注数据及其关系存储和操作。
- 无需工具辅助，不需要无参构造，不需要繁多注解，约定优于配置。
- 使用极致简约，例如：db.save(u); db.query(U.class); db.deleteAll(U.class);


Features 特色:
---

- 支持多库：每个数据库文件对应一个LiteOrm管理类实例。
- SD卡存储：可以将DB文件放在你认为合理的位置。
- 自动建表：开发者无需关心数据库以及表细节。
- 关系存储和恢复：真正实现实体关系映射持久化以及恢复，只需在实体的关联属性上标出关系类型即可。
- 独立和级联：可平滑切换，独立操作性能高，仅保存该对象数据；级联操作更强大，关联对象和关系一并保存。
- 智能列探测：App升级或者Model改变，新加了属性字段，该字段将被探测到并加入数据库中，因此无需担心新字段不被存储。
- 丰富API支持：save(replace), insert, update, delete, query, mapping, etc。
- 自动识别类型：分别转化为以sqlite支持的TEXT, REAL, INTEGER, BLOB几种数据类型存储。
- 自动构建对象，通过反射和探测构造函数参数等hack手法新建对象，大多情况下亦不需要无参构造函数。
- 更新指定列，可灵活、强制、批量赋值，强制赋值将无视被操作对象的真实值。
- 存储序列化字段：Date、ArrayList、Vector等各种容器智能保存及读取。
- 约束性语法支持：NOT NULL, UNIQUE, DEFAULT, COLLATE, CHECK, PRIMARY KEY，支持冲突算法。
- 灵活的查询和删除：columns, where, roder, limit, having group, etc。

Futures：
---

- 信息加密功能，防止破库后数据裸奔。
- 数据校验功能，防止破库后数据篡改。


##关于基础功能
需求左右功能，需求是没有界限的，不可能完美满足所有需求，做框架就有取舍。

当一个人为了挂壁画而去买电钻时，难道他买的不是墙上的洞吗？

如果能提供打洞兼挂壁画的服务，不是更方便更让用户满意吗？

回头再想一下用户买了电钻，仅为了挂壁画吗？还可能用来做更多的事。

做框架时，基础功能是要提供的，但也会提供一些更直接、更专项的接口来更便捷的让开发者完成任务。

##关于设计理念
简约的背后，往往是复杂。

为了省掉开发者存取数据时大量的繁杂重复劳动，设计并实现了这款自动化ORM框架。

LiteOrm 大多情况下不要求开发者为每个对象添加一个无参构造，这看起来傻傻的很多余。

LiteOrm 主线思路是约定大于配置，所以你可以用极少的注解完成复杂数据的存储。

LiteOrm 各个方法都承担着自己的使命从不多余，且使用极致简约。

LiteOrm 关注性能，代码每个模块我都有仔细琢磨，各个功能的实现绕过了android提供的直接接口而比较贴近底层的实现。

LiteOrm 超级轻量、专注，你甚至看不到任何包括线程在内的其他功能存在，你根本不用担心增加你项目的负担，引入一个大包袱。

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
