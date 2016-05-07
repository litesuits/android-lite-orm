package com.litesuits.orm.samples;

import android.os.Bundle;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.DataBaseConfig;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.WhereBuilder;
import com.litesuits.orm.db.model.ColumnsValue;
import com.litesuits.orm.db.model.ConflictAlgorithm;
import com.litesuits.orm.log.OrmLog;
import com.litesuits.orm.model.single.*;
import com.litesuits.orm.test.SqliteUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SingleTestActivity extends BaseActivity {
    //Timer    timer;
    static LiteOrm liteOrm;
    static Man uComplex, uAlice, uMax, uMin;
    /**
     * object relation mapping test
     * man:address -> 1:n
     */
    static ConcurrentLinkedQueue<Address> addrList;
    /**
     * man:teacher -> n:n
     */
    static ArrayList<Boss> bossList;
    /**
     * man:company -> n:1
     */
    static Company company;
    /**
     * man:wife -> 1:1
     */
    static Wife wife1, wife2;

    /**
     * 在{@link com.litesuits.orm.samples.BaseActivity#onCreate(android.os.Bundle)}中设置视图
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSubTitile(getString(R.string.sub_title));
        mockData();

        if (liteOrm == null) {
            DataBaseConfig config = new DataBaseConfig(this, "liteorm.db");
            config.debugged = true; // open the log
            config.dbVersion = 1; // set database version
            config.onUpdateListener = null; // set database update listener
            liteOrm = LiteOrm.newSingleInstance(config);
        }
    }

    @Override
    public String getMainTitle() {
        return getString(R.string.title);
    }

    @Override
    public String[] getButtonTexts() {
        return getResources().getStringArray(R.array.orm_test_case);
    }

    @Override
    public Runnable getButtonClickRunnable(final int id) {
        //Main UI Thread
        //makeOrmTest(id);
        return new Runnable() {
            @Override
            public void run() {
                //Child Thread
                makeOrmTest(id);
            }
        };
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
                testLargeScaleUseLite();
                break;
            case 14:
                testLargeScaleUseSystem();
                break;
            default:
                break;
        }
    }

    private void testSave() {
        liteOrm.save(uMax);
        liteOrm.save(uMin);

        liteOrm.save(company);

        liteOrm.save(wife1);
        liteOrm.save(wife2);
        //保存任意集合
        liteOrm.save(addrList);
        liteOrm.save(bossList);
    }

    private void testInsert() {
        liteOrm.insert(uAlice, ConflictAlgorithm.Replace);
        liteOrm.insert(uComplex, ConflictAlgorithm.Rollback);
    }

    private void testUpdate() {

        //交换2个User的信息
        long id = uMax.getId();
        uMax.setId(uMin.getId());
        uMin.setId(id);

        // save : 既可以当insert 也可以做update，非常灵活
        long c = liteOrm.save(uMax);
        OrmLog.i(this, "update User Max: " + c);

        // update：仅能在已经存在时更新
        c = liteOrm.update(uMin, ConflictAlgorithm.Replace);
        OrmLog.i(this, "update User Min: " + c);

        //更新任意的整个集合
        bossList.get(0).setName("Cang Jin Kong");
        bossList.get(1).setName("Song Dao Feng");
        liteOrm.update(bossList, ConflictAlgorithm.Fail);
    }

    /**
     * 仅更新指定字段
     */
    private void testUpdateColumn() {

        //1. 集合更新实例：
        Boss boss0 = bossList.get(0);
        boss0.address = "随意写个乱七八糟的地址，反正我不会更新它";
        // 仅更新这一个字段
        boss0.phone = "168 8888 8888";

        Boss boss1 = bossList.get(1);
        boss1.address = "呵呵呵呵呵";
        boss1.phone = "168 0000 0000";

        ColumnsValue cv = new ColumnsValue(new String[]{"phone"});
        long c = liteOrm.update(bossList, cv, ConflictAlgorithm.None);
        OrmLog.i(this, "update boss ：" + c);


        //2. 更新单个实体（强制赋指定值）示例：
        wife1.des = "随意写个乱七八糟的描述，反正它会被覆盖";
        wife1.bm = "实体自带值";
        wife1.age = 18;
        cv = new ColumnsValue(new String[]{"des", "bm", "age"}, new Object[]{"外部强制赋值地址", null, 20});
        c = liteOrm.update(wife1, cv, ConflictAlgorithm.None);
        OrmLog.i(this, "update wife1 " + wife1.name + ": " + c);
    }


    private void testQueryAll() {
        ArrayList<Man> query = liteOrm.query(Man.class);
        ArrayList<Address> as = liteOrm.query(Address.class);
        ArrayList<Wife> ws = liteOrm.query(Wife.class);
        ArrayList<Company> cs = liteOrm.query(Company.class);
        ArrayList<Boss> ts = liteOrm.query(Boss.class);
        if (as != null) {
            for (Address uu : as) {
                OrmLog.i(this, "query Address: " + uu);
            }
        }
        if (ws != null) {
            for (Wife uu : ws) {
                OrmLog.i(this, "query Wife: " + uu);
            }
        }
        if (cs != null) {
            for (Company uu : cs) {
                OrmLog.i(this, "query Company: " + uu);
            }
        }
        if (ts != null) {
            for (Boss uu : ts) {
                OrmLog.i(this, "query Teacher: " + uu);
            }
        }
        if (query != null) {
            for (Man uu : query) {
                OrmLog.i(this, "query user: " + uu);
            }
        }
    }

    private void testQueryByWhere() {
        // 模糊查询：所有带“山”字的地址
        QueryBuilder<Address> qb = new QueryBuilder<Address>(Address.class)
                .where(Address.COL_ADDRESS + " LIKE ?", new String[]{"%山%"});
        printAddress(liteOrm.query(qb));

        //AND关系 获取 南京的香港路
        qb = new QueryBuilder<Address>(Address.class)
                .whereEquals(Address.COL_CITY, "南京")
                .whereAppendAnd()
                .whereEquals(Address.COL_ADDRESS, "香港路");
        printAddress(liteOrm.query(qb));

        //OR关系 获取所有 地址为香港路 ，和 青岛 的所有地址
        qb = new QueryBuilder<Address>(Address.class)
                .whereEquals(Address.COL_ADDRESS, "香港路")
                .whereAppendOr()
                .whereEquals(Address.COL_CITY, "青岛");
        printAddress(liteOrm.query(qb));

        //IN语句 获取所有 城市为杭州 和 北京 的地址
        qb = new QueryBuilder<Address>(Address.class)
                .whereIn(Address.COL_CITY, new String[]{"杭州", "北京"});
        printAddress(liteOrm.query(qb));

        //IN语句 获取同时满足：非香港路 & ID>5 & address包含山
        qb = new QueryBuilder<Address>(Address.class)
                .whereNoEquals(Address.COL_ADDRESS, "香港路")
                .whereAppendAnd()
                .whereGreaterThan(Address.COL_ID, 5)
                .whereAppendAnd()
                .whereAppend(Address.COL_ADDRESS + " LIKE ?", new String[]{"%山%"});
        printAddress(liteOrm.query(qb));
    }

    private void testQueryByID() {
        Man man = liteOrm.queryById(uComplex.getId(), Man.class);
        OrmLog.i(this, "query id: " + uComplex.getId() + ",MAN: " + man);
    }

    private void testQueryAnyUwant() {

        long nums = liteOrm.queryCount(Address.class);
        OrmLog.i(this, "Address All Count : " + nums);

        QueryBuilder<Address> qb = new QueryBuilder<Address>(Address.class)
                .columns(new String[]{Address.COL_ADDRESS})
                .appendOrderAscBy(Address.COL_ADDRESS)
                .appendOrderDescBy(Address.COL_ID)
                .distinct(true)
                .where(Address.COL_ADDRESS + "=?", new String[]{"香港路"});

        nums = liteOrm.queryCount(qb);
        OrmLog.i(this, "Address All Count : " + nums);
        List<Address> addrList = liteOrm.query(qb);
        for (Address uu : addrList) {
            OrmLog.i(this, "Query Address: " + uu);
        }

    }


    private void testMapping() {
        // 先找出来相关的实体
        ArrayList<Man> mans = liteOrm.query(Man.class);
        ArrayList<Address> as = liteOrm.query(Address.class);
        ArrayList<Wife> ws = liteOrm.query(Wife.class);
        ArrayList<Company> cs = liteOrm.query(Company.class);
        ArrayList<Boss> ts = liteOrm.query(Boss.class);
        // 为它们映射关系
        liteOrm.mapping(mans, as);
        liteOrm.mapping(mans, ws);
        liteOrm.mapping(mans, cs);
        liteOrm.mapping(mans, ts);
        //可以看到与Man关联的Teacher、Company、Address都智能映射给Man对应的各个的实例了。
        for (Man uu : mans) {
            OrmLog.i(this, "query user: " + uu);
        }
        for (Wife uu : ws) {
            OrmLog.i(this, "query Wife: " + uu);
        }
        for (Company uu : cs) {
            OrmLog.i(this, "query Company: " + uu);
        }
        for (Boss uu : ts) {
            OrmLog.i(this, "query Teacher: " + uu);
        }

    }

    private void testDelete() {
        liteOrm.delete(uMin);
        liteOrm.delete(uMax);
        liteOrm.delete(uAlice);
        liteOrm.delete(uComplex);

        // delete 任意 collection
        liteOrm.delete(bossList);

    }

    private void testDeleteByIndex() {
        // 最后一个参数可为null，默认按ID升序排列
        // 按id升序，删除[2, size-1]，结果：仅保留第一个和最后一个
        liteOrm.delete(Address.class, 2, addrList.size() - 1, Address.COL_ID);
    }

    private void testDeleteByWhereBuilder() {
        //AND关系 删掉 南京 的 香港路 第一种写法
        liteOrm.delete(WhereBuilder
                .create(Address.class)
                .equals(Address.COL_ADDRESS, "香港路")
                .andEquals(Address.COL_CITY, "南京"));

        //AND关系 删掉 南京 的 香港路 第二种写法
        liteOrm.delete(WhereBuilder
                .create(Address.class)
                .where("address=? AND city=?", new String[]{"香港路", "南京"}));

        //AND关系 删掉 南京 的 香港路 第三种写法
        liteOrm.delete(WhereBuilder
                .create(Address.class)
                .where("address=? AND city=?", "香港路", "南京"));

        printAllAddress();

        //OR关系 删掉所有地址为 香港路 ，同时删掉 青岛的所有地址
        liteOrm.delete(WhereBuilder
                .create(Address.class)
                .equals(Address.COL_ADDRESS, "香港路")
                .orEquals(Address.COL_CITY, "青岛"));
        printAllAddress();

        //IN语句 删掉所有城市为 杭州 或 北京的地址
        liteOrm.delete(WhereBuilder
                .create(Address.class)
                .in(Address.COL_CITY, new String[]{"杭州", "北京"}));
        printAllAddress();

        //IN语句 删掉所有 非香港路 并且 ID>10
        liteOrm.delete(WhereBuilder
                .create(Address.class)
                .equals(Address.COL_ADDRESS, "夫子庙")
                .and()
                .greaterThan(Address.COL_ID, 5));
        printAllAddress();
    }

    private void testDeleteAll() {
        liteOrm.deleteAll(Address.class);
        liteOrm.deleteAll(Company.class);
        liteOrm.deleteAll(Wife.class);
        liteOrm.deleteAll(Man.class);
        liteOrm.deleteAll(Boss.class);

        // 顺带测试：连库文件一起删掉
        liteOrm.deleteDatabase();
        // 顺带测试：然后重建一个新库
        liteOrm.openOrCreateDatabase();
        // 满血复活
    }


    /**
     * 100 000 条数据
     */
    final int MAX = 100000;

    private void testLargeScaleUseLite() {
        // LiteOrm 插入10w条数的效率测试
        SqliteUtils.testLargeScaleUseLiteOrm(liteOrm, MAX);
    }

    private void testLargeScaleUseSystem() {
        // 原生android代码 插入10w条数的效率测试
        SqliteUtils.testLargeScaleUseDefault(SingleTestActivity.this, MAX);
    }

    private void printAllAddress() {
        printAddress(liteOrm.query(Address.class));
    }

    private void printAddress(List<Address> addrList) {
        for (Address uu : addrList) {
            OrmLog.i(this, "Address: " + uu);
        }
    }

    private void mockData() {
        if (uAlice != null) {
            return;
        }
        uAlice = new Man(0, "alice", 18, false, (short) 12345, (byte) 123, 0.56f, 123.456d, 'c');
        uMax = new Man(0, "max", 99, false, Short.MAX_VALUE, Byte.MAX_VALUE, Float.MAX_VALUE, Double.MAX_VALUE,
                Character.MAX_VALUE);
        uMin = new Man(0, "min", 1, true, Short.MIN_VALUE, Byte.MIN_VALUE, Float.MIN_VALUE, Double.MIN_VALUE,
                Character.MIN_VALUE);
        uComplex = new Man(0, null, 0, false);
        uComplex.name = "complex";
        uComplex.setAge(18);
        uComplex.aShort = 32766;
        uComplex.aByte = 126;
        uComplex.aFloat = Float.MAX_VALUE;
        uComplex.setaDouble(Double.MAX_VALUE);
        uComplex.setLogin(true);
        uComplex.setDate(new Date(System.currentTimeMillis()));
        uComplex.setImg(new byte[]{23, 34, 77, 23, 19, 11});
        uComplex.def_bool = true;
        uComplex.def_int = 922;
        uComplex.conflict = "cutom";

        uComplex.map = new HashMap<Long, String>();
        uComplex.map.put(1002L, "1002 sdfsd324443534534534");
        uComplex.map.put(1003L, "1003 3dfgdfg24443534534534");
        uComplex.map.put(1004L, "1004 sdfsdg324443534534534");
        uComplex.map.put(1005L, "1005 dfsfd324443534534534");
        // 1 to N
        addrList = new ConcurrentLinkedQueue<Address>();
        addrList.add(new Address("1 西湖  ", "杭州"));
        addrList.add(new Address("2 武林  ", "杭州"));
        addrList.add(new Address("3 西二旗", "北京"));
        addrList.add(new Address("4 公主坟", "北京"));
        addrList.add(new Address("夫子庙", "南京"));
        addrList.add(new Address("中山陵", "南京"));
        addrList.add(new Address("西山陵", "南京"));
        addrList.add(new Address("香港路", "南京"));
        addrList.add(new Address("香港路", "杭州"));
        addrList.add(new Address("香港路", "青岛"));
        addrList.add(new Address("海尔路", "青岛"));
        addrList.add(new Address("海信路", "青岛"));
        uMax.addrList = addrList;

        // N to N
        ArrayList<Man> manlist = new ArrayList<Man>();
        manlist.add(uAlice);
        manlist.add(uComplex);

        bossList = new ArrayList<Boss>();
        Boss cang = new Boss("Cang boss", manlist);
        Boss song = new Boss("Song boss", manlist);
        bossList.add(cang);
        bossList.add(song);

        uAlice.bosses = bossList;
        uComplex.bosses = bossList;

        // 1 To 1
        wife1 = new Wife("Echo", uComplex);
        wife1.type = Wife.Type.enumOne;
        uComplex.wife = wife1;
        wife2 = new Wife("Yamaidi", uMax);
        wife2.type = Wife.Type.enumTwo;
        uMax.wife = wife2;

        // N To 1
        company = new Company("Apple Tech Co.Ltd", manlist);
        uComplex.company = company;
        uAlice.company = company;

        // Array
        //		uComplex.addrArray = new Address[2];
        //		uComplex.addrArray[0] = new Address("Array 0 Xihu Hangzhou China", uComplex);
        //		uComplex.addrArray[1] = new Address("Array 1 Xihu Hangzhou China", uComplex);

        //stack
        //		uComplex.addrIds = new Stack<Long>();
        //		uComplex.addrArray[0] = new Address("Array 0 Xihu Hangzhou China", uComplex);
        //		uComplex.addrArray[1] = new Address("Array 1 Xihu Hangzhou China", uComplex);

        System.out.println(uComplex);
        System.out.println(uAlice);
        System.out.println(uMax);
        System.out.println(uMin);
    }


}
