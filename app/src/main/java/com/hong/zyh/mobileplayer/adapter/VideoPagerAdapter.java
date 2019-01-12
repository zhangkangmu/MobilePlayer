package com.hong.zyh.mobileplayer.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hong.zyh.mobileplayer.R;
import com.hong.zyh.mobileplayer.bean.MediaItem;
import com.hong.zyh.mobileplayer.pager.VideoPager;
import com.hong.zyh.mobileplayer.utils.Utils;

import java.util.ArrayList;

/**
 * Created by shuaihong on 2019/1/12.
 */

public class VideoPagerAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<MediaItem> mediaItems;
    private Utils utils;

    public VideoPagerAdapter(Context context, ArrayList<MediaItem> mediaItems) {
        this.context = context;
        this.mediaItems = mediaItems;
        utils=new Utils();
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

    public static class ViewHoder {
        ImageView iv_item_video;
        TextView tv_video_name;
        TextView tv_video_time;
        TextView tv_video_size;
    }
}
