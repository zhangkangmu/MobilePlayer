package com.hong.zyh.mobileplayer.view;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hong.zyh.mobileplayer.R;
import com.hong.zyh.mobileplayer.activity.SearchActivity;
import com.hong.zyh.mobileplayer.utils.LogUtil;

/**
 * Created by shuaihong on 2019/1/9.
 * 标题栏的自定义控件
 */

public class TitleBar extends LinearLayout implements View.OnClickListener{

    //上下文
    Context context;

    //点击事件对应的id
    View tv_search;
    View rl_game;
    View iv_history;

    /**
     * 代码中实例化的时候调用这个构造方法
     *
     * @param context
     */
    public TitleBar(Context context) {
        this(context, null);
    }

    /**
     * 在布局中是使用该类的时候，会自定调用这个构造方法
     *
     * @param context
     * @param attrs
     */
    public TitleBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 当需要设置样式的时候调用的方法
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public TitleBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
    }

    /**
     * 在加载完布局后会调用这个方法
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tv_search=getChildAt(1);
        rl_game=getChildAt(2);
        iv_history=getChildAt(3);

        //给对应的按钮设置点击事件
        tv_search.setOnClickListener(this);
        rl_game.setOnClickListener(this);
        iv_history.setOnClickListener(this);
    }

    /**
     * setOnClickListener后调用这个方法，然后在此判断按下那个按钮，然后做出对应的响应
     */
    @Override
    public void onClick(View view) {
     switch (view.getId()){
         //Toast不要忘记show出来了
         case R.id.tv_search:
             Intent intent = new Intent(context, SearchActivity.class);
             context.startActivity(intent);
             break;
         case R.id.rl_game:
             Toast.makeText(context,"点击游戏按钮。。。。",Toast.LENGTH_SHORT).show();
             break;
         case R.id.iv_history:
             Toast.makeText(context,"这是历史记录。。。。",Toast.LENGTH_SHORT).show();
             break;
     }
    }
}
