package com.hong.zyh.mobileplayer.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.hong.zyh.mobileplayer.R;
import com.hong.zyh.mobileplayer.base.BasePager;
import com.hong.zyh.mobileplayer.pager.AudioPager;
import com.hong.zyh.mobileplayer.pager.NetAudioPager;
import com.hong.zyh.mobileplayer.pager.NetVideoPager;
import com.hong.zyh.mobileplayer.pager.VideoPager;

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
        ft.replace(R.id.fl_main_content,new MyFragment(getBasePager()));
        //4、提交事务
        ft.commit();
    }

    /**
     *Fragment必须为static，因为防止内存泄漏
     * 静态内部类不持有外部类的引用，可以避免这些不必要的麻烦
     */
    public static class MyFragment extends Fragment {

        private BasePager currPager;

        public MyFragment(BasePager pager) {
            this.currPager=pager;
        }

        @Override
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
        BasePager basePager = basePagers.get(position);
        if (basePager != null) {
            //绑定数据
            basePager.initData();
            return basePager;
        }
        return null;
    }
}
