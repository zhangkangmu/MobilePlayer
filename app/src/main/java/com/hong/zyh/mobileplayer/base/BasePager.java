package com.hong.zyh.mobileplayer.base;

import android.content.Context;
import android.view.View;

/**
 * Created by shuaihong on 2019/1/1.
 * VideoPager，AudioPager，NetVideoPager，NetAudioPager主页的父类
 */

public abstract class BasePager {
    /**
     * 上下文
     */
    public Context context;
    //私有的view，因为只需要给自己的页面展示
    public View rootView;

    public BasePager(Context context) {
        this.context = context;
        rootView = initView();
    }

    /**
     * 强制子类实现的效果
     */
    public abstract View initView();

    /**
     * 当继承类需要初始化数据,联网请求数据，或者绑定数据的时候重写的方法
     */
    public void initData() {
    }

}
