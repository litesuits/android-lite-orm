package com.litesuits.orm.test;

import com.litesuits.orm.db.utils.FieldUtil;
import com.litesuits.orm.model.single.Boss;

import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.List;

/**
 * @author MaTianyu
 * @date 2015-03-22
 */
public class TestClassType {

    public static void main(String[] args) {
        System.out.println("".getClass().isPrimitive());
        System.out.println(byte[].class.isPrimitive());
        System.out.println(int[].class.isPrimitive());
        System.out.println(Integer[].class.isPrimitive());
        System.out.println(Integer.class.isPrimitive());
        String url = "http://wap.cmread.com/r/lv;jsessionid=1C8B1A37019F1054DB6B759DD8A81D44.8ngGBhmvj.1" +
                ".0l?n=%r____&t1=16519&cm=M2040002&purl=%2Fr%2Fl%2Fr" +
                ".jsp%3Fcm%3DM2040002%26cid%3D400644484%26bid%3D400270618&vt=9&tgu=https%3A%2F%2Fwap.cmread.com%2Fsso%2Fauth%3Fe_p%3D1%26response_type%3Dtoken%26e_l%3D9%26redirect_uri%3Dhttp%253A%252F%252Fwap.cmread.com%252Fr%252Ff%252Fslr%253Bjsessionid%253D1C8B1A37019F1054DB6B759DD8A81D44.8ngGBhmvj.1.0%26state%3Dsuccurl%25253D%25252Fr%25252Fl%25252Fr.jsp%25253Bjsessionid%25253D1C8B1A37019F1054DB6B759DD8A81D44.8ngGBhmvj.1.0%25253Fln%25253D____%252526t1%25253D16519%252526cm%25253DM2040002%252526vt%25253D9%252526bid%25253D400270618%252526cid%25253D400644484%2526faildurl%25253D%26e_f%3D0%26client_id%3Dcmread-wap%26e_c%3DM2040002%26e_s%3D0";
        System.out.println(URLEncoder.encode("%%*-._0-9a-zA-Z"));
        System.out.println(URLEncoder.encode("!#$&'()*+-./:;=?@_~0-9a-zA-Z"));

        //System.out.println(URLDecoder.decode("%u9EC4%u5FD7%u52C7"));
        System.out.println(url.matches("^.+\\?(%[0-9a-fA-F]+|[=&A-Za-z0-9_#\\-\\.\\*])+$"));
        System.out.println(url.matches("^.+\\?[=&A-Za-z0-9_#%[0-9a-fA-F]+\\-\\.\\*]+$"));

        List<Field> fs = FieldUtil.getAllDeclaredFields(Boss.class);
        for(Field f : fs){
            System.out.println(f.getName() + " : " + f.getType() +"  " + CharSequence.class.isAssignableFrom(f.getType()));
            System.out.println(f.getName() + " : " + f.getType() +"  " + double.class.isAssignableFrom(f.getType()));
            System.out.println(f.getName() + " : " + f.getType() +"  " + Double.class.isAssignableFrom(f.getType()));
        }
    }
}
