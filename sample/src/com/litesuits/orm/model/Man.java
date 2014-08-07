package com.litesuits.orm.model;

import com.litesuits.orm.db.annotation.*;
import com.litesuits.orm.db.annotation.Mapping.Relation;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Table("man")
public class Man extends Person{
    /************* 基础数据示例 ***************/

    private boolean isLogin;

    private int age;

    /************* Ignore 和 static final 属性将被忽略 ***************/
    @Ignore
    protected           String password = "4444";
    public static final String FINAL    = "this property will no be saved";

    /************* 四种映射关系示例 ***************/
    @Mapping(Relation.ManyToMany)
    public ArrayList<Teacher> teachers;

    //使用任何其他容器
    @Mapping(Relation.OneToMany)
    public ConcurrentLinkedQueue<Address> addrList;

    @Mapping(Relation.OneToOne)
    public Wife wife;

    @Mapping(Relation.ManyToOne)
    public Company company;

    /************* 约束性规则示例 ***************/

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

    /************* 其他数据类型示例 ***************/
    public  short  us;

    public byte ub;

    public float uf;

    private double ud;

    @Column("uChar")
    public char uc;

    @Column("uDate")
    protected Date date;

    private byte[] img;

    public Map<Long, String> map;

    public Man() { }

    public Man(long id, String name, int age, boolean isLogin, short us, byte ub, float uf, double ud, char uc) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.isLogin = isLogin;
        this.us = us;
		this.ub = ub;
		this.uf = uf;
		this.ud = ud;
		this.uc = uc;
	}

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

	public short getUs() {
		return us;
	}

	public void setUs(short us) {
		this.us = us;
	}

	public byte getUb() {
		return ub;
	}

	public void setUb(byte ub) {
		this.ub = ub;
	}

	public float getUf() {
		return uf;
	}

	public void setUf(float uf) {
		this.uf = uf;
	}

	public double getUd() {
		return ud;
	}

	public void setUd(double ud) {
		this.ud = ud;
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
        return  super.toString()+ "Man{" +
                "isLogin=" + isLogin +
                ", age=" + age +
                ", password='" + password + '\'' +
                ", teachers=" + teachers +
                ", addrList=" + addrList +
                ", wife=" + wife +
                ", company=" + company +
                ", us=" + us +
                ", ub=" + ub +
                ", uf=" + uf +
                ", ud=" + ud +
                ", uc=" + uc +
                ", date=" + date +
                ", img=" + Arrays.toString(img) +
                ", map=" + map +
                "} ";
    }

}