package com.litf.learning.ui.middle;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.listener.OnBottomLoadMoreTime;
import com.andview.refreshview.utils.LogUtils;
import com.litf.learning.R;
import com.litf.learning.adapter.VrVideoAdapter;
import com.litf.learning.bean.VideoItem;
import com.litf.learning.control.CodeConstants;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MiddleFragment extends Fragment {
    private XRefreshView xRefreshView;
    private ListView listView;
    private View contentView;
    private VrVideoAdapter adapter;
    private List<VideoItem> videoItems;
    public static long lastRefreshTime;
    public MiddleFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.fragment_middle,container,false);
        xRefreshView = contentView.findViewById(R.id.custom_view_lv);
        listView = contentView.findViewById(R.id.middle_listView);
        loadData();
        xRefreshView.setPullRefreshEnable(true);// 设置是否可以下拉刷新,默认为true
        xRefreshView.setPullLoadEnable(true); // 设置是否可以上拉加载,默认为false
        xRefreshView.setMoveHeadWhenDisablePullRefresh(false);//默认为true，当下拉刷新被禁用时，调用这个方法并传入false可以不让头部被下拉
        xRefreshView.setMoveFootWhenDisablePullLoadMore(false);//默认为true
        xRefreshView.setAutoRefresh(false);// 设置时候可以自动刷新
        xRefreshView.setAutoLoadMore(false);// 设置时候可以自动刷新
        // 设置上次刷新的时间
        xRefreshView.restoreLastRefreshTime(lastRefreshTime);
        xRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener(){
            @Override
            public void onRefresh(final boolean isPullDown) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.w("=====下拉", "isPullDown"+isPullDown);
                        xRefreshView.stopRefresh();
                        lastRefreshTime = xRefreshView.getLastRefreshTime();
                    }
                }, 2000);
            }

            @Override
            public void onLoadMore(final boolean isSilence) {
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        Log.w("=====上拉", "isSilence"+isSilence);
                        xRefreshView.stopLoadMore();
                    }
                }, 2000);
            }

            @Override
            public void onRelease(float direction) {

                if (direction > 0) {
                    Log.w("=====下拉", "下拉");
//                    toast("下拉");
                } else {
                    Log.w("=====上拉", "上拉");
//                    toast("上拉");
                }
                super.onRelease(direction);
            }
        });

//        xRefreshView.setOnAbsListViewScrollListener(new AbsListView.OnScrollListener() {
//
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                Log.e("=====StateChanged", "onScrollStateChanged");
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem,
//                                 int visibleItemCount, int totalItemCount) {
//                Log.e("=====onScroll", "onScroll");
//            }
//        });

        return contentView;
    }

    private void loadData() {
        OkGo.<String>get(CodeConstants.URL_Query).cacheKey(CodeConstants.URL_Query).cacheMode(CacheMode.DEFAULT).execute(new StringCallback() {
            @Override
            public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                Log.e("=====ssss", response.body());
                if (response.code() == 200) {
                    try {
                        JSONObject obj = new JSONObject(response.body());
                        String content = obj.getString("content");
                        videoItems = JSON.parseArray(content, VideoItem.class);
                        listView.setAdapter(new VrVideoAdapter(getActivity(), videoItems));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


    }
    public void toast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
}
