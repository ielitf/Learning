package com.litf.learning.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class IPAddressUtils {
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
    /**
     * 获取Android设备IP
     */
    public static String getAndroidIp(Context context) {
        int type;
        //以下方法是为了获取联网的状态
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);//获取系统的连接服务
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();//获取网络的连接情况
        type = activeNetInfo.getType();
        System.out.println("type=======" + type);

        String IP = null;
        if (type == ConnectivityManager.TYPE_WIFI) {
            try {
                WifiManager wifiManager = (WifiManager) context
                        .getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int i = wifiInfo.getIpAddress();
                System.out.println("dddd=======" + i);
                IP = int2ip(i);
                return IP;
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
            return IP;
        }
        if (type == ConnectivityManager.TYPE_ETHERNET) {//以太网
            try {
                Enumeration nis = NetworkInterface.getNetworkInterfaces();
                InetAddress ia = null;
                while (nis.hasMoreElements()) {
                    NetworkInterface ni = (NetworkInterface) nis.nextElement();
                    Enumeration<InetAddress> ias = ni.getInetAddresses();
                    while (ias.hasMoreElements()) {
                        ia = ias.nextElement();
                        if (ia instanceof Inet6Address) {
                            continue;// skip ipv6
                        }
                        String ip = ia.getHostAddress();
                        if (!"127.0.0.1".equals(ip)) {
                            IP = ia.getHostAddress();
                            break;
                        }
                    }
                }
            } catch (SocketException e) {
                e.printStackTrace();
            }
            return IP;
        }
        return "";
    }

    /**
     * 将ip的整数形式转换成ip形式
     */
    private static String int2ip(int ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }

}
