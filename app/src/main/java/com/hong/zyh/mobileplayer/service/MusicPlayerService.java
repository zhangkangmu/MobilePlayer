package com.hong.zyh.mobileplayer.service;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.hong.demo.IMusicPlayerService;
import com.hong.zyh.mobileplayer.R;
import com.hong.zyh.mobileplayer.activity.AudioPlayerActivity;
import com.hong.zyh.mobileplayer.bean.MediaItem;
import com.hong.zyh.mobileplayer.utils.CacheUtils;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by hong on 2019/8/10.
 */

public class MusicPlayerService extends Service {
    public static final String OPENAUDIO = "com.hong.zyh.mobileplayer_OPENAUDIO";
    private ArrayList<MediaItem> mediaItems;
    private int position;
    /**
     * 播放音乐
     */
    private MediaPlayer mediaPlayer;
    /**
     * 当前播放的音频文件对象
     */
    private MediaItem mediaItem;

    /**
     * 顺序播放
     */
    public static final int REPEAT_NORMAL = 1;
    /**
     * 单曲循环
     */
    public static final int REPEAT_SINGLE = 2;
    /**
     * 全部循环
     */
    public static final int REPEAT_ALL = 3;

    /**
     * 播放模式
     */
    private int playmode = REPEAT_NORMAL;

    //中间人对象
    private IMusicPlayerService.Stub stub = new IMusicPlayerService.Stub() {
        MusicPlayerService service = MusicPlayerService.this;

        @Override
        public void openAudio(int position) throws RemoteException {
            service.openAudio(position);
        }

        @Override
        public void start() throws RemoteException {
            service.start();
        }

        @Override
        public void pause() throws RemoteException {
            service.pause();
        }

        @Override
        public void stop() throws RemoteException {
            service.stop();
        }

        @Override
        public int getCurrentPosition() throws RemoteException {
            return service.getCurrentPosition();
        }

        @Override
        public int getDuration() throws RemoteException {
            return service.getDuration();
        }

        @Override
        public String getArtist() throws RemoteException {
            return service.getArtist();
        }

        @Override
        public String getName() throws RemoteException {
            return service.getName();
        }

        @Override
        public String getAudioPath() throws RemoteException {
            return service.getAudioPath();
        }

        @Override
        public void next() throws RemoteException {
            service.next();
        }

        @Override
        public void openNextAudio() throws RemoteException {
            service.openNextAudio();
        }

        @Override
        public void setNextPosition() throws RemoteException {
            service.setNextPosition();
        }

        @Override
        public void pre() throws RemoteException {
            service.pre();
        }

        @Override
        public void openPreAudio() throws RemoteException {
            service.openPreAudio();
        }

        @Override
        public void setPrePosition() throws RemoteException {
            service.setPrePosition();
        }

        @Override
        public void setPlayMode(int playmode) throws RemoteException {
            service.setPlayMode(playmode);
        }

        @Override
        public int getPlayMode() throws RemoteException {
            return service.getPlayMode();
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return service.isPlaying();
        }

        @Override
        public void seekTo(int progress) throws RemoteException {
            mediaPlayer.seekTo(progress);
        }
    };
//这里结束adil的stub

    @Override
    public void onCreate() {
        super.onCreate();
        playmode= CacheUtils.getPlaymode(this,"playmode");
        //加载音乐列表
        getDataFromLocal();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }

    /**
     * 根据位置打开对应的音频文件,并且播放
     *
     * @param position
     */
    private void openAudio(int position) {
        this.position=position;
        if (mediaItems != null && mediaItems.size() > 0) {

            mediaItem = mediaItems.get(position);
            if (mediaPlayer != null) {
//                mediaPlayer.release();
                //重置多媒体
                mediaPlayer.reset();
                mediaPlayer=null;
            }try {
                mediaPlayer = new MediaPlayer();
                //设置监听：播放出错，播放完成，准备好
                mediaPlayer.setOnPreparedListener(new MyOnPreparedListener());
                mediaPlayer.setOnCompletionListener(new MyOnCompletionListener());
                mediaPlayer.setOnErrorListener(new MyOnErrorListener());
                mediaPlayer.setDataSource(mediaItem.getData());
                //这个不要忘了,异步装载
                mediaPlayer.prepareAsync();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }else {
            Toast.makeText(MusicPlayerService.this, "no music，try again！", Toast.LENGTH_SHORT).show();
        }
    }
    class MyOnErrorListener implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            next();
            return true;
        }
    }

    class  MyOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            next();
        }
    }
    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mp) {
            //这里要通知activity显示艺术家之类的，可以使用广播
            notifyChange(OPENAUDIO);
            start();
        }
    }

    private void notifyChange(String openaudio) {
        Intent intent = new Intent(openaudio);
        sendBroadcast(intent);
    }


    NotificationManager manager;
    /**
     * 开始
     */
    private void start() {
        mediaPlayer.start();
        Intent intent = new Intent(this, AudioPlayerActivity.class);
        intent.putExtra("notification",true);//标识来自状态拦
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1,intent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.notification_music_playing)
                .setContentText(getName())
                .setContentTitle("小象子影音")
                .setContentIntent(pendingIntent)
                .build();
        manager.notify(1,notification);  //参数1：通知的id  参数2：通知
    }

    /**
     * 暂停
     */
    private void pause() {
        mediaPlayer.pause();
    }

    /**
     * 停止
     */
    private void stop() {
        mediaPlayer.stop();
    }

    /**
     * 得到当前的播放进度
     *
     * @return
     */
    private int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }


    /**
     * 得到当前音频的总时长
     *
     * @return
     */
    private int getDuration() {
        return mediaPlayer.getDuration();
    }

    /**
     * 得到艺术家
     *
     * @return
     */
    private String getArtist() {
        return mediaItem.getArtist();
    }

    /**
     * 得到歌曲名字
     *
     * @return
     */
    private String getName() {
        return mediaItem.getName();
    }


    /**
     * 得到歌曲播放的路径
     *
     * @return
     */
    private String getAudioPath() {
        return mediaItem.getData();
    }


    /**
     * 播放下一个音频
     */
    private void next() {

    }

    private void openNextAudio() {

    }

    private void setNextPosition() {

    }

    /**
     * 播放上一个音频
     */
    private void pre() {

    }

    private void openPreAudio() {

    }

    private void setPrePosition() {

    }

    /**
     * 设置播放模式
     *
     * @param playmode
     */
    private void setPlayMode(int playmode) {
        this.playmode = playmode;
        CacheUtils.putPlaymode(this,"playmode",playmode);
    }

    /**
     * 得到播放模式
     *
     * @return
     */
    private int getPlayMode() {
        return playmode;
    }


    /**
     * 是否在播放音频
     *
     * @return
     */
    private boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public void getDataFromLocal() {
        new Thread() {
            @Override
            public void run() {
                super.run();

                mediaItems = new ArrayList<>();
                ContentResolver resolver = getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] objs = {
                        MediaStore.Audio.Media.DISPLAY_NAME,//视频文件在sdcard的名称
                        MediaStore.Audio.Media.DURATION,//视频总时长
                        MediaStore.Audio.Media.SIZE,//视频的文件大小
                        MediaStore.Audio.Media.DATA,//视频的绝对地址
                        MediaStore.Audio.Media.ARTIST,//歌曲的演唱者

                };
                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {

                        MediaItem mediaItem = new MediaItem();

                        mediaItems.add(mediaItem);//写在上面

                        String name = cursor.getString(0);//视频的名称
                        mediaItem.setName(name);

                        long duration = cursor.getLong(1);//视频的时长
                        mediaItem.setDuration(duration);

                        long size = cursor.getLong(2);//视频的文件大小
                        mediaItem.setSize(size);

                        String data = cursor.getString(3);//视频的播放地址
                        mediaItem.setData(data);

                        String artist = cursor.getString(4);//艺术家
                        mediaItem.setArtist(artist);
                    }
                    cursor.close();
                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
