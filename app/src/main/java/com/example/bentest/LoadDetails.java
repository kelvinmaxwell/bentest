package com.example.bentest;



import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.R.layout.simple_spinner_item;

//import com.example.ses.R;


public class LoadDetails extends Fragment {
    public TextView Loan_ID,Member,Loan_Amount,Outstanding_Amount,New_Outstanding,Amount_paid;
public Button submitloanetails;
EditText nextpaymentdate;
    final Calendar myCalendar = Calendar.getInstance();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =  inflater.inflate(R.layout.fragment_load_details, container, false);
        Loan_ID=view.findViewById(R.id.textView12);
        Member=view.findViewById(R.id.textView15);
        Loan_Amount=view.findViewById(R.id.textView16);
       Outstanding_Amount=view.findViewById(R.id.textView17);
       New_Outstanding=view.findViewById(R.id.amountdue);
       Amount_paid=view.findViewById(R.id.Amount_repaid);
       submitloanetails=view.findViewById(R.id.submitloanpayment);
        nextpaymentdate= view.findViewById(R.id.next_repayemnt);


     SessionManager   sessionManager=new SessionManager(getContext());

        HashMap<String,String> user=sessionManager.getUserDetail();



        Loan_ID.setText("0");
        Member.setText(user.get(sessionManager.NAME));

        Loan_Amount.setText("0");
        Outstanding_Amount.setText("0");
        New_Outstanding.setText("0");
datepicker();
getloanbalances();
submitloanspayment();

        return view;
    }



//get loanbalances


public void getloanbalances(){
    checkdupdate();

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

int duepayment=Integer.valueOf(jsonChildNode.getString("totalloan"))/12;
if(New_Outstanding.getText().toString().contains("due")){
New_Outstanding.setText(String.valueOf(duepayment));
    Amount_paid.setVisibility(View.VISIBLE);
   Amount_paid.setFilters(new InputFilter[]{ new MinMaxFilter("1", String.valueOf(duepayment))});
submitloanetails.setVisibility(View.VISIBLE);
nextpaymentdate.setVisibility(View.VISIBLE);}
else{
Amount_paid.setVisibility(View.GONE);
    submitloanetails.setVisibility(View.GONE);
    nextpaymentdate.setVisibility(View.GONE);}

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
            char id =Member.getText().toString().charAt(0);

            String sql="SELECT  Ifnull( (sum( if( `transactiontype` = 'Loan'AND transactionoption='Borrow',(amount), 0 ) )" +
                    "- sum( if(  `transactiontype` = 'Loan' AND transactionoption='Payment' ,(amount), 0 ) ) )  ,0)    AS `loanbalance`,Ifnull( (sum( if( `transactiontype` = 'Loan'AND transactionoption='Borrow',(amount), 0 ) )),0) " +
                    "  AS `totalloan` FROM `loans` WHERE    `memberid`='"+id+"' ORDER BY ref DESC LIMIT 1";





            params.put("function", "getresult");
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
System.out.println(response);


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


              String sql1= "update loans set status= 'paid' where `loanid`='1/SES1/2019' and `memberid`='"+id+"' and `status`='waiting';" ;
              String sql2="insert into loans(`account`,`amount`,`loanid`,`memberid`,`transactionoption`,`transactiontype`,`repayment`,`next_payment`,`status`) VALUES('Savings','"+Amount_paid.getText().toString()+"','1/SES1/2019','"+id+"'" +
                      ",'Payment','Loan','"+nextpaymentdate.getText().toString()+"','"+getcurrentdate()+"','waiting');" ;


              System.out.println("maxi"+sql1+sql2);



              params.put("function", "transactions");
              params.put("sql1",sql1);
              params.put("sql2",sql2);



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
             //  nextrepayment();
            }
        });
  }

  public String checkdupdate(){


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


if(jsonChildNode.getString("nextrepayment").equalsIgnoreCase("due")){
    New_Outstanding.setText("Next payment date:  " + jsonChildNode.getString("nextrepayment"));

}
else{
                              New_Outstanding.setText("Next payment date:  " + jsonChildNode.getString("next_payment"));


                          }
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

              char id =Member.getText().toString().charAt(0);


              String sql="SELECT     next_payment  ,if( `memberid`='"+id+"' AND `next_payment`>='"+getcurrentdate()+"' and `status`='waiting','paid' ,'due')  AS `nextrepayment` " +
                      "FROM `loans` WHERE    `memberid`='"+id+"' and `status`='waiting' order BY ref DESC LIMIT 1";

              System.out.println(sql);



              params.put("function", "getresult");
              params.put("sql",sql);



              return params;
          }
      };
      MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
        return  null;
  }


public String getcurrentdate(){
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String currentDateandTime = sdf.format(new Date());
    return  currentDateandTime;

}

    public void  datepicker(){
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateLabel();
            }

        };

        nextpaymentdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // TODO Auto-generated method stub
                new DatePickerDialog(getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }



    private void updateLabel() {
        String myFormat = "YYYY-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);

        nextpaymentdate.setText(sdf.format(myCalendar.getTime()));
    }



}
