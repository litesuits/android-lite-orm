package com.litesuits.orm.samples;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import com.litesuits.orm.log.OrmLog;

/**
 * 动态添加按钮和点击事件
 *
 * @author MaTianyu
 *         2014-2-25下午2:36:30
 */
public abstract class BaseActivity extends Activity implements OnClickListener {
    protected String TAG = "BaseActivity";
    private TextView mTvSubTitle;
    public LinearLayout container;
    public ScrollView scroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_list_btn);
        TAG = this.getClass().getSimpleName();
        OrmLog.setTag(TAG);

        container = (LinearLayout) findViewById(R.id.container);
        scroll = (ScrollView) container.getParent();
        TextView tv = (TextView) container.findViewById(R.id.title);
        tv.setText(getMainTitle());
        mTvSubTitle = (TextView) container.findViewById(R.id.sub_title);

        String[] bttxt = getButtonTexts();
        if (bttxt != null) {
            for (int i = 0; i < bttxt.length; i++) {
                Button bt = new Button(this);
                LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                int margin = getResources().getDimensionPixelSize(R.dimen.common_marin);
                lp.setMargins(margin, margin, margin, margin);
                bt.setId(i);
                bt.setText(bttxt[i]);
                bt.setOnClickListener(this);
                bt.setLayoutParams(lp);
                container.addView(bt);
            }
        }
    }

    /**
     * 获取主标题
     */
    public abstract String getMainTitle();

    /**
     * 设置二级标题
     */
    public void setSubTitile(String st) {
        mTvSubTitle.setText(st);
    }

    /**
     * 取button列表
     */
    public abstract String[] getButtonTexts();

    /**
     * 在{@link #onClick(View)} 里调用。
     * id值得含义为：若{@link #getButtonTexts()}的string[]数组长度为len，则id从0,1,2到len-1.
     * 点击第N个按钮，id变为N。
     */
    public abstract Runnable getButtonClickRunnable(final int id);

    @Override
    public void onClick(View v) {
        Runnable r = getButtonClickRunnable(v.getId());
        if (r != null) {
            new Thread(r).start();
        }
    }

}
