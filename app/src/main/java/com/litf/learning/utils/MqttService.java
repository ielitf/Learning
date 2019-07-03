package com.litf.learning.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.litf.learning.R;
import com.litf.learning.control.CodeConstants;
import com.litf.learning.listener.CallBack;
import com.litf.learning.ui.MainActivity;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;

public class MqttService extends Service {
    public static String clientId = "";//具体见初始化方法 init()
    public static String BROKER_URL = "tcp://192.168.10.2:1883";
    public static String TOPIC_NAME = "";//具体见初始化方法 init()
    public static String TOPIC_SIGN = "";//具体见初始化方法 init()
    private static String userName = "atv";
    private static String passWord = "atv";
    private static String roomNum, tableNum;
    public MqttClient mqttClient;
    public MqttConnectOptions options;
    private ScheduledExecutorService scheduler;
    private ConnectivityManager mConnectivityManager; //网络状态监测
    private static String[] topicFilters;
    private static int[] qos;
    private static CallBack mCallBack;

    public MqttService() {
    }
    public static void setCallBack(CallBack callBack) {
        mCallBack = callBack;
    }
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification.Builder builder = new Notification.Builder(this.getApplicationContext()); //获取一个Notification构造器
        Intent nfIntent = new Intent(this, MainActivity.class);
        builder.setContentIntent(PendingIntent.
                getActivity(this, 0, nfIntent, 0)) // 设置PendingIntent
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                        R.mipmap.ic_launcher))
                .setContentTitle("com.danggui")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText("namePlate正在运行")
                .setWhen(System.currentTimeMillis());
        Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_SOUND;
        startForeground(110, notification);      // 开始前台服务
        init();
        connect();
        return super.onStartCommand(intent, flags, startId);
    }


    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 初始化相关数据
     */
    public void init() {
        roomNum = SharedPreferencesManager.getRoomNum();//从缓存中读会议室编号。看需求选择读取方式
        tableNum = SharedPreferencesManager.getTableNum();//从缓存中读取桌牌编号。看需求选择读取方式
        clientId = roomNum + CodeConstants.CLIENT_ID + tableNum;
        TOPIC_NAME = roomNum + CodeConstants.ZP_NAME + tableNum;
        TOPIC_SIGN = roomNum + CodeConstants.ZP_SIGN + tableNum;
        topicFilters = new String[]{TOPIC_NAME, TOPIC_SIGN};
        qos = new int[]{1, 1};
        LogUtil.i("===MQTT参数", "clientId:" + clientId + "；TOPIC_NAME:" + TOPIC_NAME + "；TOPIC_SIGN:" + TOPIC_SIGN);

        // todo 设置主题
        try {
            //以下判断目的：当从新设置桌号会议室号后，重新startService,断开之前的连接。并重新设置参数，重新连接
            if(mqttClient!= null){
                if (mqttClient.isConnected()) {
                    mqttClient.disconnect(0);
                    mqttClient = null;
                }
            }
            mqttClient = new MqttClient(BROKER_URL, clientId, new MemoryPersistence());
            //MQTT的连接设置
            options = new MqttConnectOptions();
            //设置是否清空session
            options.setCleanSession(false);
            //设置连接的用户名
            options.setUserName(userName);
            //设置连接的密码
            options.setPassword(passWord.toCharArray());
            // 设置超时时间 单位为秒
            options.setConnectionTimeout(30);
            options.setKeepAliveInterval(30);
            options.setAutomaticReconnect(true);//设置自动重连
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    //连接丢失，进行重新连接
                    if (isNetworkAvailable()) {
                        reconnectIfNecessary();
                    }
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    String str = new String(message.getPayload());
                    LogUtil.i("===MqttService", "topic=" + topic + "；Qos=" + message.getQos() + "；----message:" + str);
                    mCallBack.setData(topic,str);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    LogUtil.e(TAG, "messageId=:" + token.getMessageId());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        mConnectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.registerReceiver(mConnectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }


    /**
     * 如果网络状态正常则返回true反之flase
     */
    private boolean isNetworkAvailable() {
        NetworkInfo info = mConnectivityManager.getActiveNetworkInfo();
        return (info == null) ? false : info.isConnected();
    }

    /**
     * 进行重新连接前判断client状态
     */
    public synchronized void reconnectIfNecessary() {
        if (!mqttClient.isConnected()) {
            LogUtil.e(TAG, "断开连接，开始重连");
            connect();
        }
    }

    /*连接服务器，并订阅消息主题*/
    private void connect() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
                try {
                    mqttClient.connect(options);
                    mqttClient.subscribe(topicFilters);
                } catch (Exception e) {
                    e.printStackTrace();
                }
//            }
//        }).start();
    }

    /**
     * 调用init() 方法之后，调用此方法。
     */
    public void startReconnect() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (!mqttClient.isConnected() && isNetworkAvailable()) {
                    connect();
                }
            }
        }, 0, 10 * 1000, TimeUnit.MILLISECONDS);
    }

    /**
     * 网络状态发生变化接收器
     */
    private final BroadcastReceiver mConnectivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("BroadcastReceiver", "Connectivity Changed...");
            if (!isNetworkAvailable()) {
                Toast.makeText(context, "网络错误", Toast.LENGTH_SHORT).show();
                scheduler.shutdownNow();
            } else {
                startReconnect();
            }
        }
    };

    @Override
    public void onDestroy() {
        stopForeground(true);// 停止前台服务--参数：表示是否移除之前的通知
        super.onDestroy();
        try {
            unregisterReceiver(mConnectivityReceiver);
            mqttClient.disconnect(0);
            mqttClient = null;
        } catch (MqttException e) {
            Toast.makeText(getApplicationContext(), "Something went wrong!" + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
