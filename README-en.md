LiteOrm:Android SQLite Framework
================

A fast, small, powerful ORM framework for Android. LiteOrm makes you do CRUD operarions on SQLite database with a sigle line of code efficiently.

#### Goal：simple, powerful, and most userful android ORM framework library. 


100 000 inset vs android SQLiteDatabase API
![100000 inset vs android SQLiteDatabase API](http://litesuits.com/imgs/lite-vs-system.png)

100 000 test
![100000 test](http://litesuits.com/imgs/lite-10w-test.png)

Principles :
---

- Lightweight, focus, performance priority, unrelated threads, focused data and relational storage and manipulation.
- No tools to assist, no constructor with no arguments, no many annotations, convention over configuration.
- Use extreme simplicity, such as: db.save (u); db.query (U.class); db.deleteAll (U.class);

Features :
---

- **Support for multi-database**: a database file corresponds to a LiteOrm management class instances.
- **SD card storage**: DB files can be placed in the position you think is reasonable.
- **Automatically build tables**: Developers do not care about the database and table details.
- **Relational storage and recovery**: real entity-relational mapping persistence and recovery, just mark the relationship type to the associated attributes of the entity.
- **Independent and cascading**: You can smooth handoff, independent operation of high performance, save only the object data; cascade operation more powerful, associated objects and relationships kept together.
- **Intelligent Column Detection**: App Model upgrade or change, added a new property field, which will be detected and added to the database, so no need to worry about the new fields will not be stored.
- **Rich API support: save (replace)**, insert, update, delete, query, mapping, etc.
- **Automatic Identification Type**: respectively into with sqlite support TEXT, REAL, INTEGER, BLOB data type stores several.
- **Automatically build an object**, the new object is detected by the reflection and constructor parameters hack techniques, most cases nor requires no-argument constructor.
- **Updates the designated column can be flexible, force, mass assignment**, the assignment will be forced to disregard the real value of the object to be operated.
- **Store a sequence of fields**: Date, ArrayList, Vector, and other containers of smart save and read.
- **Binding syntax supports**: NOT NULL, UNIQUE, DEFAULT, COLLATE, CHECK, PRIMARY KEY, support conflict algorithms.
- **Flexible query and delete**: columns, where, roder, limit, having group, etc.

Futures:
---

- Information encryption to prevent data streaking after breaking library.
- Data validation features to prevent tampering after breaking the library.


About ## design

LiteOrm most cases does not require the developer for each object, add a constructor with no arguments, a lot more than it looks silly.

LiteOrm main idea is that the agreement is greater than the configuration, so you can use very few notes complete storage complex data.

LiteOrm concerned about performance, the code for each module I have to digest, bypassing implement various functions provides a direct interface to android and more closely tied to the underlying implementation.

LiteOrm super lightweight, focused, you do not even see any other features include threads, including the existence, you do not have to worry about the increased burden on your project, the introduction of a large burden.

Behind the simplicity is often complex. And all of this is due to the reduction of the object-oriented experience, so part of the increase, not increased.

About Author
-----
blog：[http://vmatianyu.cn](http://vmatianyu.cn/)

site：[http://litesuits.com](http://litesuits.com/)
