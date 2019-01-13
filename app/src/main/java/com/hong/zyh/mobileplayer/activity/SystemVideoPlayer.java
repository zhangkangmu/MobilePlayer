package com.hong.zyh.mobileplayer.activity;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.hong.zyh.mobileplayer.R;

/**
 * Created by shuaihong on 2019/1/12.
 * 自己创建的系统播放器
 */

public class SystemVideoPlayer extends Activity implements View.OnClickListener{
    private Uri uri;
    //videopager傳過來的數據
    private String videoName;
    //通過工具找到的id
    private VideoView system_vedio_player;
    private LinearLayout llTop;
    private TextView tvName;
    private ImageView ivBattery;
    private TextView tvSystemTime;
    private Button btVoice;
    private SeekBar seekbarVoice;
    private LinearLayout llBottom;
    private TextView tvCurrentTime;
    private SeekBar seekbarVideo;
    private TextView tvDuration;
    private Button bthExit;
    private Button btnVideoPre;
    private Button btnVideoStartPause;
    private Button btnVideoNext;
    private Button btnVideoSwitchScreen;

    /**
     * Find the Views in the layout
     * Auto-created on 2019-01-13 14:14:39 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        system_vedio_player = findViewById(R.id.system_vedio_player);
        llTop = (LinearLayout)findViewById( R.id.ll_top );
        tvName = (TextView)findViewById( R.id.tv_name );
        ivBattery = (ImageView)findViewById( R.id.iv_battery );
        tvSystemTime = (TextView)findViewById( R.id.tv_system_time );
        btVoice = (Button)findViewById( R.id.bt_voice );
        seekbarVoice = (SeekBar)findViewById( R.id.seekbar_voice );
        llBottom = (LinearLayout)findViewById( R.id.ll_bottom );
        tvCurrentTime = (TextView)findViewById( R.id.tv_current_time );
        seekbarVideo = (SeekBar)findViewById( R.id.seekbar_video );
        tvDuration = (TextView)findViewById( R.id.tv_duration );
        bthExit = (Button)findViewById( R.id.bth_exit );
        btnVideoPre = (Button)findViewById( R.id.btn_video_pre );
        btnVideoStartPause = (Button)findViewById( R.id.btn_video_start_pause );
        btnVideoNext = (Button)findViewById( R.id.btn_video_next );
        btnVideoSwitchScreen = (Button)findViewById( R.id.btn_video_switch_screen );

        btVoice.setOnClickListener( this );
        bthExit.setOnClickListener( this );
        btnVideoPre.setOnClickListener( this );
        btnVideoStartPause.setOnClickListener( this );
        btnVideoNext.setOnClickListener( this );
        btnVideoSwitchScreen.setOnClickListener( this );
    }

    /**
     * Handle button click events
     * Auto-created on 2019-01-13 14:14:39 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == btVoice) {
            // Handle clicks for btVoice
        } else if (v == bthExit) {
            // Handle clicks for bthExit
        } else if (v == btnVideoPre) {
            // Handle clicks for btnVideoPre
            //播放的id
        } else if (v == btnVideoStartPause) {
            if (system_vedio_player.isPlaying()) {
                //正在播放的视频被点击后暂停
                system_vedio_player.pause();
                //按钮切换成准备开始播放的样式
                btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_play_selector);
            }else{
                //视频开始播放
                system_vedio_player.start();
                //按钮切换成准备暂停播放的样式
                btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_pause_selector);
            }
        } else if (v == btnVideoNext) {
            // Handle clicks for btnVideoNext
        } else if (v == btnVideoSwitchScreen) {
            // Handle clicks for btnVideoSwitchScreen
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_video_player);
        //找出activity_system_video_player.xml的id
        findViews();

        videoName = getIntent().getStringExtra("videoName");

        //准备好了的监听
        system_vedio_player.setOnPreparedListener(new MyOnPreparedListener());

        //播放出错的监听
        system_vedio_player.setOnErrorListener(new MyOnErrorListener());

        //播放完成的监听
        system_vedio_player.setOnCompletionListener(new MyOnCompletionListener());

        //获取播放地址
        uri = getIntent().getData();
        if (uri != null) {
            system_vedio_player.setVideoURI(uri);
        }
        //设置控制面板
        //system_vedio_player.setMediaController(new MediaController(this));
        //自己定义一個面板
    }

    /**
     * 监听准备播放的class
     */
    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            //让VideoView开始播放
            system_vedio_player.start();
        }
    }

    /**
     * 监听播放出错的class
     * 如果出错会默认弹出一个对话框，但是这里增加多一个toash
     */
    class MyOnErrorListener implements MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Toast.makeText(SystemVideoPlayer.this, "播放出错了", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * 播放完成的監聽，播放完後顯示name播放完成
     */
    class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            Toast.makeText(SystemVideoPlayer.this, videoName+"播放完成了", Toast.LENGTH_SHORT).show();
        }
    }

}
