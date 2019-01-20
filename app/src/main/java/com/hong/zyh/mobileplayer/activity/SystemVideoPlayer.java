package com.hong.zyh.mobileplayer.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.hong.zyh.mobileplayer.bean.MediaItem;
import com.hong.zyh.mobileplayer.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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

    private Utils utils;
    /**
     * 监听电量广播接收者
     */
    private MyBroadcastReceiver myBroadcastReceiver;
    /**
     * VideoPlayer传递过来的列表数据
     */
    private ArrayList<MediaItem> mediaItems;
    /**
     * VideoPlayer传递过来的点击位置
     */
    private int position;

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
            finish();
            //播放上一个的id
        } else if (v == btnVideoPre) {
            playPreVideo();
            //播放暂停的id
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
            //播放下一个的id
        } else if (v == btnVideoNext) {
            playNextVideo();
        } else if (v == btnVideoSwitchScreen) {
            // Handle clicks for btnVideoSwitchScreen
        }
    }

    /**
     * btnVideoNext,播放下一个视频按钮的方法
     */
    private void playNextVideo() {
        if (mediaItems.size() > 0 && mediaItems != null) {
            position++;
            if (position < mediaItems.size()) {
                MediaItem mediaItem = mediaItems.get(position);
                tvName.setText(mediaItem.getName());
                system_vedio_player.setVideoPath(mediaItem.getData());
                //设置按钮的点击状态
                setButtonState();
            }
        } else if (uri != null) {
            //设置按钮状态-上一个和下一个按钮设置灰色并且不可以点击
            setButtonState();
        }
    }

    /**
     * 设置按钮的点击状态
     */
    private void setButtonState() {
        if (mediaItems != null && mediaItems.size() > 0) {
            if (mediaItems.size() == 1) {
                setEnable(false);
            }else if(mediaItems.size() == 2){
                //虽然position在被引用的地方++了，但是这里代表的是下一个播放的页面了，所以直接从0开始考虑
                if (position == 0) {
                    //下一个按钮可以点击
                    btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
                    btnVideoNext.setEnabled(true);

                    //上一个按钮不可以点击
                    btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                    btnVideoPre.setEnabled(false);
                }else if(position== (mediaItems.size()-1)){
                    //下一个按钮不可以点击
                    btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                    btnVideoNext.setEnabled(false);

                    //上一个按钮可以点击
                    btnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_seletor);
                    btnVideoPre.setEnabled(true);
                }else {
                    if (position == 0) {
                        btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                        btnVideoPre.setEnabled(false);
                    } else if (position == mediaItems.size() - 1) {
                        btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                        btnVideoNext.setEnabled(false);
                    } else {
                        setEnable(true);
                    }
                }
            }
        }else if (uri != null) {
            //两个按钮设置灰色
            setEnable(false);
        }
    }

    /**
     * 当不可以被点击的时候将突破资源显示灰色
     */
    private void setEnable(boolean isEnable) {
        if (isEnable) {
            btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
            btnVideoNext.setEnabled(true);
            btnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_seletor);
            btnVideoPre.setEnabled(true);
        }else {
            //两个按钮设置灰色
            btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
            btnVideoPre.setEnabled(false);
            btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
            btnVideoNext.setEnabled(false);
        }
    }

    /**
     * btnVideoPre,播放上一个视频按钮的方法
     */
    private void playPreVideo() {
        if (mediaItems.size() > 0 && mediaItems != null) {
            position--;
            if (position >=0) {
                MediaItem mediaItem = mediaItems.get(position);
                tvName.setText(mediaItem.getName());
                system_vedio_player.setVideoPath(mediaItem.getData());
                //设置按钮的点击状态
                setButtonState();
            }
        } else if (uri != null) {
            //设置按钮状态-上一个和下一个按钮设置灰色并且不可以点击
            setButtonState();
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

                    //更新时间
                    tvSystemTime.setText(getSystemTime());

                    //4、每秒更新一次
                    handler.removeMessages(PROGRESS);
                    handler.sendEmptyMessageDelayed(PROGRESS,1000);
                    break;
            }
        }
    };

    /**
     * 得到系统时间
     * @return
     */
    private String getSystemTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        return simpleDateFormat.format(new Date());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();

        setContentView(R.layout.activity_system_video_player);
        //找出activity_system_video_player.xml的id
        findViews();

        //设置system_vedio_player系统播放器的监听
        setListener();
        //获取播放VideoPager传递过来的列表数据方法
        getData();
        //设置列表数据
        setData();
        //设置控制面板
        //system_vedio_player.setMediaController(new MediaController(this));
        //自己定义一個面板
    }

    private void setData() {
        if (mediaItems != null && mediaItems.size() > 0) {
            MediaItem mediaItem = mediaItems.get(position);
            tvName.setText(mediaItem.getName());
            system_vedio_player.setVideoPath(mediaItem.getData());
        } else if (uri != null) {
            //如果是其他应用进来的就会传递一个uri过来，这里会获取他的路径进行播放
            tvName.setText(uri.toString());
            system_vedio_player.setVideoURI(uri);
        } else {
            Toast.makeText(SystemVideoPlayer.this, "没有找到到数据", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获取播放VideoPager传递过来的列表数据
     */
    private void getData() {
        //获取播放地址
        uri = getIntent().getData(); ////文件夹，图片浏览器，QQ空间
        mediaItems = (ArrayList<MediaItem>) getIntent().getSerializableExtra("videolist");
        position = getIntent().getIntExtra("position", 0);
    }

    private void initData() {
        utils=new Utils();
        myBroadcastReceiver=new MyBroadcastReceiver();
        //注册电量广播
        IntentFilter intentFilter = new IntentFilter();
        //当电量发生变化的时候发出的广播
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(myBroadcastReceiver,intentFilter);
    }

    class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //电量发出的电量值内容是level
            int level = intent.getIntExtra("level",0);
            setBattery(level);
        }
    }

    private void setBattery(int battery) {
        if (battery <= 0) {
            //更换图片使用的方法setImageResource
            ivBattery.setImageResource(R.drawable.ic_battery_0);
        } else if (battery < 10) {
            ivBattery.setImageResource(R.drawable.ic_battery_10);
        } else if (battery < 20) {
            ivBattery.setImageResource(R.drawable.ic_battery_20);
        } else if (battery < 40) {
            ivBattery.setImageResource(R.drawable.ic_battery_40);
        } else if (battery < 60) {
            ivBattery.setImageResource(R.drawable.ic_battery_60);
        } else if (battery < 80) {
            ivBattery.setImageResource(R.drawable.ic_battery_80);
        } else if (battery <=100) {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        } else {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }
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
            playNextVideo();
            Toast.makeText(SystemVideoPlayer.this, "播放完成了", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        //释放资源的时候要先释放子类的再释放父类的
        if(myBroadcastReceiver !=null){
            unregisterReceiver(myBroadcastReceiver);
            myBroadcastReceiver=null;
        }

        super.onDestroy();
    }
}
