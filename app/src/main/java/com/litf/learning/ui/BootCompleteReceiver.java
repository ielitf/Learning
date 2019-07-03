package com.litf.learning.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootCompleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("oye", "收到开机广播");
		// 开启程序的主页面
		Intent intent2 = new Intent(context, MainActivity.class);
		// 在Receiver中开启Activity，必须要执行以下代码
		// 如果程序中已经有任务栈就直接使用，如果没有的话，就创建一个
		intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent2);
	}
	
}
