package com.litf.learning.base;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.litf.learning.R;
import com.litf.learning.control.AppManager;
import com.litf.learning.control.MyApplication;
import com.litf.learning.utils.LogUtil;

public abstract class BaseActivity extends AppCompatActivity {

    public MyApplication application;
    public AppCompatActivity activity;
    //    public FragmentActivity activity;
    protected String TAG;
    private ProgressDialog progressDialog;
//    private CompositeSubscription mCompositeSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = MyApplication.getMyApplication();
        activity = this;
        AppManager.getAppManager().addActivity(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initArgs();
        setLayout();
        init();
        initViews();
//        ButterKnife.bind(this);
//        init();
        if (!TextUtils.isEmpty(getTopbarTitle())) {
            TextView titleTv = (TextView) findViewById(R.id.top_name_text);
            titleTv.setText(getTopbarTitle());
        }
        if (isNeedInitBack()) {
            RelativeLayout backBtn = (RelativeLayout) findViewById(R.id.top_back_btn);
            backBtn.setVisibility(View.VISIBLE);
            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BaseActivity.this.finish();
                    initBackBtn();
                }
            });
        }
    }

    private void init() {
        TAG = getClass().getSimpleName();
        if (progressDialog == null)
            progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("正在请求数据");
    }

    protected void initArgs() {
    }

    abstract protected void setLayout();

    abstract protected void initViews();

    abstract protected boolean isNeedInitBack();

    abstract protected String getTopbarTitle();

    protected void initBackBtn() {

    }
//    /**
//     * 左侧有返回键的标题栏
//     * <p>如果在此基础上还要加其他内容,比如右侧有文字按钮,可以获取该方法返回值继续设置其他内容
//     *
//     * @param title 标题
//     */
//    protected TitleBuilder initBackTitle(String title) {
//        return new TitleBuilder(this)
//                .setTitleText(title)
//                //.setLeftImage(R.dr)
//                .setLeftOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        finish();
//                    }
//                });
//    }

//    /**
//     * 跳转页面,无extra简易型
//     *
//     * @param tarActivity 目标页面
//     */
//    public void intent2Activity(Class<? extends Activity> tarActivity) {
//        ActivityUtils.intent2Activity(activity, tarActivity);
//    }
//
//    /**
//     * 跳转页面
//     *
//     * @param tarActivity 目标页面
//     */
//    public void intent2Activity(Class<? extends Activity> tarActivity, Bundle bundle) {
//        ActivityUtils.intent2Activity(activity, tarActivity, bundle);
//    }
//    public void intent2Activity(Class<? extends Activity> tarActivity,Bundle bundle){
//        Intent intent=new Intent(this,tarActivity);
//        intent.putExtra(CodeConstants.BUNDLE_KEY,bundle);
//        startActivity(intent);
//    }

    public void showToast(String msg) {
        application.showToast(msg);
    }

    public void showLog(String msg) {
        LogUtil.i(TAG, msg);
    }

    public void showProgressDialog() {
        progressDialog.show();
    }

    public void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }


//    public void addSubscription(Observable observable, Subscriber subscriber) {
//        if (mCompositeSubscription == null) {
//            mCompositeSubscription = new CompositeSubscription();
//        }
//
//        mCompositeSubscription.add(observable
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(subscriber));
//    }
//
//    public void addSubscription(Subscription subscription) {
//        if (mCompositeSubscription == null) {
//            mCompositeSubscription = new CompositeSubscription();
//        }
//        mCompositeSubscription.add(subscription);
//    }
//
//    public void onUnsubscribe() {
//        showLog("onUnsubscribe is called");
//        //取消注册，以避免内存泄露
//        if (mCompositeSubscription != null && mCompositeSubscription.hasSubscriptions())
//            mCompositeSubscription.unsubscribe();
//    }

    @Override
    protected void onDestroy() {
//        onUnsubscribe();
        super.onDestroy();
    }
}
