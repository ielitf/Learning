package com.litf.vrlibrary;

import android.content.Context;
import android.content.Intent;

/**
 * ============================================================
 * Copyright： learning
 * Author：   Li Teng fei
 * Email：    ietengfeili@163.com
 * Version：1.0
 * time：2018/10/18 15:15
 * ============================================================
 */
public class VrPlayerManager {
    private volatile static VrPlayerManager instance = null;

    public static VrPlayerManager getInstance() {
        if (instance == null) {
            synchronized (VrPlayerManager.class) {
                if (instance == null) {
                    instance = new VrPlayerManager();
                }
            }
        }
        return instance;
    }

    public void init() {
    }

    private VrPlayerManager() {
    }

    public void playVideo(Context mContext,String videoUrl) {

        Intent intent=new Intent(mContext,VideoPlayerActivity.class);
        intent.putExtra(CodeConstants.VIDEO_URL,videoUrl);
        mContext.startActivity(intent);
    }
    public void playPicture(Context mContext,String pictureUrl) {

        Intent intent=new Intent(mContext,VrImageDetailActivity.class);
        intent.putExtra(CodeConstants.PICTURE_URL,pictureUrl);
        intent.putExtra(CodeConstants.MUSIC_URL,"");
        mContext.startActivity(intent);
    }
    public void playPictureWithMusic(Context mContext,String pictureUrl,String MusicUrl) {

        Intent intent=new Intent(mContext,VrImageDetailActivity.class);
        intent.putExtra(CodeConstants.PICTURE_URL,pictureUrl);
        intent.putExtra(CodeConstants.MUSIC_URL,MusicUrl);
        mContext.startActivity(intent);
    }
}
