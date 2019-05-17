package com.litf.learning.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.litf.learning.R;
import com.litf.learning.base.MyBaseAdapter;
import com.litf.learning.bean.VideoItem;

import java.util.List;

public class VrVideoAdapter extends MyBaseAdapter<VideoItem> {
    private LayoutInflater inflater;
    private Context context;
    public VrVideoAdapter(Context context, List<VideoItem> mData) {
        super(context, mData);
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    protected View newView(Context context, int position, ViewGroup parentView) {
        ViewHolder holderView = new ViewHolder();
        View convertView = inflater.inflate(R.layout.vr_video_list_item, null, false);
        holderView.vrVideoName = convertView.findViewById(R.id.topic_init_title);
        holderView.vrVideoType = convertView.findViewById(R.id.channel_name);
        holderView.data =  convertView.findViewById(R.id.date_text);
        holderView.vrVideoPicture = convertView.findViewById(R.id.topic_init_img);
        convertView.setTag(holderView);
        return convertView;
    }

    @Override
    protected void bindView(Context context, View view, int position, VideoItem model) {
        ViewHolder holderView = (ViewHolder) view.getTag();
        holderView.vrVideoName.setText(model.getTitle());
        holderView.vrVideoType.setText(model.getType());
        holderView.data.setText(model.getDateCnSimple());
        holderView.vrVideoUrl =  model.getPlay();
        holderView.picUrl =  model.getImg();
        Glide.with(context).load(holderView.picUrl).into(holderView.vrVideoPicture);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        private TextView vrVideoName,vrVideoType,data;
        private ImageView vrVideoPicture;
        private String vrVideoUrl,picUrl;
    }
}
