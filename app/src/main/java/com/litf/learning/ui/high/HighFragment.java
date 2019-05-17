package com.litf.learning.ui.high;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.litf.learning.R;

public class HighFragment extends Fragment {

    private View contentView;
    public HighFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.fragment_high,container,false);
        return contentView;
    }

}
