package com.hong.zyh.mobileplayer.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hong.zyh.mobileplayer.R;
import com.hong.zyh.mobileplayer.bean.MediaItem;
import com.hong.zyh.mobileplayer.utils.Utils;

import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by shuaihong on 2019/1/12.
 * 网络视频pager的adapter
 */

public class NetVideoPagerAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<MediaItem> mediaItems;

    public NetVideoPagerAdapter(Context context, ArrayList<MediaItem> mediaItems) {
        this.context = context;
        this.mediaItems = mediaItems;
    }

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
            convertView = View.inflate(context, R.layout.item_net_video_pager, null);
            viewHoder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            viewHoder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHoder.tv_desc = (TextView) convertView.findViewById(R.id.tv_desc);

            convertView.setTag(viewHoder);
        } else {
            viewHoder = (ViewHoder) convertView.getTag();
        }
        MediaItem mediaItem = mediaItems.get(position);
        viewHoder.tv_name.setText(mediaItem.getName());
        viewHoder.tv_desc.setText(mediaItem.getDesc());
//        1.使用xUtils3请求图片
        x.image().bind(viewHoder.iv_icon,mediaItem.getImageUrl());
//        2.使用glide加载图片
        Glide.with(context).load(mediaItem.getImageUrl()).
                diskCacheStrategy(DiskCacheStrategy.ALL)   //可换成不同尺寸的图片，可以比如说第一个也页面是200*200，第二个是100*100，那就不用加载两次了
                .placeholder(R.mipmap.ic_launcher)    //默认加载
                .error(R.mipmap.ic_launcher)          //失败加载的图片
                .into(viewHoder.iv_icon);
        return convertView;
    }


    static class ViewHoder{
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_desc;
    }
}
