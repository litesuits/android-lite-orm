package com.litesuits.android.model;

import java.io.Serializable;

/**
 * @author MaTianyu
 * @date 14-7-22
 */
public class BaseModel implements Serializable {

    private String bm = "test base";

    @Override
    public String toString() {
        return "BaseModel{" +
                "bm='" + bm + '\'' +
                '}';
    }
}
