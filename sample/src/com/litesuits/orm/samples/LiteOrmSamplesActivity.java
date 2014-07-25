package com.litesuits.orm.samples;

import android.os.Bundle;
import android.view.Menu;
import com.litesuits.android.log.Log;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.R;
import com.litesuits.orm.db.DataBase;
import com.litesuits.orm.model.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LiteOrmSamplesActivity extends BaseActivity {
    //Timer    timer;
    DataBase db;
    static Man uComplex, uAlice, uMax, uMin;
    /**
     * object relation mapping test
     * man:address -> 1:n
     */
    static ConcurrentLinkedQueue<Address> addrList;
    /**
     * man:teacher -> n:n
     */
    static ArrayList<Teacher>             teacherList;
    /**
     * man:company -> n:1
     */
    static Company                        company;
    /**
     * man:wife -> 1:1
     */
    static Wife                           wife1, wife2;

    /**
     * 在{@link BaseActivity#onCreate(Bundle)}中设置视图
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSubTitile(getString(R.string.sub_title));
        mockData();
        db = LiteOrm.via(this);
        DataBase db = LiteOrm.newInstance(this, "dbname");
        db.query(Man.class);
    }

    public void onDestroy() {
        super.onDestroy();
        //if (timer != null) timer.cancel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public String getMainTitle() {
        return getString(R.string.title);
    }

    @Override
    public String[] getButtonTexts() {
        return getResources().getStringArray(R.array.orm_test_list);
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
     * 0<item>Insert</item>
     * 1<item>Update</item>
     * 2<item>Select</item>
     * 3<item>Delete</item>
     * 4<item>Mapping</item>
     *
     * @param id
     */
    private void makeOrmTest(int id) {
        switch (id) {
            case 0:
                testSave();
                break;
            case 1:
                testUpdate();
                break;
            case 2:
                testSelect();
                break;
            case 3:
                testDelete();
                break;
            case 4:
                testMapping();
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                break;
            case 8:
                break;
            case 9:
                break;
            default:
                break;
        }
    }

    private void testSave() {
        db.save(teacherList);
        db.save(addrList);
        db.save(company);
        db.save(wife1);
        db.save(wife2);

        long c = db.save(uAlice);
        c = db.save(uMax);
        c = db.save(uMin);
        c = db.save(uComplex);
    }

    private void testUpdate() {
        uAlice.setLogin(true);
        long c = db.save(uAlice);
        Log.i(this, "update alice: " + c);

        //交换uMin 和 uMax 的信息
        long id = uMin.getId();
        uMin.setId(uMax.getId());
        uMax.setId(id);

        c = db.save(uMax);
        Log.i(this, "update max: " + c);
        c = db.save(uMin);
        Log.i(this, "update min: " + c);
        if (id != 0) {
            //恢复
            uMax.setId(uMin.getId());
            uMin.setId(id);
        }

        uComplex.setName("Other Name ");
        uComplex.setAge(101);
        c = db.save(uComplex);
        Log.i(this, "update com: " + c);
    }

    private void testSelect() {
        long nums = db.queryCount(Address.class);
        Log.i(this, "Address count : " + nums);
        nums = db.queryCount(Man.class);
        Log.i(this, "Man count : " + nums);
        nums = db.queryCount(Company.class);
        Log.i(this, "Company count : " + nums);
        nums = db.queryCount(Wife.class);
        Log.i(this, "Wife count : " + nums);
        ArrayList<Man> query = db.query(Man.class);
        ArrayList<Address> as = db.query(Address.class);
        ArrayList<Wife> ws = db.query(Wife.class);
        ArrayList<Company> cs = db.query(Company.class);
        ArrayList<Teacher> ts = db.query(Teacher.class);
        for (Address uu : as) {
            Log.i(this, "query Address: " + uu);
        }
        for (Wife uu : ws) {
            Log.i(this, "query Wife: " + uu);
        }
        for (Company uu : cs) {
            Log.i(this, "query Company: " + uu);
        }
        for (Teacher uu : ts) {
            Log.i(this, "query Teacher: " + uu);
        }
        for (Man uu : query) {
            Log.i(this, "query user: " + uu);
        }
    }

    private void testMapping() {
        ArrayList<Man> mans = db.query(Man.class);
        ArrayList<Address> as = db.query(Address.class);
        ArrayList<Wife> ws = db.query(Wife.class);
        ArrayList<Company> cs = db.query(Company.class);
        ArrayList<Teacher> ts = db.query(Teacher.class);
        db.mapping(mans, as);
        db.mapping(mans, ws);
        db.mapping(mans, cs);
        db.mapping(mans, ts);
        for (Address uu : as) {
            Log.i(this, "query Address: " + uu);
        }
        for (Wife uu : ws) {
            Log.i(this, "query Wife: " + uu);
        }
        for (Company uu : cs) {
            Log.i(this, "query Company: " + uu);
        }
        for (Teacher uu : ts) {
            Log.i(this, "query Teacher: " + uu);
        }
        //可以看到与Man关联的Teacher、Company、Address都智能映射给Man对应的各个的实例了。
        for (Man uu : mans) {
            Log.i(this, "query user: " + uu);
        }
    }

    private void testDelete() {
        db.delete(uMin);
        db.delete(uMax);
        db.delete(uAlice);
        db.delete(uComplex);

        db.delete(teacherList);

        db.delete(company);

        db.deleteAll(Wife.class);

        // 仅保留一个地址，后边的全部删除
        db.delete(Address.class, 1, Integer.MAX_VALUE);
    }

    private void mockData() {
        if (uAlice != null) return;
        uAlice = new Man(0, "alice", 18, false, (short) 12345, (byte) 123, 0.56f, 123.456d, 'c');
        uMax = new Man(0, "max", 99, false, Short.MAX_VALUE, Byte.MAX_VALUE, Float.MAX_VALUE, Double.MAX_VALUE,
                Character.MAX_VALUE);
        uMin = new Man(0, "min", 1, true, Short.MIN_VALUE, Byte.MIN_VALUE, Float.MIN_VALUE, Double.MIN_VALUE,
                Character.MIN_VALUE);
        uComplex = new Man(0, null, 0, false);
        uComplex.name = "complex";
        uComplex.setAge(18);
        uComplex.us = 32766;
        uComplex.ub = 126;
        uComplex.uf = Float.MAX_VALUE;
        uComplex.setUd(Double.MAX_VALUE);
        uComplex.setLogin(true);
        uComplex.setDate(new Date(System.currentTimeMillis()));
        uComplex.setImg(new byte[]{23, 34, 77, 23, 19, 11});

        uComplex.map = new HashMap<Long, String>();
        uComplex.map.put(1002L, "1002 sdfsd324443534534534");
        uComplex.map.put(1003L, "1003 3dfgdfg24443534534534");
        uComplex.map.put(1004L, "1004 sdfsdg324443534534534");
        uComplex.map.put(1005L, "1005 dfsfd324443534534534");
        // 1 to N
        addrList = new ConcurrentLinkedQueue<Address>();
        addrList.add(new Address("1 Xihu Hangzhou China"));
        addrList.add(new Address("2 Hangzhou China"));
        addrList.add(new Address("3 Nanjing China"));
        addrList.add(new Address("4 sssss"));
        addrList.add(new Address("5 bbbbbb"));
        addrList.add(new Address("6 ccccc"));
        addrList.add(new Address("7 dddddd"));
        addrList.add(new Address("8 eeeee"));
        addrList.add(new Address("9 fffff"));
        uMax.addrList = addrList;

        // N to N
        ArrayList<Man> manlist = new ArrayList<Man>();
        manlist.add(uAlice);
        manlist.add(uComplex);

        teacherList = new ArrayList<Teacher>();
        Teacher cang = new Teacher("Cang sensei", manlist);
        Teacher song = new Teacher("Song sensei", manlist);
        teacherList.add(cang);
        teacherList.add(song);

        uAlice.teachers = teacherList;
        uComplex.teachers = teacherList;

        // 1 To 1
        wife1 = new Wife("Echo", uComplex);
        uComplex.wife = wife1;
        wife2 = new Wife("Yamaidi", uMax);
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
