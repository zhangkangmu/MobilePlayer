package com.hong.zyh.mobileplayer.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.hong.zyh.mobileplayer.R;
import com.hong.zyh.mobileplayer.base.BasePager;
import com.hong.zyh.mobileplayer.pager.AudioPager;
import com.hong.zyh.mobileplayer.pager.NetAudioPager;
import com.hong.zyh.mobileplayer.pager.NetVideoPager;
import com.hong.zyh.mobileplayer.pager.VideoPager;
import com.hong.zyh.mobileplayer.utils.LogUtil;

import java.util.ArrayList;

/**
 * Created by shuaihong on 2018/12/31.
 */

public class MainActivity extends FragmentActivity {
    //bottom组合按键
    private RadioGroup rg_bottom_tag;
    private RadioButton rb_video;
    private RadioButton rb_audio;
    private RadioButton rb_net_video;
    private RadioButton rb_net_audio;
    //存每个页面的集合，方便后续RadioGroup监听
    private ArrayList<BasePager> basePagers;
    //记录每个页面的位置
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        requestPermissions();
        rg_bottom_tag = findViewById(R.id.rg_bottom_tag);
        //本地视频id
        rb_video = findViewById(R.id.rb_video);
        //本地音乐id
        rb_audio = findViewById(R.id.rb_audio);
        //网络视频id
        rb_net_video = findViewById(R.id.rb_net_video);
        //本地音乐id
        rb_net_audio = findViewById(R.id.rb_net_audio);

        //把四个页面增加到集合中
        basePagers = new ArrayList<BasePager>();
        basePagers.add(new VideoPager(this));       //本地视频页面
        basePagers.add(new AudioPager(this));       //本地音乐页面
        basePagers.add(new NetVideoPager(this));    //本地音频页面
        basePagers.add(new NetAudioPager(this));    //网络视频页面

       //设置RadioGroup的监听
        rg_bottom_tag.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
        //默认选中本地视频
        LogUtil.e("默认rb_video");
       rg_bottom_tag.check(R.id.rb_video);

    }

    private class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
            switch (checkedId) {
                case R.id.rb_video:
                    position = 0;
                    break;
                case R.id.rb_audio:
                    position = 1;
                    break;
                case R.id.rb_net_video:
                    position = 2;
                    break;
                case R.id.rb_net_audio:
                    position = 3;
                    break;
            }
            setFragment();
        }
    }

    /**
     * 把页面添加到Fragment中
     */
    private void setFragment() {
        //1、得到一个FragmentManager
        FragmentManager manager = getSupportFragmentManager();
        //2、开启事务
        FragmentTransaction ft = manager.beginTransaction();
        //3、替换
        MyFragment myFragment=new MyFragment();
        myFragment.newInstance(getBasePager());
        ft.replace(R.id.fl_main_content,myFragment);
        //4、提交事务
        ft.commit();
    }

    /**
     *Fragment必须为static，因为防止内存泄漏
     * 静态内部类不持有外部类的引用，可以避免这些不必要的麻烦
     */
    public static class MyFragment extends Fragment {

        private BasePager currPager;
//        public MyFragment() {
//        }
//        //如果重写了Fragment的构造方法会导致报错，因为如屏幕翻转时，fragment被重新创建，就可能会造成数据丢失
        //加的@SuppressLint({"NewApi", "ValidFragment"})这个语句就可以不检察，但是这是google不推荐的做法
//        @SuppressLint({"NewApi", "ValidFragment"})
//        public MyFragment(BasePager pager) {
//            this.currPager=pager;
//        }
        public BasePager newInstance(BasePager pager) {
            this.currPager=pager;
            return pager;
        }
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return currPager.rootView;
        }
    }
    /**
     * 根据位置得到对应的pager页面
     */
    private BasePager getBasePager() {
        LogUtil.e("position："+position);
        BasePager basePager = basePagers.get(position);
        if (basePager != null && !basePager.isInitData) {
            //绑定数据
            basePager.initData();
            basePager.isInitData =true;
        }
        return basePager;
    }

    /**
     * 是否已经退出
     */
    private boolean isExit = false;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode ==KeyEvent.KEYCODE_BACK){
            if(position != 0){//不是第一页面
                position = 0;
                rg_bottom_tag.check(R.id.rb_video);//首页
                return true;
            }else if(!isExit){
                isExit = true; Toast.makeText(MainActivity.this,"再按一次推出",Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isExit  = false;
                    }
                },2000);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    //申请录音权限
    private void requestPermissions(){
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                int permission = ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if(permission!= PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,new String[]
                            {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.LOCATION_HARDWARE,Manifest.permission.READ_PHONE_STATE,
                                    Manifest.permission.WRITE_SETTINGS,Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_CONTACTS},0x0010);
                }

                if(permission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,new String[] {
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},0x0010);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
