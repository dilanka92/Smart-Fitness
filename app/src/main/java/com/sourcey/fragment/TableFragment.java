package com.sourcey.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.sourcey.smartfitness.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TableFragment extends Fragment {
    @Bind(R.id.input_age)
    EditText _ageText;
    @Bind(R.id.input_height)
    EditText _heightText;
    @Bind(R.id.input_weight)
    EditText _weightText;
    @Bind(R.id.btn_signup)
    Button _signupButton;

    public TableFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_table, container, false);
        ButterKnife.bind(getActivity());

        return rootView;
    }

}
