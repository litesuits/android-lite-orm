package com.litesuits.orm;

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();

        LiteOrmApplication.init(this);
    }
}
