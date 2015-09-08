package com.litesuits.orm.model.single;

import com.litesuits.orm.db.annotation.*;
import com.litesuits.orm.db.enums.Relation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

@Table("man")
public class Man extends Person {
    /**
     * ********** 基础数据示例 **************
     */

    private boolean isLogin;

    private int age;

    /**
     * ********** Ignore 和 static final 属性将被忽略 **************
     */
    @Ignore
    protected String password = "4444";
    public static final String FINAL = "this property will no be saved";

    /**
     * ********** 四种映射关系示例 **************
     */
    @Mapping(Relation.ManyToMany)
    public ArrayList<Boss> bosses;

    //使用任何其他容器
    @Mapping(Relation.OneToMany)
    public ConcurrentLinkedQueue<Address> addrList;

    @Mapping(Relation.OneToOne)
    public Wife wife;

    @Mapping(Relation.ManyToOne)
    public Company company;

    /**
     * ********** 约束性规则示例 **************
     */

    @Check("custom_name > 99")
    @Column("custom_name")
    private int check = 100;

    @Collate("NOCASE")
    private String _collate;

    @Column("_conlict")
    @Default("SQL默认值")
    @NotNull
    public String conflict;

    @Default("true")
    @NotNull
    public Boolean def_bool;

    @Default("911")
    @NotNull
    public Integer def_int;

    @NotNull
    private String not_null = "not null";

    /**
     * ********** 其他数据类型示例 **************
     */
    public short aShort;

    public byte aByte;

    public float aFloat;

    private double aDouble;

    public char aChar;

    protected Date date;

    private byte[] img = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

    public Map<Long, String> map;


    public Man(long id, String name, int age, boolean isLogin, short aShort, byte aByte, float aFloat, double aDouble, char aChar) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.isLogin = isLogin;
        this.aShort = aShort;
        this.aByte = aByte;
        this.aFloat = aFloat;
        this.aDouble = aDouble;
        this.aChar = aChar;
    }

    //public Man() { }

    public Man(long id, String name, int age, boolean isLogin) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.isLogin = isLogin;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public short getaShort() {
        return aShort;
    }

    public void setaShort(short aShort) {
        this.aShort = aShort;
    }

    public byte getaByte() {
        return aByte;
    }

    public void setaByte(byte aByte) {
        this.aByte = aByte;
    }

    public float getaFloat() {
        return aFloat;
    }

    public void setaFloat(float aFloat) {
        this.aFloat = aFloat;
    }

    public double getaDouble() {
        return aDouble;
    }

    public void setaDouble(double aDouble) {
        this.aDouble = aDouble;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public byte[] getImg() {
        return img;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean isLogin) {
        this.isLogin = isLogin;
    }

    @Override
    public String toString() {
        return "Man{" +
                super.toString() +
                "isLogin=" + isLogin +
                ", age=" + age +
                ", password='" + password + '\'' +
                ", teachers=" + bosses +
                ", addrList=" + addrList +
                ", wife=" + wife +
                ", company=" + company +
                ", check=" + check +
                ", _collate='" + _collate + '\'' +
                ", conflict='" + conflict + '\'' +
                ", def_bool=" + def_bool +
                ", def_int=" + def_int +
                ", not_null='" + not_null + '\'' +
                ", aShort=" + aShort +
                ", aByte=" + aByte +
                ", aFloat=" + aFloat +
                ", aDouble=" + aDouble +
                ", aChar=" + aChar +
                ", date=" + date +
                ", img=" + Arrays.toString(img) +
                ", map=" + map +
                "} ";
    }

}