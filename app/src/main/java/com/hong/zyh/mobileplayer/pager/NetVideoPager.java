package com.hong.zyh.mobileplayer.pager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hong.zyh.mobileplayer.R;
import com.hong.zyh.mobileplayer.activity.SystemVideoPlayer;
import com.hong.zyh.mobileplayer.adapter.NetVideoPagerAdapter;
import com.hong.zyh.mobileplayer.base.BasePager;
import com.hong.zyh.mobileplayer.bean.MediaItem;
import com.hong.zyh.mobileplayer.utils.Constants;
import com.hong.zyh.mobileplayer.utils.LogUtil;
import com.hong.zyh.mobileplayer.view.XListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by shuaihong on 2019/1/3.
 * 网络视频页面
 */

public class NetVideoPager extends BasePager {
    @ViewInject(R.id.listview)
    private XListView mListView;

    @ViewInject(R.id.tv_nonet)
    private TextView mTv_nonet;

    @ViewInject(R.id.pb_loading)
    private ProgressBar mProgressBar;

    //存放每一个MedaiItem的集合，方便listview获取数据
    ArrayList<MediaItem> mediaItems=new ArrayList<MediaItem>();

    private NetVideoPagerAdapter adapter;

    /**
     * 是否已经加载更多了
     */
    private boolean isLoadMore = false;


    public NetVideoPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        LogUtil.e("网络视频页面initView");
        View view = View.inflate(context, R.layout.net_video_pager, null);
        x.view().inject(NetVideoPager.this, view);
        mListView.setOnItemClickListener(new MyOnItemClickListener());
        mListView.setPullLoadEnable(true);
        mListView.setXListViewListener(new MyIXListViewListener());
        return view;
    }


    class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            //3.传递列表数据-对象-序列化
            Intent intent = new Intent(context,SystemVideoPlayer.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("videolist",mediaItems);
            intent.putExtras(bundle);
            intent.putExtra("position",position-1);
            context.startActivity(intent);

        }
    }

    class MyIXListViewListener implements XListView.IXListViewListener {
        @Override
        public void onRefresh() {
            getDataFromNet();
        }

        @Override
        public void onLoadMore() {

            getMoreDataFromNet();
        }
    }
    private void getMoreDataFromNet() {
        //联网
        //视频内容
        RequestParams params = new RequestParams(Constants.NET_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("联网成功==" + result);
                isLoadMore = true;
                //主线程
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("getMoreDataFromNet联网失败==" + ex.getMessage());
                isLoadMore = false;
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("onCancelled==" + cex.getMessage());
                isLoadMore = false;
            }

            @Override
            public void onFinished() {
                LogUtil.e("onFinished==");
                isLoadMore = false;
            }
        });
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("网络视频页面initData");
        getDataFromNet();
    }

    private void getDataFromNet() {
        //联网。视频聊天
        RequestParams params= new RequestParams(Constants.NET_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("联网成功==" + result);

                //主线程，解析数据
                processData(result);
                isLoadMore = true;
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("getDataFromNet联网失败==" + ex.getMessage());
                isLoadMore = false;
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("onCancelled==" + cex.getMessage());
                isLoadMore = false;
            }

            @Override
            public void onFinished() {
                LogUtil.e("onFinished==");
                isLoadMore = false;
            }
        });
    }

    private void onLoad() {
        LogUtil.e("onLoad");
        mListView.stopRefresh();
        mListView.stopLoadMore();
        mListView.setRefreshTime("更新时间:"+getSysteTime());
    }
    /**
     * 得到系统时间
     *
     * @return
     */
    public String getSysteTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }
    private void processData(String json) {
        LogUtil.e("processData");
        LogUtil.e("isLoadMore:"+isLoadMore);

        if (!isLoadMore){
        mediaItems = parseJson(json);
            showData();
        }else{
            //加载更多
            //要把得到更多的数据，添加到原来的集合中
//            ArrayList<MediaItem> moreDatas = parseJson(json);
            isLoadMore = false;
            mediaItems.addAll(parseJson(json));
            LogUtil.e("ediaItems.size"+mediaItems.size());
            //刷新适配器
            adapter.notifyDataSetChanged();
            onLoad();
        }
    }

    private void showData() {
        //设置适配器
        if(mediaItems != null && mediaItems.size() >0){
            //有数据
            //设置适配器
            adapter = new NetVideoPagerAdapter(context,mediaItems);
            mListView.setAdapter(adapter);
            onLoad();
            //把文本隐藏
            mTv_nonet.setVisibility(View.GONE);
        }else{
            //没有数据
            //文本显示
            mTv_nonet.setVisibility(View.VISIBLE);
        }


        //ProgressBar隐藏
        mProgressBar.setVisibility(View.GONE);
    }
    /**
     * 解决json数据：
     * 1.用系统接口解析json数据
     * 2.使用第三方解决工具（Gson,fastjson）
     * @param json
     * @return
     */
    private ArrayList<MediaItem> parseJson(String json) {
        ArrayList<MediaItem> mediaItems = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.optJSONArray("trailers");
            if(jsonArray!= null && jsonArray.length() >0){

                for (int i=0;i<jsonArray.length();i++){

                    JSONObject jsonObjectItem = (JSONObject) jsonArray.get(i);

                    if(jsonObjectItem != null){

                        MediaItem mediaItem = new MediaItem();


                        String movieName = jsonObjectItem.optString("movieName");//name
                        mediaItem.setName(movieName);

                        String videoTitle = jsonObjectItem.optString("videoTitle");//desc
                        mediaItem.setDesc(videoTitle);

                        String imageUrl = jsonObjectItem.optString("coverImg");//imageUrl
                        mediaItem.setImageUrl(imageUrl);

                        String hightUrl = jsonObjectItem.optString("hightUrl");//data
                        mediaItem.setData(hightUrl);

                        //把数据添加到集合
                        mediaItems.add(mediaItem);
                    }
                }
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }
LogUtil.e("parseJson-mediaItems.size"+mediaItems.size());
        return mediaItems;
    }
}
