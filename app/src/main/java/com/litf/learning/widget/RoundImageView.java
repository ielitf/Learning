package com.litf.learning.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

public class RoundImageView extends AppCompatImageView {
	public Paint inPaint;
	public Paint outPaint;
	private Paint paint;
	private Paint paint2;

	public RoundImageView(Context ctx) {
		super(ctx);
		init(ctx, null);
	}

	public RoundImageView(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);
		init(ctx, attrs);
	}

	public RoundImageView(Context ctx, AttributeSet attrs, int defStyle) {
		super(ctx, attrs, defStyle);
		init(ctx, attrs);
	}

	private void drawLiftDown(Canvas canvas) {
		Path path = new Path();
		path.moveTo(0.0F, getHeight() / 2);
		path.lineTo(0.0F, getHeight());
		path.lineTo(getWidth() / 2, getHeight());
		path.arcTo(new RectF(0, 0, getWidth(), getHeight()),
				90.0F, 90.0F);
		path.close();
		canvas.drawPath(path, paint);
	}

	private void drawLiftUp(Canvas canvas) {
		Path path = new Path();
		path.moveTo(0.0F, getHeight() / 2);
		path.lineTo(0.0F, 0.0F);
		path.lineTo(getWidth() / 2, 0.0F);
		path.arcTo(new RectF(0, 0, getWidth(), getHeight()),
				-90.0F, -90.0F);
		path.close();
		canvas.drawPath(path, paint);
	}

	private void drawRightDown(Canvas canvas) {
		Path path = new Path();
		path.moveTo(getWidth(), getHeight() / 2);
		path.lineTo(getWidth(), getHeight());
		path.lineTo(getWidth() / 2, getHeight());
		path.arcTo(new RectF(0, 0, getWidth(), getHeight()),
				90.0F, -90.0F);
		path.close();
		canvas.drawPath(path, paint);
	}

	private void drawRightUp(Canvas canvas) {
		Path path = new Path();
		path.moveTo(getWidth(), getHeight() / 2);
		path.lineTo(getWidth(), 0.0F);
		path.lineTo(getWidth() / 2, 0.0F);
		path.arcTo(new RectF(0, 0, getWidth(), getHeight()),
				-90.0F, 90.0F);
		path.close();
		canvas.drawPath(path, paint);
	}

	private void init(Context ctx, AttributeSet paramAttributeSet) {
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
		paint2 = new Paint();
		paint2.setXfermode(null);
	}

	@Override
	public void draw(Canvas canvas) {
		Bitmap img = Bitmap.createBitmap(getWidth(), getHeight(),
				Bitmap.Config.ARGB_8888);
		Canvas localCanvas = new Canvas(img);
		super.draw(localCanvas);
		drawLiftUp(localCanvas);
		drawLiftDown(localCanvas);
		drawRightUp(localCanvas);
		drawRightDown(localCanvas);
		canvas.drawBitmap(img, 0.0F, 0.0F, paint2);
	}
}