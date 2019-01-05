package com.hong.zyh.mobileplayer.pager;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.hong.zyh.mobileplayer.base.BasePager;
import com.hong.zyh.mobileplayer.utils.LogUtil;

/**
 * Created by shuaihong on 2019/1/3.
 * 网络视频页面
 */

public class NetVideoPager extends BasePager {
    private TextView textView;
    public NetVideoPager(Context context) {
        super(context);
    }

    @Override
        public View initView() {
            LogUtil.e("网络视频页面initView");
            textView=new TextView(context);
            textView.setTextSize(25);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(Color.RED);
            return textView;
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("网络视频页面initData");
        textView.setText("网络视频页面");
    }
}
