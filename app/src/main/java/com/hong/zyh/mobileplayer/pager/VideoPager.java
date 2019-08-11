package com.hong.zyh.mobileplayer.pager;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hong.zyh.mobileplayer.R;
import com.hong.zyh.mobileplayer.activity.SystemVideoPlayer;
import com.hong.zyh.mobileplayer.adapter.VideoPagerAdapter;
import com.hong.zyh.mobileplayer.base.BasePager;
import com.hong.zyh.mobileplayer.bean.MediaItem;
import com.hong.zyh.mobileplayer.utils.LogUtil;

import java.util.ArrayList;

/**
 * Created by shuaihong on 2019/1/3.
 * 本地视频页面
 */

public class VideoPager extends BasePager {
    private ListView listview_video;
    private TextView tv_nomedia;
    private ProgressBar pg_loading;
    //单独维护出一个adapter方便以后修改
   private VideoPagerAdapter videoPagerAdapter;

    //存放每一个MedaiItem的集合，方便listview获取数据
    ArrayList<MediaItem> mediaItems;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mediaItems != null && mediaItems.size() > 0) {
                //有数据就设置适配器
                videoPagerAdapter= new VideoPagerAdapter(context,mediaItems,true);
                listview_video.setAdapter(videoPagerAdapter);
                listview_video.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //1、调用系统的播放器--隐式意图
                        //Intent intent = new Intent();
                        //Uri.parse()需要传入一个地址来展示这个数据
                        //intent.setDataAndType(Uri.parse(mediaItems.get(position).getData()),"vieo/*");
                        //不要忘了加context
                        //context.startActivity(intent);

                        //2、调用自己写的播放器--显示意图
                        //Intent intent = new Intent(context,SystemVideoPlayer.class);
                        //intent会传入一个data路径，而其他的activity可以通过一个getData（）获取
                        //intent.setDataAndType(Uri.parse(mediaItems.get(position).getData()),"vieo/*");
                        //把视频的名字也传过去
                        //intent.putExtra("videoName",mediaItems.get(position).getName());
                        //context.startActivity(intent);

                        //3、传递列表数据--对象--序列化
                        Intent intent = new Intent(context, SystemVideoPlayer.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("videolist", mediaItems);
                        intent.putExtras(bundle);
                        intent.putExtra("position",position);
                        context.startActivity(intent);

                    }
                });
                //把文本隐藏
                tv_nomedia.setVisibility(View.GONE);
            } else {
                //没有数据就显示文本找到不到数据
                tv_nomedia.setVisibility(View.VISIBLE);
                tv_nomedia.setText("沒有數據可加載....");
            }
            pg_loading.setVisibility(View.GONE);
        }
    };

    public VideoPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.video_pager, null);
        listview_video = view.findViewById(R.id.listview_video);
        tv_nomedia = view.findViewById(R.id.tv_nomedia);
        pg_loading = view.findViewById(R.id.pg_loading);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("本地视频按钮页面initData");
        getDataFromLocal();
        LogUtil.e(mediaItems.size()+"");
    }

    /**
     * 从本地加载视频
     * 1.遍历SD卡，后缀名
     * 2.从内容提供者中获取视频
     */
    private void getDataFromLocal() {

        mediaItems = new ArrayList<MediaItem>();
        new Thread() {

            @Override
            public void run() {
                super.run();
                //为了显示出加载的画面睡眠一会
                SystemClock.sleep(500);
                ContentResolver resolver = context.getContentResolver();
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                Log.d("MediaStore",MediaStore.Video.Media.EXTERNAL_CONTENT_URI.getPath());
                String[] objs = {
                        MediaStore.Video.Media.DISPLAY_NAME,//获取视频在sd卡中的名称
                        MediaStore.Video.Media.DURATION,//获取视频总时长
                        MediaStore.Video.Media.SIZE,//获取视频大小
                        MediaStore.Video.Media.DATA,//获取视频绝对地址
                        MediaStore.Video.Media.ARTIST //获取音频的艺术家
                };
                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        //建立一个bean对象封装一个条目视频
                        MediaItem mediaItem = new MediaItem();

                        //视频名称
                        String name = cursor.getString(0);
                        mediaItem.setName(name);
                        LogUtil.e(name);
                        //视频时长
                        long duration = cursor.getLong(1);
                        mediaItem.setDuration(duration);
                        LogUtil.e(duration+"");
                        //视频大小
                        long size = cursor.getLong(2);
                        mediaItem.setSize(size);
                        //视频地址
                        String data = cursor.getString(3);
                        mediaItem.setData(data);
                        //艺术家
                        String artist = cursor.getString(4);
                        mediaItem.setArtist(artist);

                        mediaItems.add(mediaItem);
                    }
                    cursor.close();
                }
                handler.sendEmptyMessage(10);
            }
        }.start();
    }
}
