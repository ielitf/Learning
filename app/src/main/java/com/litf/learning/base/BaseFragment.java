package com.litf.learning.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.litf.learning.R;
import com.litf.learning.control.MyApplication;
import com.litf.learning.utils.ActivityUtils;
import com.litf.learning.utils.LogUtil;

import java.lang.reflect.Field;


public abstract class BaseFragment extends Fragment {
    public MyApplication application;
    public BaseActivity activity;
    public Context mContext;
    protected String TAG = getClass().getName();
    protected LayoutInflater inflater;
    protected View contentView;
    protected ViewGroup container;
//    protected CompositeSubscription mCompositeSubscription;

    @Override
    public final View onCreateView(LayoutInflater inflater,
                                   ViewGroup container, Bundle savedInstanceState) {
        activity = (BaseActivity) getActivity();
        mContext = activity.getApplicationContext();
        application = MyApplication.getMyApplication();
        this.inflater = inflater;
        this.container = container;
        initArgs();
        onCreateView(savedInstanceState);
        if (contentView == null)
            return super.onCreateView(inflater, container, savedInstanceState);
        if (contentView != null) {
            if (!TextUtils.isEmpty(getTopbarTitle())) {
                final TextView titleText = (TextView) contentView.findViewById(R.id.top_name_text);
                titleText.setText(getTopbarTitle());
                if (hasPopWindow()) {
                    final LinearLayout myPopLayout = (LinearLayout) contentView.findViewById(R.id.my_pop_layout);
                    final ImageView popImage = (ImageView) contentView.findViewById(R.id.poping_image);
                    popImage.setVisibility(View.VISIBLE);
                    myPopLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showPopWindow(myPopLayout, titleText, popImage);
                        }
                    });
                }
            }
            if (isNeedInitBack()) {
                RelativeLayout backBtn = (RelativeLayout) contentView.findViewById(R.id.top_back_btn);
                backBtn.setVisibility(View.VISIBLE);
                backBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initBackBtn();
                    }
                });
            }

        }
        return contentView;
    }


    protected abstract void onCreateView(Bundle savedInstanceState);

    public void setContentView(View view) {
        contentView = view;
    }

    public View getContentView() {
        return contentView;
    }

    public void setContentView(int layoutResID) {
        contentView = inflater.inflate(layoutResID, container, false);
    }

    public View findViewById(int id) {
        if (contentView != null)
            return contentView.findViewById(id);
        return null;
    }

    protected void intent2Activity(Class<? extends Activity> tarActivity) {
        ActivityUtils.intent2Activity(activity, tarActivity);
    }

    protected void intent2Activity(Class<? extends Activity> tarActivity, Bundle bundle) {
        ActivityUtils.intent2Activity(activity, tarActivity, bundle);
    }

    protected void showToast(String msg) {
        //ToastUtils.showToast(mActivity, msg, Toast.LENGTH_SHORT);
        activity.showToast(msg);
    }

    protected void showLog(String msg) {
        LogUtil.i(TAG, msg);
    }

    protected void showProgressDialog() {
        activity.showProgressDialog();
    }

    protected void dismissProgressDialog() {
        activity.dismissProgressDialog();
    }


//
//    public void addSubscription(Observable observable, Subscriber subscriber) {
//        if (mCompositeSubscription == null) {
//            mCompositeSubscription = new CompositeSubscription();
//        }
//        mCompositeSubscription.add(observable
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(subscriber));
//    }
//
//
//    public void addSubscription(Subscription subscription) {
//        if (mCompositeSubscription == null) {
//            mCompositeSubscription = new CompositeSubscription();
//        }
//        mCompositeSubscription.add(subscription);
//    }
//
//    private void onUnsubscribe() {
//        //取消注册，以避免内存泄露
//        if (mCompositeSubscription != null) {
//            mCompositeSubscription.unsubscribe();
//        }
//    }

    public Context getApplicationContext() {
        return mContext;
    }


    @Override
    public void onDestroyView() {
        LogUtil.d(TAG, "onDestroyView() : ");
        super.onDestroyView();
        contentView = null;
        container = null;
        inflater = null;
    }

    @Override
    public void onDetach() {
        LogUtil.d(TAG, "onDetach() : ");
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDestroy() {
        LogUtil.v(TAG, "onDestroy() : ");
//        onUnsubscribe();
//        mCompositeSubscription = null;
        super.onDestroy();
    }

    //    abstract protected void setLayout();
    abstract protected boolean hasPopWindow();

    abstract protected boolean isNeedInitBack();

    abstract protected String getTopbarTitle();

    protected void initBackBtn() {
        activity.finish();
    }

    protected void showPopWindow(LinearLayout myPopLayout, TextView titleText, ImageView popImage) {
    }

    protected void initArgs() {
    }
}

