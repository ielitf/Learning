package com.litf.learning.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.litf.learning.R;
import com.litf.learning.base.MyBaseAdapter;
import com.litf.learning.bean.VideoItem;

import java.util.List;

public class CeshiAdapter extends MyBaseAdapter <VideoItem>{
    private LayoutInflater inflater;
    public CeshiAdapter(Context context, List<VideoItem> data) {
        super(context, data);
        inflater= LayoutInflater.from(context);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.vr_video_list_item,null);
            viewHolder.control = convertView.findViewById(R.id.topic_init_title);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.control.setText("");

        return convertView;
    }
    class ViewHolder {
        TextView title;
        TextView startTime;
        TextView endTime;
        TextView control;
    }
}
