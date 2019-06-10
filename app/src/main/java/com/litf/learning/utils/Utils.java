package com.litf.learning.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: wyf
 * @类描述:工具类
 * @version 1.0
 */
public class Utils {
	public static int getHeightPixel(Activity activity) {
		DisplayMetrics localDisplayMetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
		return localDisplayMetrics.heightPixels;
	}

	public static int getWidthPixel(Activity activity) {
		DisplayMetrics localDisplayMetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
		return localDisplayMetrics.widthPixels;
	}

	public static int getStatusBarHeight(Activity activity) {
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);

		int statusBarHeight = frame.top;
		return statusBarHeight;
	}

	/**
	 * 获取文件夹大小
	 * 
	 * @param file
	 *            File实例
	 * @return long
	 */
	public static long getFolderSize(java.io.File file) {

		long size = 0;
		try {
			java.io.File[] fileList = file.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				if (fileList[i].isDirectory()) {
					size = size + getFolderSize(fileList[i]);

				} else {
					size = size + fileList[i].length();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// return size/1048576;
		return size;
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)。
	 * 
	 * @param dpValue
	 * @return
	 */
	public static int dp2px(float dpValue, Context context) {
		final float density = context.getResources().getDisplayMetrics().density;
		return Math.round(dpValue * density);

	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp。
	 * 
	 * @param pxValue
	 * @return
	 */
	public static int px2dp(float pxValue, Context context) {
		final float density = context.getResources().getDisplayMetrics().density;
		return Math.round(pxValue / density);
	}

	/**
	 * 
	 * @Title: isFirstRun
	 * @类描述:判断程序是否第一次运行
	 * @参数: @return
	 * @return boolean 返回类型
	 * @throws
	 */
	public static boolean isFirstRun(Context context) {
		boolean isFirstRun = false;
		SharedPreferences sp = context.getSharedPreferences("isFirst",
				Context.MODE_PRIVATE);
		int version = sp.getInt("version", 0);
		int appVersion = getVersionCode(context);
		if (version != appVersion) {
			sp.edit().putInt("version", appVersion).commit();
			isFirstRun = true;
		}
		return isFirstRun;
	}

	/** 获取版本号(内部识别号) */
	public static int getVersionCode(Context context) {
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return pi.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return 0;
		}
	}

	/** 获取版本名（Android#系统版本号#应用版本号：Android#4.1.1#1.0） */
	public static String getAppVersionName(Context context) {
		String versionName = "";
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionName = pi.versionName;
			if (versionName == null || versionName.length() <= 0) {
				return "";
			}
		} catch (Exception e) {
		}
		// return "Android#" + android.os.Build.VERSION.RELEASE + "#"
		// + versionName;
		return versionName;
	}

	/** 获取Android设备IP */
	public static String getAndroidIp(Context context, int type) {
		String IP = null;
		if (type == ConnectivityManager.TYPE_WIFI) {
			try {
				WifiManager wifiManager = (WifiManager) context
						.getSystemService(Context.WIFI_SERVICE);
				WifiInfo wifiInfo = wifiManager.getConnectionInfo();
				int i = wifiInfo.getIpAddress();
				System.out.println("dddd=======" + i);
				IP = int2ip(i);
			} catch (Exception ex) {

			}
		}
		if (type == ConnectivityManager.TYPE_MOBILE) {
			StringBuilder IPStringBuilder = new StringBuilder();
			try {
				Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface
						.getNetworkInterfaces();
				while (networkInterfaceEnumeration.hasMoreElements()) {
					NetworkInterface networkInterface = networkInterfaceEnumeration
							.nextElement();
					Enumeration<InetAddress> inetAddressEnumeration = networkInterface
							.getInetAddresses();
					while (inetAddressEnumeration.hasMoreElements()) {
						InetAddress inetAddress = inetAddressEnumeration
								.nextElement();
						if (!inetAddress.isLoopbackAddress()
								&& !inetAddress.isLinkLocalAddress()
								&& inetAddress.isSiteLocalAddress()) {
							IPStringBuilder.append(inetAddress.getHostAddress()
									.toString() + "\n");
						}
					}
				}
			} catch (SocketException ex) {

			}
			IP = IPStringBuilder.toString();
		}
		return IP;
	}

	/** 将ip的整数形式转换成ip形式 */
	private static String int2ip(int ipInt) {
		StringBuilder sb = new StringBuilder();
		sb.append(ipInt & 0xFF).append(".");
		sb.append((ipInt >> 8) & 0xFF).append(".");
		sb.append((ipInt >> 16) & 0xFF).append(".");
		sb.append((ipInt >> 24) & 0xFF);
		return sb.toString();
	}

	/** 进行MD5加密 */
	public static String md5(String string) {
		byte[] hash;
		try {
			hash = MessageDigest.getInstance("MD5").digest(
					string.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Huh, MD5 should be supported?", e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Huh, UTF-8 should be supported?", e);
		}

		StringBuilder hex = new StringBuilder(hash.length * 2);
		for (byte b : hash) {
			if ((b & 0xFF) < 0x10)
				hex.append("0");
			hex.append(Integer.toHexString(b & 0xFF));
		}
		return hex.toString();
	}

	/** 对电话号码进行正则匹配 */
	public static boolean isMobileNO(String mobiles) {// 177
		Pattern p = Pattern.compile("[1][3578]\\d{9}");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	/** 判断email格式是否正确 */
	public static boolean isEmail(String email) {
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);

		return m.matches();
	}

	/** dp与px转化的方法 */
	public static int dp2px(int dp, Context context) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				context.getResources().getDisplayMetrics());
	}

	/**
	 * 图片质量压缩
	 * 
	 * @param image
	 *            传入Bitmap对象
	 * @return
	 */
	public static Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			options -= 10;// 每次都减少10
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中

		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}
	
	/**
	 * 制作圆角图片/或者任意度数的倒角
	 * @author litf
	 * @param bit
	 * @return
	 */
	public static Bitmap getRoundBitmap(Bitmap bit) {
		Bitmap bitmap = Bitmap.createBitmap(bit.getWidth(), bit.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.YELLOW);
		// 绘制一个作为基准的圆角矩形
		RectF rect = new RectF(0, 0, bit.getWidth(), bit.getHeight());
		//改变一下第二个参数和第三个参数的值，可以任意改变倒角度数
		canvas.drawRoundRect(rect, 180, 180, paint);
		// 设置相交后的保留原则
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bit, 0, 0, paint);

		return bitmap;
	}

	/**
	 * 转换图片变成圆形
	 * 
	 * @param bitmap
	 *            传入Bitmap对象
	 * @return
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			float clip = (height - width) / 2;
			left = 0;
			top = clip;
			right = width;
			bottom = height - clip;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}

		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right,
				(int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top,
				(int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);

		paint.setAntiAlias(true);// 设置画笔无锯齿

		canvas.drawARGB(0, 0, 0, 0); // 填充整个Canvas
		paint.setColor(color);

		// 以下有两种方法画圆,drawRounRect和drawCircle
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);// 画圆角矩形，第一个参数为图形显示区域，第二个参数和第三个参数分别是水平圆角半径和垂直圆角半径。
		// canvas.drawCircle(roundPx, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));// 设置两张图片相交时的模式,参考http://trylovecatch.iteye.com/blog/1189452
		canvas.drawBitmap(bitmap, src, dst, paint); // 以Mode.SRC_IN模式合并bitmap和已经draw了的Circle

		return output;
	}

	/**
	 * 设置图片，不压缩
	 * 
	 * @param realPath
	 *            图片地址
	 * @param imag
	 *            控件
	 * @param context
	 */
	public static void setImagePhoto2(String realPath, ImageView imag,
                                      Context context) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		Bitmap b1 = BitmapFactory.decodeFile(realPath, options);
		imag.setImageBitmap(Utils.getRoundBitmap(b1));
	}
	/**
	 * 设置图片
	 * 
	 * @param realPath
	 *            图片地址
	 * @param imag
	 *            控件
	 * @param context
	 */
	public static void setImagePhoto(String realPath, ImageView imag,
                                     Context context) {
		
		BitmapFactory.Options options = new BitmapFactory.Options();

		options.inJustDecodeBounds = true;

		BitmapFactory.decodeFile(realPath, options);
		int w = options.outWidth;
		int h = options.outHeight;
		// 现在主流手机比较多是40dp*40dp分辨率，所以高和宽我们设置为
		float hh = Utils.dp2px(40, context);// 这里设置高度
		float ww = Utils.dp2px(40, context);// 这里设置宽度
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (options.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (options.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		options.inSampleSize = be;// 设置缩放比例
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Config.RGB_565;// 降低图片从ARGB888到RGB565
		Bitmap b1 = BitmapFactory.decodeFile(realPath, options);
		// 进行质量压缩
		Bitmap b = Utils.compressImage(b1);
		Bitmap resizedBitmap = null;

		int degree = readPictureDegree(realPath);

		if (degree <= 0) {
			imag.setImageBitmap(Utils.toRoundBitmap(b));
		} else {
			// 创建操作图片是用的matrix对象
			Matrix matrix = new Matrix();
			// 旋转图片动作
			matrix.postRotate(degree);
			// 创建新图片
			resizedBitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
					b.getHeight(), matrix, true);
			imag.setImageBitmap(Utils.toRoundBitmap(resizedBitmap));
		}
	}

	/**
	 * 获取图片的旋转角度
	 * 
	 * @param path
	 *            图片地址
	 * @return
	 */
	private static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	/**
	 * 网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager mgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] info = mgr.getAllNetworkInfo();
		if (info != null) {
			for (int i = 0; i < info.length; i++) {
				if (info[i].getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}
			}
		}
		return false;
	}

	public static int parseStringToInt(String value) {
		if (TextUtils.isEmpty(value)) {
			return 0;
		} else if (value.equals("null")) {
			return 0;
		} else {
			return Integer.parseInt(value);
		}

	}
	
	/**
     * 将文件转化为流的形式
     * @param path  文件路径
     * @return
     */
    public static String fileTobuffer(String path) {
        String uploadBuffer = null;
        try {
            FileInputStream fis = new FileInputStream(path);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int count = 0;
            while ((count = fis.read(buffer)) >= 0) {
                baos.write(buffer, 0, count);
            }
            BASE64Encoder encoder = new BASE64Encoder();
            uploadBuffer = new String(encoder.encode(baos.toByteArray()));
            fis.close();
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uploadBuffer;
    }


	public static boolean hasSdcard() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}


//	public boolean isExternalStorageWritable() {
//		String state = Environment.getExternalStorageState();
//		if (Environment.MEDIA_MOUNTED.equals(state)) {
//			return true;
//		}
//		return false;
//	}

	/* Checks if external storage is available to at least read */
	public boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state) ||
				Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}
}
