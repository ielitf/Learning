package io.vov.vitamio.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * 触摸板
 */

public class MyScrollView2 extends ImageView implements View.OnTouchListener {

    private Context mContext;
    private GestureDetector mGestureDetector;
    private static final String TAG = "ScrollView";
    private GestureListener mGestureListener;

    public MyScrollView2(Context context) {
        super(context);
        initData(context);
    }

    public MyScrollView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData(context);
    }

    public MyScrollView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData(context);
    }

    public interface GestureListener {
        void onConfirm();

        void onHorizontalScrolled(float x);

        void onVerticalScrolled(float y);

        void onUp();
    }

    public void setOnGestureListener(GestureListener gestureListener) {
        this.mGestureListener = gestureListener;
    }

    private void initData(Context context) {
        this.mContext = context;
        super.setOnTouchListener(this);
        super.setClickable(true);
        super.setLongClickable(true);
        super.setFocusable(true);
        mGestureDetector = new GestureDetector(mContext, new MyGestureListener());
    }


    /*
   * 当该view上的事件被分发到view上时触发该方法的回调
   * 如果这个方法返回false时,该事件就会被传递给Activity中的onTouchEvent方法来处理
   * 如果该方法返回true时，表示该事件已经被onTouch函数处理玩，不会上传到activity中处理
   * 该方法属于View.OnTouchListening接口
   * */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        private float downX;
        private float downY;
        private float myDistanceX;
        private float myDistanceY;

        private MyGestureListener() {
            this.myDistanceX = 0.0F;
            this.myDistanceY = 0.0F;
            this.downX = 0.0F;
            this.downY = 0.0F;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.e(TAG, "onDoubleTap");
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            Log.e(TAG, "onDoubleTapEvent");
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.e(TAG, "onSingleTapConfirmed");
            if (mGestureListener != null) {
                mGestureListener.onConfirm();
            }
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            Log.e(TAG, "onDown");
            this.myDistanceX = 0.0F;
            this.myDistanceY = 0.0F;
            this.downX = e.getX();
            this.downY = e.getY();
            System.out.println("downx = " + this.downX + " downy = " + this.downY);
            return true;
        }

        /*
        *用户按下屏幕并且没有移动或松开。主要是提供给用户一个可视化的反馈，告诉用户他们的按下操作已经
        * 被捕捉到了。如果按下的速度很快只会调用onDown(),按下的速度稍慢一点会先调用onDown()再调用onShowPress().
        * */
        @Override
        public void onShowPress(MotionEvent e) {
            Log.e(TAG, "onShowPress");
        }

        /*
       *一次单纯的轻击抬手动作时触发
       * */
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.e(TAG, "onSingleTapUp");
            return true;
        }

        /*
        *屏幕拖动事件，如果按下的时间过长，调用了onLongPress，再拖动屏幕不会触发onScroll。拖动屏幕会多次触发
        * @param e1 开始拖动的第一次按下down操作,也就是第一个ACTION_DOWN
        * @parem e2 触发当前onScroll方法的ACTION_MOVE
        * @param distanceX 当前的x坐标与最后一次触发scroll方法的x坐标的差值。
        * @param diastancY 当前的y坐标与最后一次触发scroll方法的y坐标的差值。
        * */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.e(TAG, "onScroll");
            float x2 = e2.getX();
            float y2 = e2.getY();
            this.myDistanceX = x2 - this.downX;
            this.myDistanceY = y2 - this.downY;
            System.out.println("x2 = " + x2 + " y2 = " + y2);
            System.out.println("myDistanceX = " + this.myDistanceX + " myDistanceY = " + this.myDistanceY);
            if (mGestureListener != null) {
                if (this.myDistanceX > 10.0F && (Math.abs(this.myDistanceY) < Math.abs(this.myDistanceX))) {
                    mGestureListener.onHorizontalScrolled(-distanceX);
                }
                if (this.myDistanceX < 0 && (Math.abs(this.myDistanceX) > 10.0F) && (Math.abs(this.myDistanceY) < Math.abs(this.myDistanceX))) {
                    mGestureListener.onHorizontalScrolled(-distanceX);
                }
                if (this.myDistanceY > 10.0F && (Math.abs(this.myDistanceY) > Math.abs(this.myDistanceX))) {
                    mGestureListener.onVerticalScrolled(distanceY);
                }
                if (this.myDistanceY < 0 && (Math.abs(this.myDistanceY) > 10.0F) && (Math.abs(this.myDistanceY) > Math.abs(this.myDistanceX))) {
                    mGestureListener.onVerticalScrolled(distanceY);
                }
            }
            return true;
        }

        /*
        * 长按。在down操作之后，过一个特定的时间触发
        * */
        @Override
        public void onLongPress(MotionEvent e) {
            Log.e(TAG, "onLongPress");
        }

        /*
        * 按下屏幕，在屏幕上快速滑动后松开，由一个down,多个move,一个up触发
        * @param e1 开始快速滑动的第一次按下down操作,也就是第一个ACTION_DOWN
        * @parem e2 触发当前onFling方法的move操作,也就是最后一个ACTION_MOVE
        * @param velocityX：X轴上的移动速度，像素/秒
        * @parram velocityY：Y轴上的移动速度，像素/秒
        * */
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.e(TAG, "onFling");
            if (mGestureListener != null) {
                mGestureListener.onUp();
            }
            return true;
        }
    }
}
