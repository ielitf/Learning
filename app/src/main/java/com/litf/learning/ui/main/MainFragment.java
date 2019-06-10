package com.litf.learning.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.alibaba.fastjson.JSON;
import com.litf.learning.R;
import com.litf.learning.adapter.VrVideoAdapter;
import com.litf.learning.bean.VideoItem;
import com.litf.learning.control.CodeConstants;
import com.litf.learning.listener.MyListener;
import com.litf.learning.widget.PullToRefreshLayout;
import com.litf.learning.widget.pullableview.PullableGridView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;

public class MainFragment extends Fragment {
    private Context context;
    private PullToRefreshLayout pullToRefreshLayout;
    private PullableGridView gridView;
    private View contentView;
    private VrVideoAdapter adapter;
    private List<VideoItem> videoItems;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.fragment_main, container, false);
        context = getActivity();
        pullToRefreshLayout = contentView.findViewById(R.id.refresh_view);
        gridView = contentView.findViewById(R.id.content_view);

        pullToRefreshLayout.setOnRefreshListener(new MyListener(){});
        OkGo.<String>get(CodeConstants.URL_Query).cacheKey(CodeConstants.URL_Query).cacheMode(CacheMode.DEFAULT).execute(new StringCallback() {

            @Override
            public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                Log.e("=====ssss", response.body());
                if (response.code() == 200) {
                    try {
                        JSONObject obj = new JSONObject(response.body());
                        String content = obj.getString("content");
                        videoItems = JSON.parseArray(content, VideoItem.class);
                        gridView.setAdapter(new VrVideoAdapter(context, videoItems));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        return contentView;
    }

}
