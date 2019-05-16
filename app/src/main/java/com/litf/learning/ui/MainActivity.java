package com.litf.learning.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.litf.learning.R;
import com.litf.vrlibrary.VrPlayerManager;

public class MainActivity extends AppCompatActivity {
    private RadioGroup radioGroup;
    private RadioButton radioButton1;
    private ItemFragment itemFragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        radioButton1.setChecked(true);
        fragmentManager = getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                        showFragment(transaction,checkedId);

            }
        });

    }

    private void initViews() {
        itemFragment = new ItemFragment();
        radioGroup = findViewById(R.id.menu_rGroup);
        radioButton1 = findViewById(R.id.menu_base);
    }
    private void showFragment(FragmentTransaction transaction, int checkedId) {

    }
}
