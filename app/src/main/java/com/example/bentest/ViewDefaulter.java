package com.example.bentest;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

//import com.example.ses.R;

public class ViewDefaulter extends Fragment {
    TextView name,group,memid,groupid,loanid,issuedon,amount,amountpaid,outstanding,lastpaid,dueon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_defaulter, container, false);
        name = view.findViewById(R.id.textView21);
        return view;
    }}
