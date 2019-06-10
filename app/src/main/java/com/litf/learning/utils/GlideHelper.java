package com.litf.learning.utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.litf.learning.R;
import com.litf.learning.control.CodeConstants;

public class GlideHelper {

    public static void showAvatarWithUrl(@Nullable Context context, String url, @Nullable ImageView imageView) {
        Glide.with(context)
                .load(CodeConstants.URL_Query + url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.mipmap.pictures_no)
                .error(R.mipmap.pictures_no)
                .transform(new GlideCircleTransform(context))
                .into(imageView);
    }

    public static void showImageWithUrl(@Nullable Context context, String url, @Nullable ImageView imageView) {
        Glide.with(context)
                .load(CodeConstants.URL_Query + url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.mipmap.pictures_no)
                .error(R.mipmap.pictures_no)
                .dontAnimate()
                .into(imageView);
        LogUtil.i("GlideHelper", "imageurl=" + CodeConstants.URL_Query + url);
    }

    public static void showImageWithFullUrl(@Nullable Context context, String url, @Nullable ImageView imageView) {
        Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.mipmap.pictures_no)
                .error(R.mipmap.pictures_no)
                .dontAnimate()
                .into(imageView);
    }

    public static void showBannerWithUrl(int tag, @Nullable Context context, String url, @Nullable ImageView imageView) {
        if (tag == 1) {
            Glide.with(context)
                    .load(CodeConstants.URL_Query + url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.mipmap.pictures_no)
                    .error(R.mipmap.pictures_no)
                    .dontAnimate()
                    .into(imageView);
        } else {
            Glide.with(context)
                    .load(CodeConstants.URL_Query + url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.mipmap.pictures_no)
                    .error(R.mipmap.pictures_no)
                    .dontAnimate()
                    .into(imageView);
        }
    }
}
