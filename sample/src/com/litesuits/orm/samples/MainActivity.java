package com.litesuits.orm.samples;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.litesuits.orm.R;

/**
 * 动态添加按钮和点击事件
 *
 * @author MaTianyu
 *         2014-2-25下午2:36:30
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
    }

    public void clickSingle(View view) {
        startActivity(new Intent(this, SingleTestActivity.class));
    }

    public void clickCascade(View view) {
        startActivity(new Intent(this, CascadeTestActivity.class));
    }
}
