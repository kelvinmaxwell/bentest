package com.example.bentest;



import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    public TextView Loan_ID,Member,Loan_Amount,Outstanding_Amount,New_Outstanding,Amount_paid;
public Button submitloanetails;


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
       Amount_paid=view.findViewById(R.id.Amount_repaid);
       submitloanetails=view.findViewById(R.id.submitloanpayment);


     SessionManager   sessionManager=new SessionManager(getContext());

        HashMap<String,String> user=sessionManager.getUserDetail();



        Loan_ID.setText("10");
        Member.setText(user.get(sessionManager.NAME));

        Loan_Amount.setText("10");
        Outstanding_Amount.setText("10");
        New_Outstanding.setText("10");

getloanbalances();
submitloanspayment();

        return view;
    }



//get loanbalances


public void getloanbalances(){

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




                       Loan_Amount.setText(jsonChildNode.getString("totalloan"));

                       Outstanding_Amount.setText(jsonChildNode.getString("loanbalance"));


                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getContext(), "Error while reading nertwork", Toast.LENGTH_SHORT).show();

        }
    }) {
        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            Map<String, String> params = new HashMap<String, String>();


            String sql="SELECT  Ifnull( (sum( if( `transactiontype` = 'Loan'AND transactionoption='Borrow',(amount), 0 ) )" +
                    "- sum( if(  `transactiontype` = 'Loan' AND transactionoption='Payment' ,(amount), 0 ) ) )  ,0)    AS `loanbalance`,Ifnull( (sum( if( `transactiontype` = 'Loan'AND transactionoption='Borrow',(amount), 0 ) )),0) " +
                    "  AS `totalloan` FROM `loans` WHERE    `memberid`='1' ORDER BY ref DESC LIMIT 1";





            params.put("function", "getloanbalances");
            params.put("sql",sql);



            return params;
        }
    };
    MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);



}

  public void   updatebalances(){

      StringRequest stringRequest = new StringRequest(Request.Method.POST,getString(R.string.url), new Response.Listener<String>() {
          @Override
          public void onResponse(String response) {
              //  Log.d(TAG,response);



          }
      }, new Response.ErrorListener() {
          @Override
          public void onErrorResponse(VolleyError error) {
              Toast.makeText(getContext(), "Error while reading nertwork", Toast.LENGTH_SHORT).show();

          }
      }) {
          @Override
          protected Map<String, String> getParams() throws AuthFailureError {
              Map<String, String> params = new HashMap<String, String>();

              char id =Member.getText().toString().charAt(0);


              String sql="insert into loans(`account`,`amount`,`loanid`,`memberid`,`transactionoption`,`transactiontype`)" +
                      " VALUES('Savings','"+Amount_paid.getText().toString()+"','122','"+id+"','Payment','Loan')";

              System.out.println(sql);



              params.put("function", "insert");
              params.put("sql",sql);



              return params;
          }
      };
      MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
getloanbalances();


  }

  public void submitloanspayment(){

        submitloanetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatebalances();
            }
        });
  }


}
