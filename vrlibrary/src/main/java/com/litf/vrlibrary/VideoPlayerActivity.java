package com.litf.vrlibrary;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.vr.sdk.widgets.video.VrVideoEventListener;
import com.google.vr.sdk.widgets.video.VrVideoView;

import java.io.IOException;

public class VideoPlayerActivity extends AppCompatActivity {
    private VrVideoView vr_video;
    private SeekBar seekBar;
    private TextView tv_progress;
    private boolean isPaused = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        Intent intent = getIntent();
        String videoUrl = intent.getStringExtra(CodeConstants.VIDEO_URL);
        vr_video = (VrVideoView) findViewById(R.id.vr_video);
        tv_progress = (TextView) findViewById(R.id.tv_progress);
        seekBar = (SeekBar) findViewById(R.id.seek_bar);
        new VideoLoaderTask().execute(videoUrl);
        vr_video.setEventListener(new VrVideoEventListener(){

            @Override
            public void onLoadError(String errorMessage) {
                Toast.makeText(VideoPlayerActivity.this, "网络连接失败，请检查网络", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLoadSuccess() {
                super.onLoadSuccess();
                Toast.makeText(VideoPlayerActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                seekBar.setMax((int) vr_video.getDuration());
            }

            @Override
            public void onNewFrame() {
                super.onNewFrame();
                seekBar.setProgress((int) vr_video.getCurrentPosition());
                tv_progress.setText("当前进度:"+String.format("%.1f",vr_video.getCurrentPosition()/1000f));
            }

            @Override
            public void onCompletion() {
                Toast.makeText(VideoPlayerActivity.this, "播放结束", Toast.LENGTH_SHORT).show();
                vr_video.seekTo(0);//播放结束后重新开始播放
                vr_video.pauseVideo();
                isPaused = true;
            }

            @Override
            public void onClick() {
                togglePause();//点击暂停或者播放
            }
        });
    }
    private class VideoLoaderTask extends AsyncTask<String,Void,Void> {

        @Override
        protected Void doInBackground(String... params) {
            String videoUrl = params[0];
            VrVideoView.Options options=new VrVideoView.Options();
            options.inputType=VrVideoView.Options.TYPE_MONO;
            options.inputFormat=VrVideoView.Options.FORMAT_DEFAULT;
            try {
                vr_video.loadVideo(Uri.parse(videoUrl),options);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private void togglePause() {
        if (isPaused) {
            vr_video.playVideo();
            Toast.makeText(VideoPlayerActivity.this, "播放", Toast.LENGTH_SHORT).show();
        } else {
            vr_video.pauseVideo();
            Toast.makeText(VideoPlayerActivity.this, "暂停", Toast.LENGTH_SHORT).show();
        }
        isPaused = !isPaused;
    }
    @Override
    protected void onResume() {
        super.onResume();
        vr_video.resumeRendering();
    }

    @Override
    protected void onPause() {
        super.onPause();
        vr_video.pauseRendering();
        isPaused = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        vr_video.shutdown();
    }
}
