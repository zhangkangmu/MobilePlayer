package com.hong.zyh.mobileplayer;

import android.app.Application;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import org.xutils.x;

/**
 * Created by hong on 2019/8/9.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG); // 是否输出debug日志, 开启debug会影响性能
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=5d519aba");
    }
}
