package com.hong.zyh.mobileplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.hong.zyh.mobileplayer.activity.MainActivity;

public class SplashActivity extends Activity {
    //获取当前雷类名
    private static final String TAG = SplashActivity.class.getSimpleName();
    private Handler handler =new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startMianActivity();
                Log.d(TAG, "MianActivity开启");
                finish();
            }
        }, 2000);
    }

    //开启主页面的方法
    private void startMianActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
