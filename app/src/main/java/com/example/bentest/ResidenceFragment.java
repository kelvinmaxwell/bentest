package com.example.bentest;


import android.app.AlertDialog;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
//import com.example.ses.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.bentest.MainActivity.memidno;


public class ResidenceFragment extends Fragment {
     TextView county,town,location,address,sublocation;
     Button back,collateral;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_residence, container, false);

        county = (TextView)view.findViewById(R.id.county);
        town = (TextView)view.findViewById(R.id.town);
        location = (TextView)view.findViewById(R.id.location);
        sublocation = (TextView)view.findViewById(R.id.sublocation);
        address = (TextView)view.findViewById(R.id.address);
        back = (Button)view.findViewById(R.id.button4);
        collateral = (Button)view.findViewById(R.id.button5);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    System.out.println(response);
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if(success){


                        JSONArray jsonMainNode = jsonObject.optJSONArray("data");
                        for (int i = 0; i < jsonMainNode.length(); i++) {
                            JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                            county.setText(jsonChildNode.getString("county"));
                            town.setText(jsonChildNode.getString("town"));
                            location.setText(jsonChildNode.getString("location"));
                            sublocation.setText(jsonChildNode.getString("sublocation"));
                            address.setText(jsonChildNode.getString("address"));
                        }



                    }
                    else{

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Error encountered, please go back and try again").setNegativeButton("Okay",null).create().show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        };
        String sql = "SELECT * FROM members WHERE `id number` = '"+memidno+"'";
        ActionRequest driverLoginRequest = new ActionRequest("dboperations.php","query",sql,responseListener);
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(driverLoginRequest);
        return view;
    }
}
