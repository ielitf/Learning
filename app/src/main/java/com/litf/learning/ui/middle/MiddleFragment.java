package com.litf.learning.ui.middle;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.litf.learning.R;

public class MiddleFragment extends Fragment {

    private View contentView;
    public MiddleFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.fragment_middle,container,false);
        return contentView;
    }

}
