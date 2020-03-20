package com.example.bentest;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
//import com.example.ses.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.bentest.LoanApplicationForm.memid;
import static com.example.bentest.Tab1Fragment.Selectedmember;



public class Tab4Fragment extends Fragment {
    static EditText county,town,location,address,sublocation;
    Button button;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.tab4_fragment,container,false);

        county = (EditText)view.findViewById(R.id.county);
        town = (EditText)view.findViewById(R.id.town);
        location = (EditText)view.findViewById(R.id.location);
        sublocation = (EditText)view.findViewById(R.id.sublocation);
        address = (EditText)view.findViewById(R.id.address);
        button = (Button)view.findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMember();
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
                        builder.setMessage("There was an error encountered my dear, please go back and try again").setNegativeButton("Okay",null).create().show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        };
String sql = "SELECT * FROM members WHERE memberid = '"+Selectedmember+"'";
        ActionRequest driverLoginRequest = new ActionRequest("dbqueries.php","query",sql,responseListener);
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(driverLoginRequest);

        return view;
    }

    ProgressDialog progressDialog;
    public void updateMember() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading, please wait...");
        progressDialog.show();
        Response.Listener<String> responseListener = new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                try {
                    System.out.println(response);
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    progressDialog.dismiss();
                    if(success){
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("Member residence details updated").setNegativeButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create().show();                 }
                    else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("An error was encountered").setNegativeButton("Retry",null).create().show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };

        String Address =  address.getText().toString();
        String County =  county.getText().toString();
        String Town =  town.getText().toString();
        String Location =  location.getText().toString();
        String Sublocation =  sublocation.getText().toString();

String sql = "UPDATE members set address = '"+Address+"', county = '"+County+"', town = '"+Town+"', location = '"+Location+"', sublocation =  '"+Sublocation+"' where `id number` = '"+memid+"'";
        ActionRequest recordOffence = new ActionRequest("dbqueries.php","action",sql,responseListener);
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(recordOffence);
    }

}
