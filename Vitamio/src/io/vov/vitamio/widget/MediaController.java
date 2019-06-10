/*
 * Copyright (C) 2006 The Android Open Source Project
 * Copyright (C) 2013 YIXIA.COM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.vov.vitamio.widget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import java.lang.reflect.Method;

import io.vov.vitamio.R;
import io.vov.vitamio.utils.Log;
import io.vov.vitamio.utils.StringUtils;
import io.vov.vitamio.verticalseekbar.VerticalSeekBar;

/**
 * A view containing controls for a MediaPlayer. Typically contains the buttons
 * like "Play/Pause" and a progress slider. It takes care of synchronizing the
 * controls with the state of the MediaPlayer.
 * <p/>
 * The way to use this class is to a) instantiate it programatically or b)
 * create it in your xml layout.
 * <p/>
 * a) The MediaController will create a default set of controls and put them in
 * a window floating above your application. Specifically, the controls will
 * float above the view specified with setAnchorView(). By default, the window
 * will disappear if left idle for three seconds and reappear when the user
 * touches the anchor view. To customize the MediaController's style, layout and
 * controls you should extend MediaController and override the {#link
 * {@link #makeControllerView()} method.
 * <p/>
 * b) The MediaController is a FrameLayout, you can put it in your layout xml
 * and get it through {@link #findViewById(int)}.
 * <p/>
 * NOTES: In each way, if you want customize the MediaController, the SeekBar's
 * id must be mediacontroller_progress, the Play/Pause's must be
 * mediacontroller_pause, current time's must be mediacontroller_time_current,
 * total time's must be mediacontroller_time_total, file name's must be
 * mediacontroller_file_name. And your resources must have a pause_button
 * drawable and a play_button drawable.
 * <p/>
 * Functions like show() and hide() have no effect when MediaController is
 * created in an xml layout.
 */
public class MediaController extends FrameLayout {
    private static final int sDefaultTimeout = 3000;
    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;
    private MediaPlayerControl mPlayer;
    private Context mContext;
    private PopupWindow mWindow;
    private int mAnimStyle;
    private View mAnchor;
    private View mRoot;
    private SeekBar mProgress;
    private TextView mEndTime, mCurrentTime;
    private TextView mFileName;
    private OutlineTextView mInfoView;
    private String mTitle;
    private long mDuration;
    private boolean mShowing;
    private boolean mDragging;
    private boolean mInstantSeeking = false;
    private boolean mFromXml = false;
    private ImageButton mPauseButton;
    private AudioManager mAM;
    private OnShownListener mShownListener;
    private OnHiddenListener mHiddenListener;

    private ImageView mPlayLandscape;
    private ImageView mPlayBackButton;
    private OnControllerListener mController;
    private TextView titleTextView;
    private TextView mPlayLockButton;
    private View controlTitle, controlBar;
    private RelativeLayout bottomLayout;
    private LinearLayout soundlayout;

    private int mLastVolumn;
    private ImageView ivVolumn;
    private VerticalSeekBar seekVolumn;
    private TextView sound_text;
    private boolean isHorizontalScroll=false;

    private MyScrollView2 gesture;
    private static final String VOLUME = "VOLUME";
    private static final String MUTE = "MUTE";

    private Boolean isLocked = false;
    private long myprogress;
    private long nowposition;


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            long pos;
            switch (msg.what) {
                case FADE_OUT:
                    hide();
                    break;
                case SHOW_PROGRESS:
                    pos = setProgress();
                    if (!mDragging && mShowing) {
                        msg = obtainMessage(SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                        updatePausePlay();
                    }
                    break;
            }
        }
    };
    private View.OnClickListener mPauseListener = new View.OnClickListener() {
        public void onClick(View v) {
            doPauseResume();
            show(sDefaultTimeout);
        }
    };
    private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
        public void onStartTrackingTouch(SeekBar bar) {
            mDragging = true;
            show(3600000);
            mHandler.removeMessages(SHOW_PROGRESS);
            if (mInstantSeeking)
                mAM.setStreamMute(AudioManager.STREAM_MUSIC, true);
            if (mInfoView != null) {
                mInfoView.setText("");
                mInfoView.setVisibility(View.VISIBLE);
            }
        }

        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            if (!fromuser)
                return;

            long newposition = (mDuration * progress) / 1000;
            String time = StringUtils.generateTime(newposition);
            if (mInstantSeeking)
                mPlayer.seekTo(newposition);
            if (mInfoView != null)
                mInfoView.setText(time);
            if (mCurrentTime != null)
                mCurrentTime.setText(time);
        }

        public void onStopTrackingTouch(SeekBar bar) {
            if (!mInstantSeeking)
                mPlayer.seekTo((mDuration * bar.getProgress()) / 1000);
            if (mInfoView != null) {
                mInfoView.setText("");
                mInfoView.setVisibility(View.GONE);
            }
            show(sDefaultTimeout);
            mHandler.removeMessages(SHOW_PROGRESS);
            mAM.setStreamMute(AudioManager.STREAM_MUSIC, false);
            mDragging = false;
            mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000);
        }
    };

    public MediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRoot = this;
        mFromXml = true;
        initController(context);
    }

    public MediaController(Context context) {
        super(context);
        if (!mFromXml && initController(context))
            initFloatingWindow();
    }

    public MediaController(Context context, boolean fromXml, View container) {
        super(context);
        initController(context);
        mFromXml = fromXml;
        mRoot = makeControllerView();
        //这个地方的FrameLayout.LayoutpParams是因为布局文件中要把MediaController的视图作为childView加到一个FrameLayout中去
        FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        //想怎样布局MediaController就尽情的发挥这个LayoutParams吧
//    p.gravity = Gravity.BOTTOM;
        mRoot.setLayoutParams(p);
        ((FrameLayout) container).addView(mRoot);
        initControllerView(mRoot);
    }

    private boolean initController(Context context) {
        mContext = context;
        mAM = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        mLastVolumn = mAM.getStreamVolume(AudioManager.STREAM_MUSIC);
        return true;
    }

    @Override
    public void onFinishInflate() {
        if (mRoot != null)
            initControllerView(mRoot);
    }

    private void initFloatingWindow() {
        mWindow = new PopupWindow(mContext);
        mWindow.setFocusable(false);
        mWindow.setBackgroundDrawable(null);
        mWindow.setOutsideTouchable(true);
        mAnimStyle = android.R.style.Animation;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setWindowLayoutType() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            try {
                mAnchor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
                Method setWindowLayoutType = PopupWindow.class.getMethod("setWindowLayoutType", new Class[]{int.class});
                setWindowLayoutType.invoke(mWindow, WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG);
            } catch (Exception e) {
                Log.e("setWindowLayoutType", e);
            }
        }
    }

    /**
     * Set the view that acts as the anchor for the control view. This can for
     * example be a VideoView, or your Activity's main view.
     *
     * @param view The view to which to anchor the controller when it is visible.
     */
    public void setAnchorView(View view) {
        mAnchor = view;
        if (!mFromXml) {
            removeAllViews();
            mRoot = makeControllerView();
            mWindow.setContentView(mRoot);
            mWindow.setWidth(LayoutParams.MATCH_PARENT);
            mWindow.setHeight(LayoutParams.WRAP_CONTENT);
        }
        initControllerView(mRoot);
    }

    /**
     * Create the view that holds the widgets that control playback. Derived
     * classes can override this to create their own.
     *
     * @return The controller view.
     */
    protected View makeControllerView() {
        return ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(getResources().getIdentifier("mediacontroller", "layout", mContext.getPackageName()), this);
    }

    //设置播放名称
    public void setTitle(String title) {
        if (titleTextView != null)
            titleTextView.setText(title);
    }

    //是否隐藏掉时间控件
    public void setGone(boolean isGone) {
        mEndTime.setVisibility(isGone ? GONE : VISIBLE);
        mCurrentTime.setVisibility(isGone ? GONE : VISIBLE);
        mProgress.setVisibility(isGone ? GONE : VISIBLE);
    }

    //设置锁屏
    public void setLockVisible(boolean isLock) {
        isLocked = isLock;
        controlTitle.setVisibility(isLock ? View.VISIBLE : View.INVISIBLE);
        bottomLayout.setVisibility(isLock ? View.VISIBLE : View.INVISIBLE);
        controlBar.setVisibility(isLock ? View.VISIBLE : View.INVISIBLE);
        mPlayLockButton.setBackgroundResource(isLock ? R.drawable.ic_player_unlock_n : R.drawable.ic_player_lock_n);
    }

    //设置锁屏按钮与声音控制在全屏显示
    public void setLockShow(boolean isShow) {
        mPlayLockButton.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
        controlBar.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
    }

    private void initControllerView(View v) {
        mPauseButton = (ImageButton) v.findViewById(getResources().getIdentifier("mediacontroller_play_pause", "id", mContext.getPackageName()));
        if (mPauseButton != null) {
            mPauseButton.requestFocus();
            mPauseButton.setOnClickListener(mPauseListener);
        }

        mProgress = (SeekBar) v.findViewById(getResources().getIdentifier("mediacontroller_seekbar", "id", mContext.getPackageName()));
        if (mProgress != null) {
            if (mProgress instanceof SeekBar) {
                SeekBar seeker = (SeekBar) mProgress;
                seeker.setOnSeekBarChangeListener(mSeekListener);
            }
            mProgress.setMax(1000);
        }

        mEndTime = (TextView) v.findViewById(getResources().getIdentifier("mediacontroller_time_total", "id", mContext.getPackageName()));
        mCurrentTime = (TextView) v.findViewById(getResources().getIdentifier("mediacontroller_time_current", "id", mContext.getPackageName()));
        mFileName = (TextView) v.findViewById(getResources().getIdentifier("mediacontroller_file_name", "id", mContext.getPackageName()));
        if (mFileName != null)
            mFileName.setText(mTitle);

        controlTitle = v.findViewById(getResources().getIdentifier("layout_controller_title", "id", mContext.getPackageName()));
        bottomLayout = (RelativeLayout) v.findViewById(getResources().getIdentifier("controller_bottom", "id", mContext.getPackageName()));

        controlBar = v.findViewById(R.id.layout_controller_bar);

        mPlayLandscape = (ImageView) v.findViewById(getResources().getIdentifier("mediacontroller_play_landscape", "id", mContext.getPackageName()));

        mPlayLandscape.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mController.OnPortraitClick();
            }
        });

        titleTextView = (TextView) v.findViewById(getResources().getIdentifier("tv_controller_title", "id", mContext.getPackageName()));

        mPlayBackButton = (ImageView) v.findViewById(getResources().getIdentifier("tv_controller_back", "id", mContext.getPackageName()));

        mPlayBackButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mController.OnBackClick();
            }
        });

        mPlayLockButton = (TextView) v.findViewById(getResources().getIdentifier("layout_controller_lock", "id", mContext.getPackageName()));
        setLockShow(false);
        mPlayLockButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mController.OnLockClick();
            }
        });

        ivVolumn = (ImageView) controlBar.findViewById(R.id.iv_volume);
        seekVolumn = (VerticalSeekBar) controlBar.findViewById(R.id.seekbar_volume);
        seekVolumn.setOnSeekBarChangeListener(mVolumnSeekBarChangeListener);
        sound_text = (TextView) controlBar.findViewById(R.id.sound_text);
        soundlayout = (LinearLayout) controlBar.findViewById(R.id.sound_layout);
        //注册当音量发生变化时接收的广播
        myRegisterReceiver();
        //手势控制
        gesture = (MyScrollView2) controlBar.findViewById(R.id.mygesture);
        gesture.setOnGestureListener(new MyScrollView2.GestureListener() {
            @Override
            public void onConfirm() {
                if (isShowing()) {
                    hide();
                } else {
                    show();
                }
            }

            @Override
            public void onHorizontalScrolled(float x) {//进度条
                isHorizontalScroll=true;
                myprogress = mProgress.getProgress();
                if (!isLocked) {
                    if (!isShowing()) {
                        show();
                    }
                    long nowprogress = (long) (x + myprogress);
                    android.util.Log.i("yidong", "nowprogress" + nowprogress);
                    nowposition = (nowprogress * mDuration) / 1000;
                    String time = StringUtils.generateTime(nowposition);
                    if (mInstantSeeking)
                        mProgress.setProgress((int) nowprogress);
                    mPlayer.seekTo(nowposition);
                    if (mInfoView != null)
                        mInfoView.setText(time);
                    if (mCurrentTime != null)
                        mCurrentTime.setText(time);
                }
            }

            @Override
            public void onVerticalScrolled(float y) {//声音
                isHorizontalScroll=false;
                android.util.Log.i("sound", "y" + y);
                android.util.Log.i("sound", "bar" + seekVolumn.getWidth());
                int maxVolumn = mAM.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                if (y<=0){
                    mLastVolumn = (int) (mLastVolumn - (-y/10000* maxVolumn / seekVolumn.getWidth()));
                }else {
                    mLastVolumn = (int) (mLastVolumn + ( y      * maxVolumn / seekVolumn.getWidth()));
                }
                if (mLastVolumn >= maxVolumn) {
                    mLastVolumn = maxVolumn;
                }
                mAM.setStreamVolume(AudioManager.STREAM_MUSIC, mLastVolumn, 0);
                updateVolumnView(mLastVolumn);
            }

            @Override
            public void onUp() {
                if (isHorizontalScroll){
                    if (!mInstantSeeking)
                        mPlayer.seekTo((mDuration * mProgress.getProgress()) / 1000);
                    android.util.Log.i("yidong", "progress" + mProgress.getProgress());
                    if (mInfoView != null) {
                        mInfoView.setText("");
                        mInfoView.setVisibility(View.GONE);
                    }
                    show(sDefaultTimeout);
                    mHandler.removeMessages(SHOW_PROGRESS);
                    mAM.setStreamMute(AudioManager.STREAM_MUSIC, false);
                    mDragging = false;
                    mHandler.sendEmptyMessage(SHOW_PROGRESS);
                }
            }
        });


        ivVolumn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clickVolumn(v);
            }
        });
        updateVolumnView(-1);
    }

    private final OnSeekBarChangeListener mVolumnSeekBarChangeListener = new OnSeekBarChangeListener() {
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                mAM.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                updateVolumnView(progress);
            }
        }
    };

    private void clickVolumn(View v) {
        String tag = (String) v.getTag();
        int nextValue = 0;
        if (MUTE.equals(tag)) {
            ivVolumn.setImageResource(R.drawable.ic_volume);
            ivVolumn.setTag("VOLUME");
            nextValue = mLastVolumn;
        } else {
            mLastVolumn = mAM.getStreamVolume(AudioManager.STREAM_MUSIC);
            ivVolumn.setImageResource(R.drawable.ic_mute);
            ivVolumn.setTag("MUTE");
        }
        mAM.setStreamVolume(AudioManager.STREAM_MUSIC, nextValue, 0);
        updateVolumnView(nextValue);
    }

    /**
     * 更新声音进度条
     *
     * @param value 负数表示取当前系统值，否则更新到value值
     */
    public void updateVolumnView(int value) {
        int current = value >= 0 ? value : mAM.getStreamVolume(AudioManager.STREAM_MUSIC);
        seekVolumn.setMax(mAM.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        seekVolumn.setProgress(current);
        sound_text.setText("" + current * 100 / mAM.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        mLastVolumn = current;
        android.util.Log.i("sound", "current" + current);
        android.util.Log.i("sound", "mLastVolumn" + mLastVolumn);
        android.util.Log.i("sound", "Max" + mAM.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        if (current == 0) {
            ivVolumn.setImageResource(R.drawable.ic_mute);
            ivVolumn.setTag(MUTE);
        } else {
            ivVolumn.setImageResource(R.drawable.ic_volume);
            ivVolumn.setTag(VOLUME);
        }
    }

    /**
     * 注册当音量发生变化时接收的广播
     */
    private MyVolumeReceiver mVolumeReceiver;
    private void myRegisterReceiver(){
        mVolumeReceiver = new MyVolumeReceiver() ;
        IntentFilter filter = new IntentFilter() ;
        filter.addAction("android.media.VOLUME_CHANGED_ACTION") ;
        getContext().registerReceiver(mVolumeReceiver, filter) ;
    }


    public void setMediaPlayer(MediaPlayerControl player) {
        mPlayer = player;
        updatePausePlay();
    }

    /**
     * Control the action when the seekbar dragged by user
     *
     * @param seekWhenDragging True the media will seek periodically
     */
    public void setInstantSeeking(boolean seekWhenDragging) {
        mInstantSeeking = seekWhenDragging;
    }

    public void show() {
        show(sDefaultTimeout);
    }

    /**
     * Set the content of the file_name TextView
     *
     * @param name
     */
    public void setFileName(String name) {
        mTitle = name;
        if (mFileName != null)
            mFileName.setText(mTitle);
    }

    /**
     * Set the View to hold some information when interact with the
     * MediaController
     *
     * @param v
     */
    public void setInfoView(OutlineTextView v) {
        mInfoView = v;
    }

    /**
     * <p>
     * Change the animation style resource for this controller.
     * </p>
     * <p/>
     * <p>
     * If the controller is showing, calling this method will take effect only the
     * next time the controller is shown.
     * </p>
     *
     * @param animationStyle animation style to use when the controller appears
     *                       and disappears. Set to -1 for the default animation, 0 for no animation, or
     *                       a resource identifier for an explicit animation.
     */
    public void setAnimationStyle(int animationStyle) {
        mAnimStyle = animationStyle;
    }

    /**
     * Show the controller on screen. It will go away automatically after
     * 'timeout' milliseconds of inactivity.
     *
     * @param timeout The timeout in milliseconds. Use 0 to show the controller
     *                until hide() is called.
     */
    public void show(int timeout) {
        if (!mShowing && mAnchor != null && mAnchor.getWindowToken() != null) {
            if (mPauseButton != null)
                mPauseButton.requestFocus();

            if (mFromXml) {
                setVisibility(View.VISIBLE);
            } else {
                int[] location = new int[2];

                mAnchor.getLocationOnScreen(location);
                Rect anchorRect = new Rect(location[0], location[1], location[0] + mAnchor.getWidth(), location[1] + mAnchor.getHeight());

                mWindow.setAnimationStyle(mAnimStyle);
                setWindowLayoutType();
                mWindow.showAtLocation(mAnchor, Gravity.NO_GRAVITY, anchorRect.left, anchorRect.bottom);
            }
            mShowing = true;
            if (mShownListener != null)
                mShownListener.onShown();
        }
        updatePausePlay();
        mHandler.sendEmptyMessage(SHOW_PROGRESS);

        if (timeout != 0) {
            mHandler.removeMessages(FADE_OUT);
            mHandler.sendMessageDelayed(mHandler.obtainMessage(FADE_OUT), timeout);
        }
    }

    public boolean isShowing() {
        return mShowing;
    }

    public void hide() {
        if (mAnchor == null)
            return;

        if (mShowing) {
            try {
                mHandler.removeMessages(SHOW_PROGRESS);
                if (mFromXml)
                    setVisibility(View.GONE);
                else
                    mWindow.dismiss();
            } catch (IllegalArgumentException ex) {
                Log.d("MediaController already removed");
            }
            mShowing = false;
            if (mHiddenListener != null)
                mHiddenListener.onHidden();
        }
    }

    public void setOnShownListener(OnShownListener l) {
        mShownListener = l;
    }

    public void setOnHiddenListener(OnHiddenListener l) {
        mHiddenListener = l;
    }

    private long setProgress() {
        if (mPlayer == null || mDragging)
            return 0;

        long position = mPlayer.getCurrentPosition();
        long duration = mPlayer.getDuration();
        if (mProgress != null) {
            if (duration > 0) {
                long pos = 1000L * position / duration;
                mProgress.setProgress((int) pos);
            }
            int percent = mPlayer.getBufferPercentage();
            mProgress.setSecondaryProgress(percent * 10);
        }

        mDuration = duration;

        if (mEndTime != null)
            mEndTime.setText(StringUtils.generateTime(mDuration));
        if (mCurrentTime != null)
            mCurrentTime.setText(StringUtils.generateTime(position));

        return position;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        show(sDefaultTimeout);
        return true;
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        show(sDefaultTimeout);
        return false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (event.getRepeatCount() == 0 && (keyCode == KeyEvent.KEYCODE_HEADSETHOOK || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE || keyCode == KeyEvent.KEYCODE_SPACE)) {
            doPauseResume();
            show(sDefaultTimeout);
            if (mPauseButton != null)
                mPauseButton.requestFocus();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP) {
            if (mPlayer.isPlaying()) {
                mPlayer.pause();
                updatePausePlay();
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU) {
            hide();
            return true;
        } else {
            show(sDefaultTimeout);
        }
        return super.dispatchKeyEvent(event);
    }

    private void updatePausePlay() {
        if (mRoot == null || mPauseButton == null)
            return;

        if (mPlayer.isPlaying())
            mPauseButton.setImageResource(getResources().getIdentifier("mediacontroller_pause", "drawable", mContext.getPackageName()));
        else
            mPauseButton.setImageResource(getResources().getIdentifier("mediacontroller_play", "drawable", mContext.getPackageName()));
    }

    private void doPauseResume() {
        if (mPlayer.isPlaying())
            mPlayer.pause();
        else
            mPlayer.start();
        updatePausePlay();
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (mPauseButton != null)
            mPauseButton.setEnabled(enabled);
        if (mProgress != null)
            mProgress.setEnabled(enabled);
        super.setEnabled(enabled);
    }

    public interface OnShownListener {
        public void onShown();
    }

    public interface OnHiddenListener {
        public void onHidden();
    }

    public interface MediaPlayerControl {
        void start();

        void pause();

        long getDuration();

        long getCurrentPosition();

        void seekTo(long pos);

        boolean isPlaying();

        int getBufferPercentage();
    }

    public void setOnControllerListener(OnControllerListener listener) {
        mController = listener;
    }

    public interface OnControllerListener {
        void OnPortraitClick();//全屏控制

        void OnBackClick();//返回键

        void OnLockClick();//锁屏键
    }


    /**
     * 处理音量变化时的界面显示
     * @author long
     */
    private class MyVolumeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //如果音量发生变化则更改seekbar的位置
            if(intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")){
                int currVolume = mAM.getStreamVolume(AudioManager.STREAM_MUSIC) ;// 当前的媒体音量
                updateVolumnView(currVolume); ;
            }
        }
    }


}
