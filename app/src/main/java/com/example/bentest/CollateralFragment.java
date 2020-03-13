package com.example.bentest;


import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

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


import static com.example.bentest.MainActivity.memidno;
//import com.example.ses.R;


public class CollateralFragment extends Fragment {
    TextView col11,val11,col22,val22,col33,val33,total;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collateral, container, false);
        // Inflate the layout for this fragment
        col11=view.findViewById(R.id.col1);
        val11=view.findViewById(R.id.val1);

        col22=view.findViewById(R.id.col2);
        val22=view.findViewById(R.id.val2);

        col33=view.findViewById(R.id.col3);
        val33=view.findViewById(R.id.val3);
        total=view.findViewById(R.id.totalcol);


        Toast.makeText(getContext(), memidno, Toast.LENGTH_SHORT).show();
        getcolaterals();

        return view;


    }




//    public void  setvalues(){
//
//
//        val11.setText(val1);
//
//        col22.setText(col2);
//        val33.setText(val2);
//
//        col33.setText(col3);
//        val33.setText(val3);
//    }





    public void getcolaterals(){


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

                            SessionManager  sessionManager1=new SessionManager(getContext());

                            HashMap<String,String> user1=sessionManager1.getUserDetail();




                            col11.setText(jsonChildNode.getString("col1"));
                           val11.setText(jsonChildNode.getString("val1"));
                            col22.setText(jsonChildNode.getString("col2"));
                            val22.setText(jsonChildNode.getString("val2"));
                            col33.setText(jsonChildNode.getString("col3"));
                            val33.setText(jsonChildNode.getString("val3"));
                           total.setText("Total   " +jsonChildNode.getString("total"));


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


                String sql="SELECT `loanid`,`guarantor`,`repaymentperiod`,`col1`,`val1`,`col2`,`val2`,`col3`,`val3` ,Ifnull( (sum( if( `transactiontype` = 'Loan' and  `memberid`='"+memidno+"' AND transactionoption='Borrow'  and  `Active`='active',(amount), 0 ) )\n" +
                        "                    - sum( if(  `transactiontype` = 'Loan' AND transactionoption='Payment' and  `memberid`='"+memidno+"' and `Active`='active',(amount), 0 ) ) )  ,0)    AS `loanbalance`,ifnull((SELECT TIMESTAMPDIFF(month, `date`, NOW()) from `loans` where    `memberid`='"+memidno+"' and `Active`='active' and `transactiontype`='Loan' " +
                        "and `transactionoption`='Borrow' ORDER BY ref ASC LIMIT 1),0) AS DateDiff,Ifnull( (sum( if( `transactiontype` = 'Loan'AND transactionoption='Borrow' and   `memberid`='"+memidno+"' and `Active`='active' ,(amount), 0 ) )),0) \n" +
                        "                      AS `totalloan`,ifnull((SELECT sum(`val1`+`val2`+`val3`)from `loans` where `transactiontype` = 'Loan' and  `memberid`='"+memidno+"' AND transactionoption='Borrow'  and  `Active`='active'),0)as `total` FROM `loans` WHERE    `memberid`='"+memidno+"' and `Active`='active' ORDER BY ref DESC LIMIT 1";





                params.put("function", "getresult");
                params.put("sql",sql);



                return params;
            }
        };
        MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);



    }
}
