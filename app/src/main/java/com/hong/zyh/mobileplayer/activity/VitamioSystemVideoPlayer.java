package com.hong.zyh.mobileplayer.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hong.zyh.mobileplayer.R;
import com.hong.zyh.mobileplayer.bean.MediaItem;
import com.hong.zyh.mobileplayer.ui.VitamioVideoView;
import com.hong.zyh.mobileplayer.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.VideoView;

/**
 * Created by shuaihong on 2019/1/12.
 * 自己创建的系统播放器
 */

public class VitamioSystemVideoPlayer extends Activity implements View.OnClickListener{

    //如果是true，播放系统的，false则播放自己自定义的
    private boolean isUseSystem = true;

    private Uri uri;
    //videopager傳過來的數據
    private String videoName;
    //通過工具找到的id
    private VitamioVideoView system_vedio_player;
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
    private Button btnSwichPlayer;
    private RelativeLayout media_controller;
    private TextView tv_buffer_netspeed;
    private LinearLayout ll_buffer;
    private TextView tv_laoding_netspeed;
    private LinearLayout ll_loading;

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
     * 1.定义手势识别器
     */
    private GestureDetector detector;
    /**
     * 是否显示控制面板
     */
    private boolean isshowMediaController = false;
    /**
     * 是否全屏
     */
    private boolean isFullScreen = false;

    /**
     * 屏幕的宽
     */
    private int screenWidth = 0;

    /**
     * 屏幕的高
     */
    private int screenHeight = 0;

    /**
     * 真实视频的宽
     */
    private int videoWidth;
    /**
     * 真实视频的高
     */
    private int videoHeight;

    /**
     * 调用声音
     */
    private AudioManager am;

    /**
     * 当前的音量
     */
    private int currentVoice;

    /**
     * 0~15
     * 最大音量
     */
    private int maxVoice;
    /**
     * 是否是静音
     */
    private boolean isMute = false;
    /**
     * 是否是网络uri
     */
    private boolean isNetUri;

    /**
     * 上一次的播放进度
     */
    private int precurrentPosition;
    /**
     * 隐藏控制面板
     */
    private static final int HIDE_MEDIACONTROLLER = 2;


    /**
     * 显示网络速度
     */
    private static final int SHOW_SPEED = 3;
    /**
     * 视频进度
     */
    private static final int PROGRESS = 1;
    /**
     * 全屏
     */
    private static final int FULL_SCREEN = 1;
    /**
     * 默认屏幕
     */
    private static final int DEFAULT_SCREEN = 2;

        private void findViews() {
        setContentView(R.layout.activity_vitamio_video_player);
        Vitamio.isInitialized(this);
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
        media_controller = findViewById( R.id.media_controller );
            btnSwichPlayer = findViewById( R.id.btn_swich_player );
            tv_buffer_netspeed =  findViewById(R.id.tv_buffer_netspeed);
            ll_buffer = (LinearLayout) findViewById(R.id.ll_buffer);
            tv_laoding_netspeed = (TextView) findViewById(R.id.tv_laoding_netspeed);
            ll_loading = (LinearLayout) findViewById(R.id.ll_loading);


        btVoice.setOnClickListener( this );
        bthExit.setOnClickListener( this );
        btnVideoPre.setOnClickListener( this );
        btnVideoStartPause.setOnClickListener( this );
        btnVideoNext.setOnClickListener( this );
        btnVideoSwitchScreen.setOnClickListener( this );
            btnSwichPlayer.setOnClickListener( this );

        //最大音量和SeekBar关联
         seekbarVoice.setMax(maxVoice);
        //设置当前进度-当前音量
         seekbarVoice.setProgress(currentVoice);

            //开始更新网络速度
            handler.sendEmptyMessage(SHOW_SPEED);
    }

    @Override
    public void onClick(View v) {
        if (v == btVoice) {
            isMute = !isMute;
            updataVoice(currentVoice, isMute);
        } else if (v== btnSwichPlayer){
//            showSwichPlayerDialog();
            Toast.makeText(VitamioSystemVideoPlayer.this, "切换系统播放器", Toast.LENGTH_SHORT).show();
        }else if (v == bthExit) {
            finish();
            //播放上一个的id
        } else if (v == btnVideoPre) {
            playPreVideo();
            //播放暂停的id
        } else if (v == btnVideoStartPause) {
            startAndPause();
            //播放下一个的id
        } else if (v == btnVideoNext) {
            playNextVideo();
        } else if (v == btnVideoSwitchScreen) {
            // Handle clicks for btnVideoSwitchScreen
            setFullScreenAndDefault();
        }
    }

//    private void showSwichPlayerDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("万能播放器提醒您");
//        builder.setMessage("当您播放一个视频，有花屏的是，可以尝试使用系统播放器播放");
//        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                startSystemPlayer();
//            }
//        });
//        builder.setNegativeButton("取消", null);
//        builder.show();
//    }

    private void startSystemPlayer() {
        if(system_vedio_player != null){
            system_vedio_player.stopPlayback();
        }


        Intent intent = new Intent(this,SystemVideoPlayer.class);
        if(mediaItems != null && mediaItems.size() > 0){

            Bundle bundle = new Bundle();
            bundle.putSerializable("videolist", mediaItems);
            intent.putExtras(bundle);
            intent.putExtra("position", position);

        }else if(uri != null){
            intent.setData(uri);
        }
        startActivity(intent);

        finish();//关闭页面
    }

    /**
     * btnVideoNext,播放下一个视频按钮的方法
     */
    private void playNextVideo() {
        if (mediaItems.size() > 0 && mediaItems != null) {
            position++;
            if (position < mediaItems.size()) {

                ll_loading.setVisibility(View.VISIBLE);
                MediaItem mediaItem = mediaItems.get(position);
                tvName.setText(mediaItem.getName());
                isNetUri = utils.isNetUri(mediaItem.getData());
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
                ll_loading.setVisibility(View.VISIBLE);
                MediaItem mediaItem = mediaItems.get(position);
                tvName.setText(mediaItem.getName());
                isNetUri = utils.isNetUri(mediaItem.getData());
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
                case SHOW_SPEED://显示网速
                    //1.得到网络速度
                    String netSpeed = utils.getNetSpeed(VitamioSystemVideoPlayer.this);

                    //显示网络速
                    tv_laoding_netspeed.setText("玩命加载中..."+netSpeed);
                    tv_buffer_netspeed.setText("缓存中..."+netSpeed);

                    //2.每两秒更新一次
                    handler.removeMessages(SHOW_SPEED);
                    handler.sendEmptyMessageDelayed(SHOW_SPEED, 2000);

                    break;
                case HIDE_MEDIACONTROLLER://隐藏控制面板
                    hideMediaController();
                    break;
                case PROGRESS:
                    //1、得到视频当前的进度
                    int currentPosition = (int) system_vedio_player.getCurrentPosition();
                    //2、设置文本显示当前播放到的时间
                    tvCurrentTime.setText(utils.stringForTime(currentPosition));
                    //3、设置seekbar的进度
                    seekbarVideo.setProgress(currentPosition);

                    //更新时间
                    tvSystemTime.setText(getSystemTime());


                    //缓存进度的更新
                    if (isNetUri) {
                        //只有网络资源才有缓存效果
                        int buffer = system_vedio_player.getBufferPercentage();//0~100
                        int totalBuffer = buffer * seekbarVideo.getMax();
                        int secondaryProgress = totalBuffer / 100;
                        seekbarVideo.setSecondaryProgress(secondaryProgress);
                    } else {
                        //本地视频没有缓冲效果
                        seekbarVideo.setSecondaryProgress(0);
                    }

                    //监听卡
                    if (!isUseSystem) {
                         //自己设置的判断卡
                        if(system_vedio_player.isPlaying()){
                            int buffer = currentPosition - precurrentPosition;
                            if (buffer < 500) {
                                //视频卡了
                                ll_buffer.setVisibility(View.VISIBLE);
                            } else {
                                //视频不卡了
                                ll_buffer.setVisibility(View.GONE);
                            }
                        }else{
                            ll_buffer.setVisibility(View.GONE);
                        }

                    }


                    precurrentPosition = currentPosition;

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
            isNetUri = utils.isNetUri(mediaItem.getData());
            system_vedio_player.setVideoPath(mediaItem.getData());
        } else if (uri != null) {
            //如果是其他应用进来的就会传递一个uri过来，这里会获取他的路径进行播放
            tvName.setText(uri.toString());
            isNetUri = utils.isNetUri(uri.toString());
            system_vedio_player.setVideoURI(uri);
        } else {
            Toast.makeText(VitamioSystemVideoPlayer.this, "没有找到到数据", Toast.LENGTH_SHORT).show();
        }
        setButtonState();
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

        //2.实例化手势识别器，并且重写双击，点击，长按
        detector = new GestureDetector(this, new MySimpleOnGestureListener());

        //得到屏幕的宽和高最新方式
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        //得到音量
        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        currentVoice = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVoice = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    private float startY;
    private float startX;
    /**
     * 屏幕的高
     */
    private float touchRang;

    /**
     * 当一按下的音量
     */
    private int mVol;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN://手指按下
                //1.按下记录值
                startY = event.getY();
                startX = event.getX();
                mVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                touchRang = Math.min(screenHeight, screenWidth);//screenHeight  用取最小值是因为有横竖屏，这时框和高会改变
                handler.removeMessages(HIDE_MEDIACONTROLLER);

                break;
            case MotionEvent.ACTION_MOVE://手指移动
                //2.移动的记录相关值
                float endY = event.getY();
                float endX = event.getX();
                float distanceY = startY - endY;
                //改变声音 = （滑动屏幕的距离： 总距离）*音量最大值
                    float delta = (distanceY / touchRang) * maxVoice;
                    //最终声音 = 原来的 + 改变声音；
                    int voice = (int) Math.min(Math.max(mVol + delta, 0), maxVoice);
                        if (delta != 0) {
                        isMute = false;
                        updataVoice(voice, isMute);
                    }
//
//                if(endX < screenWidth/2){
//                    //左边屏幕-调节亮度
//                    final double FLING_MIN_DISTANCE = 0.5;
//                    final double FLING_MIN_VELOCITY = 0.5;
//                    if (distanceY > FLING_MIN_DISTANCE
//                            && Math.abs(distanceY) > FLING_MIN_VELOCITY) {
////                        Log.e(TAG, "up");
//                        setBrightness(20);
//                    }
//                    if (distanceY < FLING_MIN_DISTANCE
//                            && Math.abs(distanceY) > FLING_MIN_VELOCITY) {
////                        Log.e(TAG, "down");
//                        setBrightness(-20);
//                    }
//                }else{
//                    //右边屏幕-调节声音
//                    //改变声音 = （滑动屏幕的距离： 总距离）*音量最大值
//                    float delta = (distanceY / touchRang) * maxVoice;
//                    //最终声音 = 原来的 + 改变声音；
//                    int voice = (int) Math.min(Math.max(mVol + delta, 0), maxVoice);
//                    if (delta != 0) {
//                        isMute = false;
//                        updataVoice(voice, isMute);
//                    }
//
//                }


//                startY = event.getY();//不要加,加了之后距离就是一点一点了，不敏感
                break;
            case MotionEvent.ACTION_UP://手指离开
                handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
                break;
        }
        return super.onTouchEvent(event);
    }
    private  Vibrator vibrator;
    /*
     *
     * 设置屏幕亮度 lp = 0 全暗 ，lp= -1,根据系统设置， lp = 1; 最亮
     */
    public void setBrightness(float brightness) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        // if (lp.screenBrightness <= 0.1) {
        // return;
        // }
        lp.screenBrightness = lp.screenBrightness + brightness / 255.0f;
        if (lp.screenBrightness > 1) {
            lp.screenBrightness = 1;
            vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            long[] pattern = { 10, 200 }; // OFF/ON/OFF/ON...
            vibrator.vibrate(pattern, -1);
        } else if (lp.screenBrightness < 0.2) {
            lp.screenBrightness = (float) 0.2;
            vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            long[] pattern = { 10, 200 }; // OFF/ON/OFF/ON...
            vibrator.vibrate(pattern, -1);
        }
//        Log.e(TAG, "lp.screenBrightness= " + lp.screenBrightness);
        getWindow().setAttributes(lp);
    }

    class MySimpleOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            Toast.makeText(VitamioSystemVideoPlayer.this, "我被长按了", Toast.LENGTH_SHORT).show();
            startAndPause();
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
                Toast.makeText(VitamioSystemVideoPlayer.this, "我被双击了", Toast.LENGTH_SHORT).show();
            setFullScreenAndDefault();
            return super.onDoubleTap(e);

        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
                Toast.makeText(VitamioSystemVideoPlayer.this, "我被单击了", Toast.LENGTH_SHORT).show();
            if (isshowMediaController) {
                //隐藏
                hideMediaController();
                //把隐藏消息移除
                handler.removeMessages(HIDE_MEDIACONTROLLER);

            } else {
                //显示
                showMediaController();
                //发消息隐藏
                handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 2000);
            }

            return super.onSingleTapConfirmed(e);
        }
    }

    /**
     * 显示控制面板
     */
    private void showMediaController() {
        media_controller.setVisibility(View.VISIBLE);
        isshowMediaController = true;
    }


    /**
     * 隐藏控制面板
     */
    private void hideMediaController() {
        media_controller.setVisibility(View.GONE);
        isshowMediaController = false;
    }

    private void setFullScreenAndDefault() {
        if (isFullScreen) {
            //默认
            setVideoType(DEFAULT_SCREEN);
        } else {
            //全屏
            setVideoType(FULL_SCREEN);
        }
    }

    private void setVideoType(int defaultScreen) {
        switch (defaultScreen) {
            case FULL_SCREEN://全屏
                //1.设置视频画面的大小-屏幕有多大就是多大
                system_vedio_player.setVideoSize(screenWidth, screenHeight);
                //2.设置按钮的状态-默认
                btnVideoSwitchScreen.setBackgroundResource(R.drawable.btn_video_switch_default_full_screen_seletor);
                isFullScreen = true;
                break;
            case DEFAULT_SCREEN://默认
                //1.设置视频画面的大小
                //视频真实的宽和高
                int mVideoWidth = videoWidth;
                int mVideoHeight = videoHeight;

                //屏幕的宽和高
                int width = screenWidth;
                int height = screenHeight;

                // for compatibility, we adjust size based on aspect ratio
                if (mVideoWidth * height < width * mVideoHeight) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * mVideoWidth / mVideoHeight;
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * mVideoHeight / mVideoWidth;
                }

                system_vedio_player.setVideoSize(width, height);
                //2.设置按钮的状态--全屏
                btnVideoSwitchScreen.setBackgroundResource(R.drawable.btn_video_switch_screen_seletor);
                isFullScreen = false;
                break;
        }
    }


    private void startAndPause() {
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

        //音量的 seekbar
        seekbarVoice.setOnSeekBarChangeListener(new VoiceOnSeekBarChangeListener());

        if (isUseSystem) {
            //监听视频播放卡-系统的api
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                //监听卡
                system_vedio_player.setOnInfoListener(new MyOnInfoListener());
            }
        }

    }
    class MyOnInfoListener implements MediaPlayer.OnInfoListener {

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            switch (what) {
                case MediaPlayer.MEDIA_INFO_BUFFERING_START://视频卡了，拖动卡
//                    Toast.makeText(SystemVideoPlayer.this, "卡了", Toast.LENGTH_SHORT).show();
                    ll_buffer.setVisibility(View.VISIBLE);
                    break;

                case MediaPlayer.MEDIA_INFO_BUFFERING_END://视频卡结束了，拖动卡结束了
//                    Toast.makeText(SystemVideoPlayer.this, "卡结束了", Toast.LENGTH_SHORT).show();
                    ll_buffer.setVisibility(View.GONE);
                    break;
            }
            return true;
        }
    }

    class VoiceOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                if (progress > 0) {
                    isMute = false;
                } else {
                    isMute = true;
                }
                updataVoice(progress, isMute);
            }

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            //触碰的时候
            handler.removeMessages(HIDE_MEDIACONTROLLER);

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 2000);
        }
    }

    /**
     * 设置音量的大小
     *
     * @param progress
     */
    private void updataVoice(int progress, boolean isMute) {
        if (isMute) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);  //最后一个参数flag：1的话调出系统的调节声音的，0的话不调
            seekbarVoice.setProgress(0);
        } else {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            seekbarVoice.setProgress(progress);
            currentVoice = progress;
        }
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
            videoWidth = mp.getVideoWidth();
            videoHeight = mp.getVideoHeight();
            //让VideoView开始播放
            system_vedio_player.start();
            //获取当前视频总时长,这个第一种方式，还有另外一种方式是： mp.getDuration();
            int vedio_playerDuration = (int) system_vedio_player.getDuration();
            //1、设置seekbar的总时长，关联上视频
            seekbarVideo.setMax(vedio_playerDuration);
            //2、设置视频总时长显示
            tvDuration.setText( utils.stringForTime(vedio_playerDuration));
            //3、发送消息告诉handler更新进度
            handler.sendEmptyMessage(PROGRESS);

            //屏幕的默认播放
            setVideoType(DEFAULT_SCREEN);
            //把加载页面消失掉
            ll_loading.setVisibility(View.GONE);

            //拖动完成的监听，可以用于做一些监听用户习惯，比如经常拖动什么位置
//                mp.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
//                @Override
//                public void onSeekComplete(MediaPlayer mp) {
//                    Toast.makeText(SystemVideoPlayer.this, "拖动完成", Toast.LENGTH_SHORT).show();
//                }
//            });
        }
    }


    /**
     * 监听播放出错的class
     * 如果出错会默认弹出一个对话框，但是这里增加多一个toash
     */
    class MyOnErrorListener implements MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Toast.makeText(VitamioSystemVideoPlayer.this, "视频文件错误！", Toast.LENGTH_SHORT).show();
//            showErrorDialog();
            //1.播放的视频格式不支持--跳转到万能播放器继续播放
//            startVitamioPlayer();
            //2.播放网络视频的时候，网络中断---1.如果网络确实断了，可以提示用于网络断了；2.网络断断续续的，重新播放
            //3.播放的时候本地文件中间有空白---下载做完成
            Log.d("onError",mp.toString());
            return true;
        }
    }

//    private void showErrorDialog() {
//        AlertDialog.Builder builder=new AlertDialog.Builder(VitamioSystemVideoPlayer.this);
//        builder.setTitle("播放出错了");
//        builder.setMessage("抱歉，无法播放该视频！！");
//        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                finish();
//            }
//        });
//        builder.show();
//    }

    /**
     * 播放完成的監聽，播放完後顯示name播放完成
     */
    class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            playNextVideo();
           // Toast.makeText(SystemVideoPlayer.this, "播放完成了", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        //释放资源的时候要先释放子类的再释放父类的
        if(myBroadcastReceiver !=null){
            unregisterReceiver(myBroadcastReceiver);
            myBroadcastReceiver=null;
        }

        super.onDestroy();
    }

    /**
     * 监听物理健，实现声音的调节大小
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            currentVoice--;
            updataVoice(currentVoice, false);
            handler.removeMessages(HIDE_MEDIACONTROLLER);
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            currentVoice++;
            updataVoice(currentVoice, false);
            handler.removeMessages(HIDE_MEDIACONTROLLER);
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
