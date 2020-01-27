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

    String url=getResources().getString(R.string.urll);

    private ArrayList<get_setter> ArrayList;

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






        return view;
    }



    private void retrieveJSON() {


        StringRequest stringRequest = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("strrrrr", ">>" + response);


                        try {

                            JSONObject obj = new JSONObject(response);
                            if (obj.optString("status").equals("true")) {

                          ArrayList = new ArrayList<>();
                                JSONArray dataArray = obj.getJSONArray("data");

                                for (int i = 0; i < dataArray.length(); i++) {

                                    get_setter playerModel = new get_setter();
                                    JSONObject dataobj = dataArray.getJSONObject(i);
                                     Loan_ID.setText(dataobj.getString("id"));
                                     Member.setText(dataobj.getString("member"));
                                    Member.setText(dataobj.getString("member"));
                                    Loan_Amount.setText(dataobj.getString("total_amount"));
                                     Outstanding_Amount.setText(dataobj.getString("outstandingamount"));
                                     New_Outstanding.setText(dataobj.getString("new oystanding"));
                                    playerModel.setmainid(dataobj.getString("mixid"));



                              ArrayList.add(playerModel);

                                }




                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error in toast if occurrs
                        System.out.println(error.getMessage());

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();





                params.put("function","getloanbalances");
                params.put("sql","sql");
                return params;

             }
        };

        // request queue
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        requestQueue.add(stringRequest);


    }


}
