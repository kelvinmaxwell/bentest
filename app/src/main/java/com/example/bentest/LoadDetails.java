package com.example.bentest;



import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.R.layout.simple_spinner_item;

//import com.example.ses.R;


public class LoadDetails extends Fragment {
    public TextView Loan_ID,Member,Loan_Amount,Outstanding_Amount,New_Outstanding;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =  inflater.inflate(R.layout.fragment_load_details, container, false);
        Loan_ID=view.findViewById(R.id.textView12);
        Member=view.findViewById(R.id.textView15);
        Loan_Amount=view.findViewById(R.id.textView16);
       Outstanding_Amount=view.findViewById(R.id.textView17);
       New_Outstanding=view.findViewById(R.id.textView18);



        Loan_ID.setText("10");
        Member.setText("10");

        Loan_Amount.setText("10");
        Outstanding_Amount.setText("10");
        New_Outstanding.setText("10");


        return view;
    }





}
