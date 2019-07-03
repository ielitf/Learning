package com.litf.learning.utils;

import android.content.Context;
import android.util.Log;

import com.litf.learning.listener.IGetMessageCallBack;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttManager {
    public static final String TAG = MqttManager.class.getSimpleName()+"=================";
//    private String host = "tcp://127.0.0.1:1883";
    private String host = "tcp://172.16.30.226:1883";
    private String userName = "easton";
    private String passWord = "easton";
    private String clientId = "zhanghao66666666";

    private static MqttManager mqttManager = null;
    private MqttClient client;
    private MqttConnectOptions connectOptions;

    private IGetMessageCallBack messageCallBack;

    public MqttManager(Context context){
        clientId = clientId + MqttClient.generateClientId();
    }

    public MqttManager getInstance(Context context){
        if(mqttManager == null){
            mqttManager = new MqttManager(context);
        }else{
            return mqttManager;
        }
        return null;
    }

    public void connect(){
        try{
            client = new MqttClient(host,clientId,new MemoryPersistence());
            connectOptions = new MqttConnectOptions();
            // 清除缓存
            connectOptions.setCleanSession(true);
            // 设置超时时间，单位：秒
            connectOptions.setConnectionTimeout(10);
            // 心跳包发送间隔，单位：秒
            connectOptions.setKeepAliveInterval(20);

            connectOptions.setUserName(userName);
            connectOptions.setPassword(passWord.toCharArray());
            client.setCallback(mqttCallback);
            client.connect(connectOptions);
        }catch (MqttException e){
            e.printStackTrace();
        }
    }

    public void subscribe(String topic, int qos){
        if(client != null){
            int[] Qos = {qos};
            String[] topic1 = {topic};
            try {
                client.subscribe(topic1, Qos);
                Log.d(TAG,"订阅topic : "+topic);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    public void publish(String topic, String msg, boolean isRetained, int qos) {
        try {
            if (client!=null) {
                MqttMessage message = new MqttMessage();
                message.setQos(qos);
                message.setRetained(isRetained);
                message.setPayload(msg.getBytes());
                client.publish(topic, message);
                Log.d(TAG,"发送topic : "+topic+"；发送的消息msg："+msg);
            }
        } catch (MqttPersistenceException e) {
            e.printStackTrace();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private MqttCallback mqttCallback = new MqttCallback() {
        @Override
        public void connectionLost(Throwable cause) {
            Log.i(TAG,"connection lost");
        }

        @Override
        public void messageArrived(String topic, MqttMessage message){
            Log.i(TAG,"received topic : " + topic);
            String payload = new String(message.getPayload());
            Log.i(TAG,"received msg : " + payload);
            if (messageCallBack != null){
                messageCallBack.setMessage(topic,payload);
            }
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            Log.i(TAG,"deliveryComplete");
        }
    };
    public void setIGetMessageCallBack(IGetMessageCallBack messageCallBack){
        this.messageCallBack = messageCallBack;
    }


}
