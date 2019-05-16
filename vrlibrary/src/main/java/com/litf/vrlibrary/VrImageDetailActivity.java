package com.litf.vrlibrary;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.BitmapCallback;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Response;

/**
 * ============================================================
 * Copyright： learning
 * Author：   Li Teng fei
 * Email：    ietengfeili@163.com
 * Version：1.0
 * time：2018/10/18 15:15
 * ============================================================
 */
public class VrImageDetailActivity extends AppCompatActivity {

	private VrPanoramaView vrPano;
	private View loading;
	private MediaPlayer player;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_detail);
		init();
	}

	private void init() {
		initActionBar();
		initPanoView();
	}

	private void initPanoView() {
		vrPano = (VrPanoramaView) findViewById(R.id.vr_pano);
        vrPano.setInfoButtonEnabled(false);//隐藏信息按钮

		loading = findViewById(R.id.pb_loading);
		Intent intent = getIntent();
		String pictureUrl = intent.getStringExtra(CodeConstants.PICTURE_URL);
		String musicUrl = intent.getStringExtra(CodeConstants.MUSIC_URL);
		initPlayer(musicUrl);
		OkGo.get(pictureUrl).cacheKey(pictureUrl).execute(new BitmapCallback() {

			@Override
			public void onSuccess(Bitmap bitmap, Call call, Response response) {
				loading.setVisibility(View.GONE);
				VrPanoramaView.Options options = new VrPanoramaView.Options();
				options.inputType = VrPanoramaView.Options.TYPE_MONO;
				vrPano.loadImageFromBitmap(bitmap, options);
			}
		});

	}

	private void initPlayer(String musicUrl) {
		if (musicUrl != null) {
			player = new MediaPlayer();

			player.setAudioStreamType(AudioManager.STREAM_MUSIC);
			try {
				player.setDataSource(this, Uri.parse(musicUrl));
				player.prepare();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		vrPano.resumeRendering();
		if (player != null) {
			player.start();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		vrPano.pauseRendering();
		if (player != null) {
			player.pause();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		vrPano.shutdown();
		if (player != null) {
			player.stop();
			player.release();
			player = null;
		}
	}

	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
