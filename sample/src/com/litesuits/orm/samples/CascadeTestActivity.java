package com.litesuits.orm.samples;

import android.os.Bundle;
import android.os.Environment;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.DataBaseConfig;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.WhereBuilder;
import com.litesuits.orm.db.model.ColumnsValue;
import com.litesuits.orm.db.model.ConflictAlgorithm;
import com.litesuits.orm.log.OrmLog;
import com.litesuits.orm.model.Person;
import com.litesuits.orm.model.cascade.*;
import com.litesuits.orm.test.SqliteUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CascadeTestActivity extends BaseActivity {


    public static final String SD_CARD = Environment.getExternalStorageDirectory().getAbsolutePath();

    /**
     * 名字里包含路径符号"/"则将数据库建立到该路径下，可以使用sd卡路径。
     * 不包含则在系统默认路径下创建DB文件。
     */
    public static final String DB_NAME = SD_CARD + "/lite/orm/cascade.db";

    public static LiteOrm liteOrm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSubTitile(getString(R.string.sub_title));

        // Model Relation：school-(1,N)->classes-(1,1)->teacher-(N,N)->student-(1,N)->book
        // 数据持有关系：学校 -(1,N)-> 班级 -(1,1)-> 老师 -(N,N)-> 学生 -(1,N)-> 书籍
        // 模拟数据 (1,N)一对多；（N,N）多对多；(N,1)多对一；(1,1)一对一
        mockData();

        if (liteOrm == null) {
            // 使用级联操作
            DataBaseConfig config = new DataBaseConfig(this, DB_NAME);
            config.debugged = true; // open the log
            config.dbVersion = 1; // set database version
            config.onUpdateListener = null; // set database update listener
            liteOrm = LiteOrm.newCascadeInstance(config);// cascade
        }

        //DataBase db = LiteOrm.newCascadeInstance(this, "cascade.db");
        //db.save(user);

        // 与非级联交叉使用：
        //db.cascade().save(user);//级联操作：保存[当前对象]，以及该对象所有的[关联对象]以及它们的[映射关系]，超贱！
        //db.single().save(user);//非级联操作：仅保存[当前对象]，高效率。
    }

    /**
     * <item>Save(Insert Or Update)</item>
     * <item>Insert</item>
     * <item>Update</item>
     * <item>Update Column</item>
     * <item>Query All</item>
     * <item>Query By WhereBuilder</item>
     * <item>Query By ID</item>
     * <item>Query Any U Want</item>
     * <item>Mapping Test</item>
     * <item>Delete</item>
     * <item>Delete By Index</item>
     * <item>Delete By WhereBuilder</item>
     * <item>Delete All</item>
     * <item>LiteOrm Faster: Large-scale Test(100,000)</item>
     * <item>SQLiteDatabase: Large-scale Test(100,000)</item>
     */
    private void makeOrmTest(int id) {
        switch (id) {
            case 0:
                testSave();
                // testMappingForNull();
                break;
            case 1:
                testInsert();
                break;
            case 2:
                testUpdate();
                break;
            case 3:
                testUpdateColumn();
                break;
            case 4:
                testQueryAll();
                break;
            case 5:
                testQueryByWhere();
                break;
            case 6:
                testQueryByID();
                break;
            case 7:
                testQueryAnyUwant();
                break;
            case 8:
                testMapping();
                break;
            case 9:
                testDelete();
                break;
            case 10:
                testDeleteByIndex();
                break;
            case 11:
                testDeleteByWhereBuilder();
                break;
            case 12:
                testDeleteAll();
                break;
            case 13:
                //SqliteUtils.testLargeScaleCascadeLiteOrm(liteOrm, 10000);
                // 注意 级联操作10万个数据将会相当耗时
                testLargeScaleUseLite();
                break;
            case 14:
                testLargeScaleUseSystem();
                break;
            default:
                break;
        }
    }

    private void testMappingForNull() {
        School s = new School("A");
        Classes c1 = new Classes("C1");
        Classes c2 = new Classes("C2");
        Classes c3 = new Classes("C3");
        s.classesList = new ArrayList<Classes>();
        s.classesList.add(c1);
        s.classesList.add(c2);
        s.classesList.add(c3);

        liteOrm.save(s);
        queryAndPrintAll(School.class);
        queryAndPrintAll(Classes.class);

        s.classesList = null;
        liteOrm.save(s);
        queryAndPrintAll(School.class);
        queryAndPrintAll(Classes.class);

        liteOrm.deleteAll(School.class);
        liteOrm.deleteAll(Classes.class);
    }

    private void testSave() {
        liteOrm.save(school);
    }

    private void testInsert() {
        liteOrm.insert(bookList);

        // 联合唯一测试
        Book book1 = new Book("书：year和author联合唯一");
        book1.setIndex(1988);
        book1.setAuthor("hehe");

        Book book2 = new Book("和上一本冲突：year和author联合唯一");
        book2.setIndex(1988);
        book2.setAuthor("hehe");

        liteOrm.insert(book1);
        // 注意会报警告
        liteOrm.insert(book2, ConflictAlgorithm.Abort);
    }

    private void testUpdate() {
        for (Book book : bookList) {
            int j = book.getIndex() % 3;
            if (j == 0) {
                book.setStudent(student2);
            } else if (j == 1) {
                book.setStudent(student1);
            } else if (j == 2) {
                book.setStudent(student0);
            }
            book.setIndex(book.getIndex() + 100);
        }
        liteOrm.update(bookList);
    }

    private void testUpdateColumn() {
        // 把所有书的author改为liter
        HashMap<String, Object> bookIdMap = new HashMap<String, Object>();
        bookIdMap.put(Book.COL_AUTHOR, "liter");
        liteOrm.update(bookList, new ColumnsValue(bookIdMap), ConflictAlgorithm.Fail);

        // 使用下面方式也可以
        //liteOrm.update(bookList, new ColumnsValue(new String[]{Book.COL_AUTHOR},
        //        new String[]{"liter"}), ConflictAlgorithm.Fail);

        // 仅 author 这一列更新为该对象的最新值。
        //liteOrm.update(bookList, new ColumnsValue(new String[]{Book.COL_AUTHOR}, null), ConflictAlgorithm.Fail);
    }

    private void testQueryAll() {
        queryAndPrintAll(Book.class);
        queryAndPrintAll(Student.class);
        queryAndPrintAll(Teacher.class);
        queryAndPrintAll(Classes.class);
        queryAndPrintAll(School.class);
    }

    private void testQueryByWhere() {
        List<Student> list = liteOrm.query(new QueryBuilder<Student>(Student.class)
                .where(Person.COL_NAME + " LIKE ?", new String[]{"%0"})
                .whereAppendAnd()
                .whereAppend(Person.COL_NAME + " LIKE ?", new String[]{"%s%"}));
        OrmLog.i(TAG, list);
    }

    private void testQueryByID() {
        Student student = liteOrm.queryById(student1.getId(), Student.class);
        OrmLog.i(TAG, student);
    }

    private void testQueryAnyUwant() {
        List<Book> books = liteOrm.query(new QueryBuilder<Book>(Book.class)
                .columns(new String[]{"id", "author", Book.COL_INDEX})
                .distinct(true)
                .whereGreaterThan("id", 0)
                .whereAppendAnd()
                .whereLessThan("id", 10000)
                .limit(6, 9)
                .appendOrderAscBy(Book.COL_INDEX));
        OrmLog.i(TAG, books);
    }

    private void testMapping() {
        // 级联实例本来就保存了关系映射
        queryAndPrintAll(School.class);
    }

    private void testDelete() {
        // 删除 student-0
        liteOrm.delete(student0);
    }

    private void testDeleteByIndex() {
        // 按id升序，删除[2, size-1]，结果：仅保留第一个和最后一个
        // 最后一个参数可为null，默认按 id 升序排列
        liteOrm.delete(Book.class, 2, bookList.size() - 1, "id");
    }

    private void testDeleteByWhereBuilder() {
        // 删除 student-1
        liteOrm.delete(new WhereBuilder(Student.class)
                .where(Person.COL_NAME + " LIKE ?", new String[]{"%1%"})
                .and()
                .greaterThan("id", 0)
                .and()
                .lessThan("id", 10000));
    }

    private void testDeleteAll() {
        // 连同其关联的classes，classes关联的其他对象一带删除
        liteOrm.deleteAll(School.class);
        //liteOrm.deleteAll(Book.class);


        // 顺带测试：连库文件一起删掉
        //liteOrm.deleteDatabase();
        // 顺带测试：然后重建一个新库
        //liteOrm.openOrCreateDatabase();
        // 满血复活
    }

    /**
     * 10000 条数据
     */
    final int MAX = 10000;

    /**
     * 注意 级联操作10万个数据将会相当耗时
     */
    private void testLargeScaleUseLite() {
        // LiteOrm 级联代码插入10w条数的效率测试
        SqliteUtils.testLargeScaleUseLiteOrm(liteOrm, MAX);
    }

    private void testLargeScaleUseSystem() {
        // 原生android代码插入10w条数的效率测试
        SqliteUtils.testLargeScaleUseDefault(CascadeTestActivity.this, MAX);
    }

    private void queryAndPrintAll(Class claxx) {
        List list = liteOrm.query(claxx);
        OrmLog.i(TAG, claxx.getSimpleName() + " : " + list);
    }


    protected static School school = null;
    protected static Classes classA;
    protected static Classes classB;
    protected static Teacher teacherA;
    protected static Teacher teacherB;
    protected static Student student0;
    protected static Student student1;
    protected static Student student2;
    protected static ArrayList<Book> bookList = new ArrayList<Book>();

    private void mockData() {
        if (school != null) {
            return;
        }
        school = new School("US MIT");
        classA = new Classes("class-a");
        classB = new Classes("class-b");

        //school:classes = 1:N
        school.classesList = new ArrayList<Classes>();
        school.classesList.add(classA);
        school.classesList.add(classB);

        teacherA = new Teacher("teacher-a", 19);
        teacherB = new Teacher("teacher-b", 28);

        //classes:teacher = 1:1
        classA.teacher = teacherA;
        classB.teacher = teacherB;

        student0 = new Student("student-0");
        student1 = new Student("student-1");
        student2 = new Student("student-2");

        //teacher:student = N:N
        teacherA.setStudentLinkedQueue(new ConcurrentLinkedQueue<Student>());
        teacherA.getStudentLinkedQueue().add(student0);
        teacherA.getStudentLinkedQueue().add(student1);
        teacherB.setStudentLinkedQueue(new ConcurrentLinkedQueue<Student>());
        teacherB.getStudentLinkedQueue().add(student0);
        teacherB.getStudentLinkedQueue().add(student2);
        student0.setTeachersArray(new Teacher[]{teacherA, teacherB});
        student1.setTeachersArray(new Teacher[]{teacherA});
        student2.setTeachersArray(new Teacher[]{teacherB});

        for (int i = 0; i < 30; i++) {
            Book book = new Book("book-" + i);
            book.setAuthor("autor" + i).setIndex(i);
            int j = i % 3;
            if (j == 0) {
                book.setStudent(student0);
            } else if (j == 1) {
                book.setStudent(student1);
            } else if (j == 2) {
                book.setStudent(student2);
            }
            bookList.add(book);
        }
    }


    @Override
    public String getMainTitle() {
        return getString(R.string.title_cascade);
    }

    @Override
    public String[] getButtonTexts() {
        return getResources().getStringArray(R.array.orm_test_case);
    }

    @Override
    public Runnable getButtonClickRunnable(final int id) {
        return new Runnable() {
            @Override
            public void run() {
                //Sub Thread
                makeOrmTest(id);
            }
        };
    }
}
