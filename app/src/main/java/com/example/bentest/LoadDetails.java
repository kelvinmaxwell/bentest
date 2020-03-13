package com.example.bentest;



import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.text.InputFilter;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import static com.example.bentest.MainActivity.memidno;

import static android.R.layout.simple_spinner_item;

//import com.example.ses.R;


public class LoadDetails extends Fragment {
    public TextView New_Outstanding,Amount_paid;
public Button submitloanetails;
static String col1,val1,col2,val2,col3,val3;
EditText nextpaymentdate,Member,Loan_ID,Loan_Amount,Outstanding_Amount;
    final Calendar myCalendar = Calendar.getInstance();
    String guarantor="";
    Double itialloan=null,repaymentperiod=null,outstandingloan=null,amountpayable=null,datediffrence=null;


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


                        Toast.makeText(getContext(), user1.get(sessionManager1.NAME), Toast.LENGTH_SHORT).show();

                            guarantor=jsonChildNode.getString("guarantor");

                         itialloan=jsonChildNode.getDouble("totalloan");
                         outstandingloan=jsonChildNode.getDouble("loanbalance");
                            datediffrence=jsonChildNode.getDouble("DateDiff");
                                repaymentperiod= jsonChildNode.getDouble("repaymentperiod");


                                Loan_ID.setText(jsonChildNode.getString("loanid"));

                        SpannableString content = new SpannableString("KES "+new DecimalFormat("#,###.##").
                                format(jsonChildNode.getDouble("totalloan")));
                        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                        Loan_Amount.setText(content);

                                      //  Loan_Amount.setText(jsonChildNode.getString("totalloan"));
                                    Double outstanding= jsonChildNode.getDouble("loanbalance")+(0.1*repaymentperiod*itialloan);

                                    Outstanding_Amount.setText(String.valueOf(outstanding));

                                     New_Outstanding.setText(jsonChildNode.getString(String.valueOf(outstanding)));

                                    col1=jsonChildNode.getString("col1");
                                     val1=jsonChildNode.getString("val1");
                                     col2=jsonChildNode.getString("col2");
                                     val2=jsonChildNode.getString("val2");
                                     col3=jsonChildNode.getString("col3");
                                    val3=jsonChildNode.getString("val3");

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

            String sql="SELECT `loanid`,`guarantor`,`repaymentperiod`,`col1`,`val1`,`col2`,`val2`,`col3`,`val3` ,Ifnull( (sum( if( `transactiontype` = 'Loan' and  `memberid`='"+memidno+"' AND transactionoption='Borrow'  and  `Active`='active',(amount), 0 ) )\n" +
                    "                    - sum( if(  `transactiontype` = 'Loan' AND transactionoption='Payment' and  `memberid`='"+memidno+"' and `Active`='active',(amount), 0 ) ) )  ,0)    AS `loanbalance`,ifnull((SELECT TIMESTAMPDIFF(month, `date`, NOW()) from `loans` where    `memberid`='"+memidno+"' and `Active`='active' and `transactiontype`='Loan' " +
                    "and `transactionoption`='Borrow' ORDER BY ref ASC LIMIT 1),0) AS DateDiff,Ifnull( (sum( if( `transactiontype` = 'Loan'AND transactionoption='Borrow' and   `memberid`='"+memidno+"' and `Active`='active' ,(amount), 0 ) )),0) \n" +
                    "                      AS `totalloan` FROM `loans` WHERE    `memberid`='"+memidno+"' ORDER BY ref DESC LIMIT 1";





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


              String sql1= "update loans set status= 'paid' where `loanid`='"+Loan_ID.getText().toString()+"' and `memberid`='"+memidno+"' ;" ;
              String sql2="insert into loans(`account`,`amount`,`loanid`,`memberid`,`transactionoption`,`transactiontype`,`next_payment`,`repayment`,`status`,`guarantor`) VALUES('Savings','"+Amount_paid.getText().toString()+"','"+Loan_ID.getText().toString()+"','"+memidno+"'" +
                      ",'Payment','Loan','"+nextpaymentdate.getText().toString()+"','"+getcurrentdate()+"','waiting','"+guarantor+"')" ;


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
                Toast.makeText(getContext(), String.valueOf(repaymentperiod) +String.valueOf(datediffrence), Toast.LENGTH_SHORT).show();
              Double totalintrests = repaymentperiod*0.1*itialloan;
              Double amountpayablemonthly=totalintrests/repaymentperiod;
              Double amountremnaing=outstandingloan+totalintrests;
                Double paymentmade=Double.parseDouble(Amount_paid.getText().toString());
                if(amountremnaing>0) {
                    if (paymentmade < amountpayablemonthly) {
                        builder("The minimum you can pay is" + amountpayablemonthly);
                    } else if (paymentmade > amountremnaing) {
                        builder("The maximum payable is " + amountremnaing);
                    } else if (repaymentperiod <= datediffrence && paymentmade < amountremnaing) {
                        builder("It is  " + datediffrence + " month/s since you application please clear balances " + amountremnaing);
                    }
                    else{
                        updatebalances();
                    }
                }
                else{
                    builder("you have no loan balances");
                }

                Toast.makeText(getContext(), String.valueOf(itialloan), Toast.LENGTH_SHORT).show();

            }
        });
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



    public Double[] checkloanpayment(Double rpaymentperiod,Double loanbalances,Double initialamount){
        Toast.makeText(getContext(), String.valueOf(rpaymentperiod), Toast.LENGTH_SHORT).show();
        Double intrest=0.1*initialamount;


        Double totalintrest=(0.1* initialamount)*rpaymentperiod;

        Double remaining=totalintrest +loanbalances;

        Double[] arr={intrest,totalintrest,remaining};



        return arr;

    }

    public void builder(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(message).setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        })
                .setNegativeButton("Cancel",null).create().show();
    }




}
