package com.litf.learning.control;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.litf.learning.utils.SharedPreferenceManager;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.MemoryCookieStore;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import okhttp3.OkHttpClient;

public class MyApplication extends Application {
	private static final String SHARED_PREFERENCE_NAME = "PublishSystem_sp";
	public static final String TAG = MyApplication.class.getSimpleName();

	private static final String PREF_CUST_ACCOUNT_NUMBER = "cust_account_number";//账号
	private static final String PREF_CUST_ACCOUNT_PASSWORD = "cust_account_password";//密码
	private static final String PREF_CUST_SELF_LOGIN = "cust_self_login";//自动登录
	private static final String PREF_CUST_TOKEN = "cust_token";//token

	private static final String PREF_CUST_ID = "cust_id";//用户ID
	private static final String PREF_CUST_NAME = "cust_name";//用户昵称
	private static final String PREF_CUST_PHONE = "cust_phone";//用户手机号（注册手机号）
	private static final String PREF_DISCUSS = "discuss_time";//评论
	private static final String PREF_SIGN_IN = "sign_in_time";//签到
	private static final String PREF_AVATAR_URL = "avatar_url";//头像url
	private static final String PREF_JPUSH_ALIAS = "jpush_alias";//极光推送别名key
	//    private String custIntegral;
	private static final String PREF_IS_ALIAS_SET = "is_alias_set";//别名是否设置key
	private String custAccountNumber;//账号
	private String custAccountPassword;//密码
	private String custToken;//token
	private boolean isSelfLogin;//自动登录
	/**
	 * 客户编号
	 */
	private String custId;
	/**
	 * 头像url
	 */
	private String avatarUrl;
	/**
	 * 昵称
	 */
	private String custName;
	/**
	 * 手机
	 */
	private String custPhone;
	/**
	 * 签到时间
	 */
//    private String lastModifiedTime;
	private String signInTime;
	/**
	 * 评论时间
	 */
	private String discussTime;
	/**
	 * 积分
	 */
	private String custInt ;
	/**
	 * 极光推送别名
	 */
	private String jpushAlias;
	/**
	 * 性别
	 */
//    private String user_gender;

	private static final String PREF_CUST_INTEGRAL = "cust_integral";//用户积分
	private boolean isAliasSet;
	private static MyApplication application;
	public static MyApplication getMyApplication() {
		return application;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		application = this;
		SharedPreferenceManager.init(getApplicationContext(), SHARED_PREFERENCE_NAME);
		CodeConstants.HEADERS = getCustId();
		//使用OkGo的拦截器
		OkHttpClient.Builder builder = new OkHttpClient.Builder();
		HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("meee");
		//日志的打印范围
		loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
		//在logcat中的颜色
		loggingInterceptor.setColorLevel(Level.INFO);
		//默认是Debug日志类型
		builder.addInterceptor(loggingInterceptor);

		//设置请求超时时间,默认60秒
		builder.readTimeout(30000, TimeUnit.MILLISECONDS);      //读取超时时间
		builder.writeTimeout(30000, TimeUnit.MILLISECONDS);     //写入超时时间
		builder.connectTimeout(30000, TimeUnit.MILLISECONDS);   //连接超时时间

		//okhttp默认不保存cookes/session信息,需要自己的设置
		//builder.cookieJar(new CookieJarImpl(new SPCookieStore(this)));            //使用sp保持cookie，如果cookie不过期，则一直有效
//        builder.cookieJar(new CookieJarImpl(new DBCookieStore(this)));              //使用数据库保持cookie，如果cookie不过期，则一直有效
		builder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));            //使用内存保存cookie,退出后失效

		//全局初始化
		OkGo.getInstance().init(this);
		OkGo.getInstance()
				.setOkHttpClient(builder.build())
//				.setConnectTimeout(3000)
//				.setReadTimeOut(3000)
//				.setWriteTimeOut(3000)
//				.setCacheMode(CacheMode.IF_NONE_CACHE_REQUEST)
//				.setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)
				.setRetryCount(3);
	}
	public void showToast(String msg) {
		Toast.makeText(application, msg + "", Toast.LENGTH_SHORT).show();
	}

	/**
	 * 获取用户ID
	 *
	 * @return
	 */
	public String getCustId() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(MyApplication.this);
		custId = preferences.getString(PREF_CUST_ID, null);
		return custId;
	}

	/**
	 * 设置用户ID
	 *
	 * @param custId
	 */
	public void setCustId(String custId) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(MyApplication.this);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(PREF_CUST_ID, custId).apply();
	}
	/**
	 * 获取用户账号
	 *
	 * @return
	 */
	public String getCustAccountNumber() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(MyApplication.this);
		custAccountNumber = preferences.getString(PREF_CUST_ACCOUNT_NUMBER, "");
		return custAccountNumber;
	}

	/**
	 * 设置用户账号
	 *
	 * @param accountNumber
	 */
	public void setCustAccountNumber(String accountNumber) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(MyApplication.this);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(PREF_CUST_ACCOUNT_NUMBER, accountNumber).apply();
	}
	/**
	 * 获取用户密码
	 *
	 * @return
	 */
	public String getCustAccountPassword() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(MyApplication.this);
		custAccountPassword = preferences.getString(PREF_CUST_ACCOUNT_PASSWORD, "");
		return custAccountPassword;
	}

	/**
	 * 设置用户密码
	 *
	 * @param accountPassword
	 */
	public void setCustAccountPassword(String accountPassword) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(MyApplication.this);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(PREF_CUST_ACCOUNT_PASSWORD, accountPassword).apply();
	}
	/**
	 * 获取用户token
	 *
	 * @return
	 */
	public String getCustToken() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(MyApplication.this);
		custToken = preferences.getString(PREF_CUST_TOKEN, CodeConstants.TOKEN);
		return custToken;
	}

	/**
	 * 设置用户token
	 *
	 * @param custToken
	 */
	public void setCustToken(String custToken) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(MyApplication.this);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(PREF_CUST_TOKEN, custToken).apply();
	}

	/**
	 * 获取是否保存密码
	 * @return
	 */
	public boolean getIsSelfLogin() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.this);
		isSelfLogin = preferences.getBoolean(PREF_CUST_SELF_LOGIN, false);
		return isSelfLogin;
	}

	/**
	 * 设置是否保存密码
	 * @param isSelfLogin
	 */
	public void setIsSelfLogin(boolean isSelfLogin) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.this);
		preferences.edit().putBoolean(PREF_CUST_SELF_LOGIN, isSelfLogin).apply();
	}
	/**
	 * 获取用户昵称
	 *
	 * @return
	 */
	public String getCustName() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(MyApplication.this);
		custName = preferences.getString(PREF_CUST_NAME, "");
		return custName;
	}

	/**
	 * 设置用户昵称
	 *
	 * @param custName
	 */
	public void setCustName(String custName) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(MyApplication.this);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(PREF_CUST_NAME, custName).apply();
	}

	/**
	 * 设置手机
	 *
	 * @param custPhone
	 */
	public boolean setCustPhone(String custPhone) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(MyApplication.this);
		SharedPreferences.Editor editor = preferences.edit();
		return editor.putString(PREF_CUST_PHONE, custPhone).commit();
	}

	/**
	 * 获取手机
	 *
	 * @return
	 */
	public String getCustPhone() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(MyApplication.this);
		custPhone = preferences.getString(PREF_CUST_PHONE, "");
		return custPhone;
	}

	/**
	 * 获取积分
	 *
	 * @return
	 */
	public String getCustInt() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(MyApplication.this);
		custInt = preferences.getString(PREF_CUST_INTEGRAL, "");
		return custInt;
	}

	/**
	 * 设置积分
	 *
	 * @param custInt
	 */
	public void setCustInt(String custInt) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(MyApplication.this);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(PREF_CUST_INTEGRAL, custInt).apply();

	}

	/**
	 * 获取上一次评论时间
	 *
	 * @return
	 */
	public String getDiscussTime() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(MyApplication.this);
		discussTime = preferences.getString(PREF_DISCUSS, null);
		return discussTime;
	}

	/**
	 * 设置最后评论时间
	 *
	 * @param discussTime
	 */
	public void setDiscussTime(String discussTime) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(MyApplication.this);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(PREF_DISCUSS, discussTime).apply();
	}

	/**
	 * 获取上一次的签到时间
	 *
	 * @return
	 */
	public String getSignInTime() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(MyApplication.this);
		signInTime = preferences.getString(PREF_SIGN_IN, null);
		return signInTime;
	}

	/**
	 * 设置最新签到时间
	 *
	 * @param signInTime
	 */

	public void setSignInTime(String signInTime) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(MyApplication.this);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(PREF_SIGN_IN, signInTime).commit();
	}

	/**
	 * 获取头像url
	 *
	 * @return
	 */
	public String getAvatarUrl() {
		if (TextUtils.isEmpty(avatarUrl)) {
			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(MyApplication.this);
			avatarUrl = preferences.getString(PREF_AVATAR_URL, null);
		}

		return avatarUrl;
	}

	/**
	 * 设置头像url
	 *
	 * @param avatarUrl
	 */
	public void setAvatarUrl(String avatarUrl) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(MyApplication.this);
		SharedPreferences.Editor editor = preferences.edit();
//		editor.putString(PREF_PORTRAIT_URL, avatarUrl).commit();
		if (editor.putString(PREF_AVATAR_URL, avatarUrl).commit()) {
			this.avatarUrl = avatarUrl;
		}
	}

	public String getJpushAlias() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.this);
		jpushAlias = preferences.getString(PREF_JPUSH_ALIAS, "");
		return jpushAlias;
	}

	public void setJpushAlias(String jpushAlias) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(MyApplication.this);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(PREF_JPUSH_ALIAS, jpushAlias).apply();
	}

	public boolean getIsAliasSet() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.this);
		isAliasSet = preferences.getBoolean(PREF_IS_ALIAS_SET, false);
		return isAliasSet;
	}

	public void setIsAliasSet(boolean isAliasSet) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.this);
		preferences.edit().putBoolean(PREF_IS_ALIAS_SET, isAliasSet).apply();

	}

	public String getCachedMainPage() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.this);
		String cachedData = preferences.getString(CodeConstants.MAIN_PAGE_CACHED, "");
		return cachedData;
	}

	public void setCachedMainPage(String json) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.this);
		preferences.edit().putString(CodeConstants.MAIN_PAGE_CACHED, json).commit();
	}

	/**
	 * 判断是否登录
	 *
	 * @return boolean
	 */

	public boolean isLogin() {
		if (TextUtils.isEmpty(getCustId()) || getCustId().equals("null")) {
			return false;
		}
		return true;
	}

	/**
	 * 退出登录
	 */
	public void reSet() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(MyApplication.this);
		SharedPreferences.Editor edit = preferences.edit();
		edit.clear();
		edit.commit();
	}

	public boolean logout() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(MyApplication.this);
		SharedPreferences.Editor edit = preferences.edit();
		edit.clear();
		return edit.commit();
	}
}
