package com.example.bentest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class rates extends AppCompatActivity {
public EditText loan_feetxt,loan_intrests,advance_fee,advance_intrests,advance_fines,loan_fines;
public Button updatebtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rates);
        loan_feetxt=findViewById(R.id.loan_fee);
        loan_intrests=findViewById(R.id.loan_intrests);
        advance_fee=findViewById(R.id.advance_fee);
        advance_intrests=findViewById(R.id.advance_intrests);
        advance_fines=findViewById(R.id.advance_fines);
        loan_fines=findViewById(R.id.loan_fines);
        updatebtn=findViewById(R.id.updatebtn);


getrates();
clickupdate();


    }
    public void getrates(){


        StringRequest stringRequest = new StringRequest(Request.Method.POST,getString(R.string.url), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //  Log.d(TAG,response);
                System.out.println(response);
                try {
                    System.out.println(response);
                    JSONObject jsonObject = new JSONObject(response);
                    System.out.println(response);
                    boolean success = jsonObject.getBoolean("status");
                    if (success) {
                        //set this to the spinner

                        JSONArray jsonMainNode = jsonObject.optJSONArray("data");
                        for (int i = 0; i < jsonMainNode.length(); i++) {
                            JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                            loan_feetxt.setText(jsonChildNode.getString("loan_fee"));
                            loan_intrests.setText(jsonChildNode.getString("loan_intrests"));
                            advance_fee.setText(jsonChildNode.getString("advance_fee"));
                            advance_intrests.setText(jsonChildNode.getString("advance_intrests"));
                           advance_fines.setText(jsonChildNode.getString("advance_fines"));
                            loan_fines.setText(jsonChildNode.getString("loans_fines"));
                            feessession sessionManager=new feessession(getApplicationContext());

                            sessionManager.createSession(jsonChildNode.getString("loan_fee"),jsonChildNode.getString("loan_intrests")
                                    ,jsonChildNode.getString("advance_fee"),jsonChildNode.getString("advance_intrests"),jsonChildNode.getString("advance_fines"),jsonChildNode.getString("loans_fines"));


                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error while reading nertwork", Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();


                String sql="select * from  fees";





                params.put("function", "getresult");
                params.put("sql",sql);



                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);



    }


public void updatefees(){

    StringRequest stringRequest = new StringRequest(Request.Method.POST,getString(R.string.url), new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            //  Log.d(TAG,response);
            System.out.println(response);


        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getApplicationContext(), "Error while reading nertwork", Toast.LENGTH_SHORT).show();

        }
    }) {
        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            Map<String, String> params = new HashMap<String, String>();


            String sql="update fees set `loan_fee`='"+loan_feetxt.getText().toString()+"',`loan_intrests`='"+loan_intrests.getText().toString()+"',`advance_fee`='"+advance_fee.getText().toString()+"',`advance_intrests`='"+advance_intrests.getText().toString()+"',`advance_fines" +
                    "`='"+advance_fines.getText().toString()+"',`loans_fines`='"+loan_fines.getText().toString()+"'";





            params.put("function", "insert");
            params.put("sql",sql);



            return params;
        }
    };
    MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    Toast.makeText(this, "updates made", Toast.LENGTH_SHORT).show();
getrates();

}

public void clickupdate(){
updatebtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        updatefees();
    }
});
}

}
