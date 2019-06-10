package com.litf.learning.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceManager {
	    static SharedPreferences sp;

	    public static void init(Context context, String name) {
	        sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
	    }

	/**
	 * 缓存首页数据
	 */
	private static final String CACHED_HOMEPAGE_DATA="cached_homepage_data";

        public static void setCachedHomepageData(String jsonStr){
        	if (null != sp) {
	            sp.edit().putString(CACHED_HOMEPAGE_DATA, jsonStr).commit();
	        }
        }
        public static String getCachedHomepageData(){
        	 if (null != sp) {
 	            return sp.getString(CACHED_HOMEPAGE_DATA, null);
 	        }
 	        return null;
        }

	/**
	 * 是否是第一次使用
	 */
	    private static final String IS_FIRST_USE="Is_app_first_use";

	    public static void setIsFirstUse(boolean value){
	    	 if (null != sp) {
		            sp.edit().putBoolean(IS_FIRST_USE, value).commit();
		        }
	    }
	    public static boolean getIsFirstUse(){
	    	if (null != sp) {
	            return sp.getBoolean(IS_FIRST_USE, true);
	        }
	        return true;
	    }
	//	    public static boolean isLogin(){
//	    	String custName=getCachedUsername();
//	    	String custId=getCachedCustId();
//	    	if (TextUtils.isEmpty(custId)||custId.equals("null")) {
//				return false;
//			}
//	    	/*if (TextUtils.isEmpty(custName)||custName.equals("null")) {
//				return false;
//			}*/
//
//	    	return true;
//	    }
//	private static final String KEY_CACHED_USERNAME = "username";
//
//	    public static void setCachedUsername(String username) {
//	        if (null != sp) {
//	            sp.edit().putString(KEY_CACHED_USERNAME, username).commit();
//	        }
//	    }
//
//	    public static String getCachedUsername() {
//	        if (null != sp) {
//	            return sp.getString(KEY_CACHED_USERNAME, null);
//	        }
//	        return null;
//	    }
//
//	    private static final String CACHED_CUSTID="custId";
//
//	    public static void  setCachedCustId(String custId){
//	    	  if (null != sp) {
//		            sp.edit().putString(CACHED_CUSTID, custId).commit();
//		        }
//	    }
//	    public static String getCachedCustId(){
//	    	if (null != sp) {
//	            return sp.getString(CACHED_CUSTID, null);
//	        }
//	        return null;
//	    }
//
//	    private static final String KEY_CACHED_PASSWORD="password";
//	    public static void setCachedPassword(String password){
//	    	 if (null != sp) {
//		            sp.edit().putString(KEY_CACHED_PASSWORD, password).commit();
//		        }
//	    }
//	    public static String getCachedPassword() {
//	        if (null != sp) {
//	            return sp.getString(KEY_CACHED_PASSWORD, null);
//	        }
//	        return null;
//	    }
}
