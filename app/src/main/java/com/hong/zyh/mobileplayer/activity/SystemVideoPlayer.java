package com.hong.zyh.mobileplayer.activity;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.hong.zyh.mobileplayer.utils.Utils;

/**
 * Created by shuaihong on 2019/1/12.
 * 自己创建的系统播放器
 */

public class SystemVideoPlayer extends Activity implements View.OnClickListener{

    /**
     * 视频进度
     */
    private static final int PROGRESS = 1;
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

    Utils utils;
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

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PROGRESS:
                    //1、得到视频当前的进度
                    int currentPosition = system_vedio_player.getCurrentPosition();
                    //2、设置文本显示当前播放到的时间
                    tvCurrentTime.setText(utils.stringForTime(currentPosition));
                    //3、设置seekbar的进度
                    seekbarVideo.setProgress(currentPosition);
                    //4、每秒更新一次
                    handler.removeMessages(PROGRESS);
                    handler.sendEmptyMessageDelayed(PROGRESS,500);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        utils=new Utils();

        setContentView(R.layout.activity_system_video_player);
        //找出activity_system_video_player.xml的id
        findViews();

        videoName = getIntent().getStringExtra("videoName");

        //设置system_vedio_player系统播放器的监听
        setListener();

        //获取播放地址
        uri = getIntent().getData();
        if (uri != null) {
            system_vedio_player.setVideoURI(uri);
        }
        //设置控制面板
        //system_vedio_player.setMediaController(new MediaController(this));
        //自己定义一個面板
    }

    private void setListener() {
        //准备好了的监听
        system_vedio_player.setOnPreparedListener(new MyOnPreparedListener());

        //播放出错的监听
        system_vedio_player.setOnErrorListener(new MyOnErrorListener());

        //播放完成的监听
        system_vedio_player.setOnCompletionListener(new MyOnCompletionListener());

        //设置seekbar状态变化的监听
        seekbarVideo.setOnSeekBarChangeListener(new VideoOnSeekBarChangeListener());
    }

    /**
     * 设置seekbar状态变化的实现类
     */
    class VideoOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        /**
         * 当进度发生变化的时候触发
         *
         * @param seekBar
         * @param progress
         * @param fromUser 如果是用户点击的则为ture,系统自己改变的则为false
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                system_vedio_player.seekTo(progress);
            }
        }

        /**
         * 当手指点击的时候触发
         *
         * @param seekBar
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        /**
         * 当手指松开的时候触发
         *
         * @param seekBar
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    /**
     * 监听准备播放的class
     */
    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            //让VideoView开始播放
            system_vedio_player.start();
            //获取当前视频总时长,这个第一种方式，还有另外一种方式是： mp.getDuration();
            int vedio_playerDuration = system_vedio_player.getDuration();
            //1、设置seekbar的总时长，关联上视频
            seekbarVideo.setMax(vedio_playerDuration);
            //2、设置视频总时长显示
            tvDuration.setText( utils.stringForTime(vedio_playerDuration));
            //3、发送消息告诉handler更新进度
            handler.sendEmptyMessage(PROGRESS);
        }
    }


    /**
     * 监听播放出错的class
     * 如果出错会默认弹出一个对话框，但是这里增加多一个toash
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
