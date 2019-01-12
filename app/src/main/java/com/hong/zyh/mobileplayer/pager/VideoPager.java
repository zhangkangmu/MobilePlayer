package com.hong.zyh.mobileplayer.pager;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hong.zyh.mobileplayer.R;
import com.hong.zyh.mobileplayer.base.BasePager;
import com.hong.zyh.mobileplayer.bean.MediaItem;
import com.hong.zyh.mobileplayer.utils.LogUtil;
import com.hong.zyh.mobileplayer.utils.Utils;

import java.util.ArrayList;

/**
 * Created by shuaihong on 2019/1/3.
 * 本地视频页面
 */

public class VideoPager extends BasePager {
    private ListView listview_video;
    private TextView tv_nomedia;
    private ProgressBar pg_loading;
    private Utils utils;

    //存放每一个MedaiItem的集合，方便listview获取数据
    ArrayList<MediaItem> mediaItems;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mediaItems != null && mediaItems.size() > 0) {
                //有数据就设置适配器
                listview_video.setAdapter(new VideoPagerAdapter());
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
        utils = new Utils();
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
                ContentResolver resolver = context.getContentResolver();
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
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

    class VideoPagerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mediaItems.size();
        }

        @Override
        public MediaItem getItem(int position) {
            return mediaItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHoder viewHoder;
            if (convertView == null) {
                viewHoder = new ViewHoder();
                convertView = View.inflate(context, R.layout.item_video_pager, null);
                viewHoder.iv_item_video = convertView.findViewById(R.id.iv_item_video);
                viewHoder.tv_video_name = convertView.findViewById(R.id.tv_video_name);
                viewHoder.tv_video_time = convertView.findViewById(R.id.tv_video_time);
                viewHoder.tv_video_size = convertView.findViewById(R.id.tv_video_size);
                convertView.setTag(viewHoder);
            } else {
                viewHoder = (ViewHoder) convertView.getTag();
            }
            MediaItem mediaItem = mediaItems.get(position);
            viewHoder.tv_video_name.setText(mediaItem.getName());
            viewHoder.tv_video_time.setText(utils.stringForTime((int) (mediaItem.getDuration())));
            viewHoder.tv_video_size.setText(Formatter.formatFileSize(context, mediaItem.getDuration()));
            return convertView;
        }
    }

    static class ViewHoder {
        ImageView iv_item_video;
        TextView tv_video_name;
        TextView tv_video_time;
        TextView tv_video_size;
    }
}
