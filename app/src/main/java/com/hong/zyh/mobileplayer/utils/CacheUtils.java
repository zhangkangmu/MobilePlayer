package com.hong.zyh.mobileplayer.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.hong.zyh.mobileplayer.service.MusicPlayerService;

/**
 * Created by hong on 2019/8/10.
 * 缓存工具类
 */

public class CacheUtils {

    /**
     * 保存网络缓存数据
     * @param context
     * @param key
     * @param values
     */
    public static void putString(Context context ,String key ,String values){
        SharedPreferences cache = context.getSharedPreferences("XXZCache", Context.MODE_PRIVATE);
        cache.edit().putString(key,values).commit();
    }
    /**
     * 得到缓存的数据
     * @param context
     * @param key
     * @return
     */
    public static String getString(Context context,String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences("XXZCache",Context.MODE_PRIVATE);
        return  sharedPreferences.getString(key,"");
    }
    /**
     * 设置播放模式
     * @param context
     * @param key
     * @return
             */
    public static void putPlaymode(Context context, String key, int values) {
        SharedPreferences preferences = context.getSharedPreferences("XXZCache", Context.MODE_PRIVATE);
        preferences.edit().putInt(key,values).commit();
    }
    /**
     * 获取播放模式
     * @param context
     * @param key
     * @return
     */
    public static int getPlaymode(Context context,String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences("XXZCache",Context.MODE_PRIVATE);
        return  sharedPreferences.getInt(key, MusicPlayerService.REPEAT_NORMAL);
    }
}

