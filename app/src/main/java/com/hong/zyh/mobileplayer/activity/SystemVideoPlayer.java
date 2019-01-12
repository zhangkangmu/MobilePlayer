package com.hong.zyh.mobileplayer.activity;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.hong.zyh.mobileplayer.R;

/**
 * Created by shuaihong on 2019/1/12.
 * 自己创建的系统播放器
 */

public class SystemVideoPlayer extends Activity {
    private Uri uri;
    private VideoView system_vedio_player;
    //videopager傳過來的數據
    private String videoName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_video_player);
        system_vedio_player = findViewById(R.id.system_vedio_player);
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
        //設置控制面板
        system_vedio_player.setMediaController(new MediaController(this));
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
