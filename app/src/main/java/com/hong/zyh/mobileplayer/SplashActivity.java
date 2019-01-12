package com.hong.zyh.mobileplayer;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.hong.zyh.mobileplayer.activity.MainActivity;
import com.hong.zyh.mobileplayer.utils.LogUtil;

public class SplashActivity extends Activity {
    //获取当前雷类名
    private static final String TAG = SplashActivity.class.getSimpleName();
    private Handler handler =new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //判断是否获取
        if(!isGrantExternalRW(SplashActivity.this)){
            return;
        }
        //获取则走下面的代码
        setContentView(R.layout.activity_splash);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startMianActivity();
                Log.d(TAG, "MianActivity开启");
                finish();
            }
        }, 500);
    }

    /**
     * 6.0版本之后需要动态获取权限，则需要增加这个方法
     *
     * @param activity
     * @return
     */
    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);

            return false;
        }

        return true;
    }


    /**
     * 请求权限后回调的方法
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        //授权成功后的逻辑
                        //获取则走下面的代码
                       startMianActivity();
                    } else {
                        //拒绝获取权限走的代码
                        Toast.makeText(SplashActivity.this,"权限拒绝",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

    }
    //开启主页面的方法
    private void startMianActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        LogUtil.e("aplash-onDestroy");
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
