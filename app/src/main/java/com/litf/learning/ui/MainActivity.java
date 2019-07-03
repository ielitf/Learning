package com.litf.learning.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioGroup;
import com.litf.learning.R;
import com.litf.learning.ui.high.HighFragment;
import com.litf.learning.ui.main.MainFragment;
import com.litf.learning.ui.me.MeFragment;
import com.litf.learning.ui.middle.MiddleFragment;

public class MainActivity extends AppCompatActivity {
    private RadioGroup radioGroup;
    private FragmentManager fragmentManager;
    private Fragment mFragment;
    private MainFragment mainFragment = new MainFragment();
    private MiddleFragment middleFragment = new MiddleFragment();
    private HighFragment highFragment = new HighFragment();
    private MeFragment meFragment = new MeFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        mFragment = mainFragment;
        transaction.add(R.id.layout_content, mFragment).commit();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.menu_base:
                        switchFragment(mainFragment);
                        break;
                    case R.id.menu_high:
                        switchFragment(middleFragment);
                        break;
                    case R.id.menu_special:
                        switchFragment(highFragment);
                        break;
                    case R.id.menu_mine:
                        switchFragment(meFragment);
                        break;
                    default:
                        break;
                }
            }
        });

    }

    private void initViews() {
        radioGroup = findViewById(R.id.menu_rGroup);
    }

    private void switchFragment(Fragment fragment) {
        if (mFragment != fragment) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (!fragment.isAdded()) {
                transaction.hide(mFragment).add(R.id.layout_content, fragment).commit();
            } else {
                transaction.hide(mFragment).show(fragment).commit();
            }
            mFragment = fragment;
        }
    }
}
