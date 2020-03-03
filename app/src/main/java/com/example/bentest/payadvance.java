package com.example.bentest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.text.InputType;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.PostResponseAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class payadvance extends AppCompatActivity implements AsyncResponse, AdapterView.OnItemClickListener, View.OnClickListener {

Button back;

    private static final String URL = "http://192.168.43.78/www/html/seskenya/";
    String Paymntsmode[] = {
            "Disbursement Method",
            "CASH",
            "MPESA",
            "CHEQUE"
    };


    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String currentDateandTime = sdf.format(new Date());

    TextView totalsaving;
    TextView lastmonthsaving;
    ArrayList<String> Members = new ArrayList<>();
    ArrayAdapter<CharSequence>adapter;
    EditText savingamount;
    AutoCompleteTextView autoCompleteTextView;
    EditText advanceid;
    String Selectedmember = "";
    String StringPassed = "";
    String function = "";
    String excecutingcategory = "";
    private android.widget.Spinner Spinner,choosemodespn,chooseaccounts;

    ActionRequest driverLoginRequest;
    RequestQueue requestQueue;
    String m_Text = "";
    String SelectedModeOfDisbursement = "";

    private android.widget.Spinner Spinnerdisbursementmode;
    ArrayList<String> MemberGroups = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payadvance);

chooseaccounts=findViewById(R.id.chooseaccountspn);
        Spinner = (android.widget.Spinner) findViewById(R.id.spinner);
        Spinnerdisbursementmode=(android.widget.Spinner) findViewById(R.id.Spinnerdisbursementmode);

        MemberGroups.add("Select Group");
advanceid=findViewById(R.id.loanid);
back=findViewById(R.id.backbtn);
        savingamount= (EditText) findViewById(R.id.choosemode);
savingamount.setEnabled(true);
backpress();
accountselectspinner();
savings();



//        ((EditText)findViewById(R.id.savingamount)).addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if(s != null && s.length() > 0 && s.charAt(s.length() - 1) == ' '){
//
//
//                    //newavbal.setText(""+totalsavingstoadd+Double.parseDouble(s.toString()));
//
//
//                }
//            }
//        });

    }

    public  void savings(){

        HashMap postData = new HashMap();
        postData.put("function", "query");
        postData.put("StringPassed", "select name from groups");
        excecutingcategory = "MemberGroups";


        PostResponseAsyncTask loginTask =
                new PostResponseAsyncTask(payadvance.this, postData,
                        payadvance.this);
        loginTask.execute(getString(R.string.url));




        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                getApplicationContext(), R.layout.spinner_item, MemberGroups) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {

                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        Spinner.setAdapter(spinnerArrayAdapter);

        Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);


                if (position > 0) {

                    autoCompleteTextView.setEnabled(true);

                    StringPassed = "select concat(memberid,' ',firstname,' ',secondname) as name " +
                            "from members inner join groups on groups.id=members.group WHERE `name`='"+selectedItemText+"'  ";
                    Members.clear();



                    Response.Listener<String> responseListener1 = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                System.out.println(response);
                                JSONObject jsonObject = new JSONObject(response);
                                boolean success = jsonObject.getBoolean("success");

                                if (success) {

                                    JSONArray jsonMainNode = jsonObject.optJSONArray("data");
                                    for (int i = 0; i < jsonMainNode.length(); i++) {

                                        JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                                        Members.add(jsonChildNode.getString("name"));

                                    }

                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, Members);
                                    autoCompleteTextView.setAdapter(adapter);


                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "failed getting members " + e,
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    };
                    function = "query";
                    driverLoginRequest = new ActionRequest("dbqueries.php", function, StringPassed, responseListener1);
                    requestQueue = Volley.newRequestQueue(getApplicationContext());
                    requestQueue.add(driverLoginRequest);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                autoCompleteTextView.setEnabled(false);

            }
        });




        totalsaving= (TextView) findViewById(R.id.totalsaving);
        lastmonthsaving= (TextView) findViewById(R.id.lastmonthsaving);



        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.Membernu);
        autoCompleteTextView.setEnabled(false);
        autoCompleteTextView.setClickable(true);
        autoCompleteTextView.setOnClickListener(this);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Selectedmember = parent.getItemAtPosition(position).toString().split(" ")[0];

                StringPassed = "SELECT type_id,ifnull((select amount FROM  \n" +
                        "                            `transactions` WHERE `account`='"+chooseaccounts.getSelectedItem().toString()+"' AND   `memberid`='"+Selectedmember+"' AND \n" +
                        "                            transactiontype='BORROW' AND transactionoption='ADVANCE' ORDER BY ref DESC LIMIT 1 ),0) as initial_amount\n" +
                        ",ifnull((select repaymentperiod FROM `transactions` WHERE `account`='"+chooseaccounts.getSelectedItem().toString()+"' AND   `memberid`='"+Selectedmember+"' AND transactiontype='BORROW' AND transactionoption='ADVANCE' ORDER BY ref DESC LIMIT 1 ),0) as repaymentitme, Ifnull( (sum( if( `transactiontype` = 'BORROW' AND `account`='"+chooseaccounts.getSelectedItem().toString()+"' and `transactionoption`='ADVANCE' and status='Active' ,(amount), 0 ) )-" +
                        "sum( if(  `transactiontype` = 'PAYMENTS' AND `transactionoption`='ADVANCE' and `account`='"+chooseaccounts.getSelectedItem().toString()+"' ,(amount), 0 ) ) )  ,0)    AS `advancebalance`," +
                        " Ifnull((select DATEDIFF(DATE_ADD(date, INTERVAL repaymentperiod MONTH),NOW()) FROM  " +
                        " `transactions` WHERE `account`='"+chooseaccounts.getSelectedItem().toString()+"' AND   `memberid`='"+Selectedmember+"' AND " +
                        " transactiontype='BORROW' AND transactionoption='ADVANCES' ORDER BY ref DESC LIMIT 1),0)" +
                        " AS remainingdays,ifnull((SELECT TIMESTAMPDIFF(month, `date`, NOW()) from `transactions` where `account`='"+chooseaccounts.getSelectedItem().toString()+"' AND   `memberid`='"+Selectedmember+"' AND " +
                        "                             transactiontype='BORROW' AND transactionoption='ADVANCE'),0) AS DateDiff  FROM `transactions` WHERE `account`='"+chooseaccounts.getSelectedItem().toString()+"' AND   `memberid`='"+Selectedmember+"' and `status`='Active' " +
                        " ORDER BY ref DESC LIMIT 1 " ;

                HashMap postData = new HashMap();
                postData.put("function", "query");
                postData.put("StringPassed", StringPassed);
                excecutingcategory="avancebalance";

                PostResponseAsyncTask loginTask =
                        new PostResponseAsyncTask(payadvance.this, postData,
                                payadvance.this);
                loginTask.execute(getString(R.string.url));

            }
        });



        final List<String> plantsList = new ArrayList<>(Arrays.asList(Paymntsmode));

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter2 = new ArrayAdapter<String>(
                getApplicationContext(),R.layout.spinner_item,plantsList){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinnerArrayAdapter2.setDropDownViewResource(R.layout.spinner_item);
        Spinnerdisbursementmode.setAdapter(spinnerArrayAdapter2);

        Spinnerdisbursementmode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);

                if(position > 0){

                    SelectedModeOfDisbursement=selectedItemText;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }




    public void PayAdvance(View view) {

        if(savingamount.getText().toString().isEmpty()){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Please Provide Payment amount !")
                    .setNegativeButton("Cancel",null).create().show();


        }
        else if(!savingamount.getText().toString().isEmpty()){
            StringRequest stringRequest = new StringRequest(Request.Method.POST,getString(R.string.url), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //  Log.d(TAG,response);
                    System.out.println(response);
                    try {
                        System.out.println(response);
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("status");
                        if (success) {

                            JSONArray jsonMainNode = jsonObject.optJSONArray("data");
                            for (int i = 0; i < jsonMainNode.length(); i++) {
                                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                        Double advancebalances=jsonChildNode.getDouble("advancebalance");
                      int datediff = jsonChildNode.getInt("DateDiff");
                        Double rpaymentperiod=jsonChildNode.getDouble("repaymentitme");

                        Double initial_amount=jsonChildNode.getDouble("initial_amount");



                        Double[] amount_payable= checkloanpayment(rpaymentperiod,advancebalances,initial_amount);
if(datediff<rpaymentperiod){
                        if(amount_payable[2]>amount_payable[0]){
                            if    (Double.valueOf(savingamount.getText().toString())<amount_payable[0]){

                                builder("The lowest you can pay is"+amount_payable[0],"false");
                            }

                        else  if(Double.valueOf(savingamount.getText().toString())>amount_payable[2]){
                            builder("Your outstanding balance is"+amount_payable[2],"false");
                        }
                        else if(Double.valueOf(savingamount.getText().toString()).equals(amount_payable[0])||Double.valueOf(savingamount.getText().toString())>amount_payable[0]){
                            completevalidation();
                        }}
                        else if (Double.valueOf(savingamount.getText().toString()).equals(amount_payable[2])){
                            completevalidation();
                        }
                        else { builder("Your outstanding balance is"+amount_payable[2]+"pay the exact amount","false");}


}
   else{

        if (Double.valueOf(Double.valueOf(savingamount.getText().toString())).equals( amount_payable[2])) {
            completevalidation();
        } else if (Double.valueOf(savingamount.getText().toString()) > amount_payable[2]) {
            builder("You have overpaid ,Outstasting balance is: " + amount_payable[2], "true");
            SpannableString remainingdys = new SpannableString(String.valueOf(amount_payable[2]));
            savingamount.setText(remainingdys);
        } else if (amount_payable[2] > Double.valueOf(savingamount.getText().toString())) {
            SpannableString remainingdys = new SpannableString(String.valueOf(amount_payable[2]));
            savingamount.setText(remainingdys);
            builder("It is the  "+datediff+"rd final month since advance application \n outstanding balance is  " + amount_payable[2] + " \n please clear all remaining balance", "false");
        }


    }
//                            SpannableString remainingdys = new SpannableString(String.valueOf(amount_payable[2]));
//                            lastmonthsaving.setText(remainingdys);

                    } }}catch (JSONException e) {
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


                    String StringPassed = "SELECT ifnull((select amount FROM  \n" +
                            "                            `transactions` WHERE `account`='" + chooseaccounts.getSelectedItem().toString() + "' AND   `memberid`='" + Selectedmember + "' AND \n" +
                            "                            transactiontype='BORROW' AND transactionoption='ADVANCE' ORDER BY ref DESC LIMIT 1 ),0) as initial_amount\n" +
                            ",type_id,ifnull((select repaymentperiod FROM  " +
                            "                            `transactions` WHERE `account`='" + chooseaccounts.getSelectedItem().toString() + "' AND   `memberid`='" + Selectedmember + "' AND " +
                            "                            transactiontype='BORROW' AND transactionoption='ADVANCE' ORDER BY ref DESC LIMIT 1 ),0) as repaymentitme, Ifnull( (sum( if( `transactiontype` = 'BORROW' and `type_id`='123'  AND `account`='" + chooseaccounts.getSelectedItem().toString() + "'AND  `transactionoption`='ADVANCE' and status='Active',(amount), 0 ) )-" +
                            "sum( if(  `transactiontype` = 'PAYMENTS' AND `type_id`='123' and  `transactionoption`='ADVANCE' and `account`='" + chooseaccounts.getSelectedItem().toString() + "' ,(amount), 0 ) ) )  ,0)    AS `advancebalance`," +
                            " Ifnull((select DATEDIFF(DATE_ADD(date, INTERVAL repaymentperiod MONTH),NOW()) FROM  " +
                            " `transactions` WHERE `account`='" + chooseaccounts.getSelectedItem().toString() + "' AND   `memberid`='" + Selectedmember + "' AND " +
                            " transactiontype='BORROW' AND transactionoption='ADVANCES' ORDER BY ref DESC LIMIT 1),0)" +
                            " AS remainingdays,ifnull((SELECT TIMESTAMPDIFF(month, `date`, NOW()) from `transactions` where `account`='" + chooseaccounts.getSelectedItem().toString() + "' AND   `memberid`='" + Selectedmember + "' AND " +
                            "                             transactiontype='BORROW' AND transactionoption='ADVANCE'),0) AS DateDiff  FROM `transactions` WHERE `account`='" + chooseaccounts.getSelectedItem().toString() + "' AND   `memberid`='" + Selectedmember + "' and `status`='Active' " +
                            " ORDER BY ref DESC LIMIT 1 ";


                    params.put("function", "getresult");
                    params.put("sql", StringPassed);


                    return params;
                }
            };
            MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);


        }


    }




    @Override
    public void processFinish(String output) {

        if (excecutingcategory.equals("MemberGroups")) {

            try {


                JSONObject jsonObject = new JSONObject(output);
                boolean success = jsonObject.getBoolean("success");

                if (success) {


                    JSONArray jsonMainNode = jsonObject.optJSONArray("data");
                    for (int i = 0; i < jsonMainNode.length(); i++) {

                        JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                        MemberGroups.add(jsonChildNode.getString("name"));

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "failed getting groups " + e,
                        Toast.LENGTH_LONG).show();
            }

        }else if (excecutingcategory.equals("avancebalance")) {

            try {

                totalsaving.setText("");
                lastmonthsaving.setText("");
                lastmonthsaving.setText("");

                System.out.println(output);
                JSONObject jsonObject = new JSONObject(output);
                boolean success = jsonObject.getBoolean("success");
                if (success) {

                    JSONArray jsonMainNode = jsonObject.optJSONArray("data");
                    for (int i = 0; i < jsonMainNode.length(); i++) {


                            JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                        Double advancebalances=jsonChildNode.getDouble("advancebalance");
                        Double datediff = jsonChildNode.getDouble("DateDiff");
                        Double rpaymentperiod=jsonChildNode.getDouble("repaymentitme");

                        Double initial_amount=jsonChildNode.getDouble("initial_amount");
                        Double[] amount_payable= checkloanpayment(rpaymentperiod,advancebalances,initial_amount);
                        if(amount_payable[2]>0) {






                            SpannableString content = new SpannableString("KES " + new DecimalFormat("#,###.##").
                                    format(amount_payable[2]));
                            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                            totalsaving.setText(content);


                            SpannableString remainingdys = new SpannableString(jsonChildNode.getString("remainingdays") + " Days ");
                            lastmonthsaving.setText(remainingdys);
                            advanceid.setText(jsonChildNode.getString("type_id"));
                        }
                        else{

                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setMessage("You have no balances !")
                                    .setNegativeButton("Cancel",null).create().show();
                        }
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "failed getting member's Balance " + e,
                        Toast.LENGTH_LONG).show();
            }

        }




        else if (excecutingcategory.equals("savepayadvance")) {

            try {
                System.out.println(output);

                JSONObject jsonObject = new JSONObject(output);
                boolean success = jsonObject.getBoolean("success");
                if (success) {


                    StringPassed ="SELECT ifnull((select amount FROM  \n" +
                            "                            `transactions` WHERE `account`='"+chooseaccounts.getSelectedItem().toString()+"' AND   `memberid`='"+Selectedmember+"' AND \n" +
                            "                            transactiontype='BORROW' AND transactionoption='ADVANCE' ORDER BY ref DESC LIMIT 1 ),0) as initial_amount\n" +
                            ",type_id,ifnull((select repaymentperiod FROM  " +
                            "                            `transactions` WHERE `account`='"+chooseaccounts.getSelectedItem().toString()+"' AND   `memberid`='"+Selectedmember+"' AND " +
                            "                            transactiontype='BORROW' AND transactionoption='ADVANCE' ORDER BY ref DESC LIMIT 1 ),0) as repaymentitme, Ifnull( (sum( if( `transactiontype` = 'BORROW' and `type_id`='123'  AND `account`='"+chooseaccounts.getSelectedItem().toString()+"'AND  `transactionoption`='ADVANCE' and status='Active',(amount), 0 ) )-" +
                            "sum( if(  `transactiontype` = 'PAYMENTS' AND `type_id`='123' and  `transactionoption`='ADVANCE' and `account`='"+chooseaccounts.getSelectedItem().toString()+"' ,(amount), 0 ) ) )  ,0)    AS `advancebalance`," +
                            " Ifnull((select DATEDIFF(DATE_ADD(date, INTERVAL repaymentperiod MONTH),NOW()) FROM  " +
                            " `transactions` WHERE `account`='"+chooseaccounts.getSelectedItem().toString()+"' AND   `memberid`='"+Selectedmember+"' AND " +
                            " transactiontype='BORROW' AND transactionoption='ADVANCES' ORDER BY ref DESC LIMIT 1),0)" +
                            " AS remainingdays,ifnull((SELECT TIMESTAMPDIFF(month, `date`, NOW()) from `transactions` where `account`='"+chooseaccounts.getSelectedItem().toString()+"' AND   `memberid`='"+Selectedmember+"' AND " +
                            "                             transactiontype='BORROW' AND transactionoption='ADVANCE'),0) AS DateDiff  FROM `transactions` WHERE `account`='"+chooseaccounts.getSelectedItem().toString()+"' AND   `memberid`='"+Selectedmember+"' and `status`='Active' " +
                            " ORDER BY ref DESC LIMIT 1 " ;


                    HashMap postData = new HashMap();
                    postData.put("function", "query");
                    postData.put("StringPassed", StringPassed);
                    excecutingcategory="avancebalance";

                    savingamount.setText("");

                    PostResponseAsyncTask loginTask =
                            new PostResponseAsyncTask(payadvance.this, postData,
                                    payadvance.this);
                    loginTask.execute(getString(R.string.url));

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Transaction saved successfully !")
                            .setNegativeButton("Cancel",null).create().show();

                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Operation canceled due to " + e,
                        Toast.LENGTH_LONG).show();
            }

        }

    }




public String adddates(int duration){
        Date date = new Date();
        SimpleDateFormat df  = new SimpleDateFormat("YYYY-MM-dd");
        Calendar c1 = Calendar.getInstance();
        String currentDate = df.format(date);// get current date here
        //   Toast.makeText(this, currentDate, Toast.LENGTH_SHORT).show();

        // now add 30 day in Calendar instance
        c1.add(Calendar.MONTH, duration);
        df = new SimpleDateFormat("yyyy-MM-dd");
        Date resultDate = c1.getTime();
        String     dueDate = df.format(resultDate);
        Toast.makeText(this, dueDate, Toast.LENGTH_LONG).show();
        return dueDate;
    }

    public void accountselectspinner(){
        adapter= ArrayAdapter.createFromResource(this,R.array.accaounts,android.R.layout.simple_spinner_item); adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chooseaccounts.setAdapter(adapter);
        chooseaccounts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                if (!parent.getItemAtPosition(position).toString().equalsIgnoreCase("Please select accounts..............")) {
                    savings();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    public void backpress(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
            }
        });
    }

    public Double[] checkloanpayment(Double rpaymentperiod,Double advancebalances,Double initialamount){
Double intrest=0.1*initialamount;


        Double totalintrest=(0.1* initialamount)*rpaymentperiod;

        Double remaining=totalintrest +advancebalances;

        Double[] arr;

        arr = new Double[4];


        arr[0] =intrest;


        arr[1] = totalintrest;
        arr[2] = remaining;


        return arr;

    }

    public void builder(String message, final String  complete){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        })
                .setNegativeButton("Cancel",null).create().show();
    }

    public void completevalidation(){

        if(SelectedModeOfDisbursement.isEmpty()){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Please select mode of Payment !")
                    .setNegativeButton("Cancel",null).create().show();

        }else if(SelectedModeOfDisbursement.equals("CASH")){

            String sql1 = "insert into `transactions`(`memberid`,`date`,`account`,`paymentmode`," +
                    "`transactionnumber`,`transactiontype`,`transactionoption`,`repaymentperiod`,`next_repayment`,`amount`,`type_id`,`status`,`Action`)" +
                    "VALUES('" + Selectedmember + "','" + currentDateandTime + "','"+chooseaccounts.getSelectedItem().toString()+"','" +
                    SelectedModeOfDisbursement + "','','PAYMENTS','ADVANCE','','"+adddates(1)+"','" + savingamount.getText() + "','" + advanceid.getText().toString() + "','Active','Pending')";

            String sql2="update transactions set `action`='paid' where next_repayment<='"+currentDateandTime+"' and `transactiontype`='PAYMENTS' and `transactionoption`='ADVANCE'" ;
            HashMap postData = new HashMap();
            postData.put("function", "transactions");
            postData.put("sql1", sql1);
            postData.put("sql2", sql2);
            excecutingcategory="savepayadvance";

            PostResponseAsyncTask loginTask =
                    new PostResponseAsyncTask(payadvance.this, postData,
                            payadvance.this);
            loginTask.execute(getString(R.string.url));

        }else {

            if(m_Text.isEmpty()) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Transaction Number");

                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = input.getText().toString();

                        StringPassed = "insert into `transactions`(`memberid`,`date`,`account`,`paymentmode`," +
                                "`transactionnumber`,`transactiontype`,``transactionoption`,repaymentperiod`,`next_repayment`,`amount`,`type_id`,`status`,`Action`)" +
                                "VALUES('" + Selectedmember + "','" + currentDateandTime + "','"+chooseaccounts.getSelectedItem().toString()+"','" +
                                SelectedModeOfDisbursement + "','" + m_Text + "','PAYMENTS','ADVANCE','','"+adddates(1)+"','" + savingamount.getText() + "','" + advanceid.getText().toString() + "','Active','pending')";


                        String sql2="update transactions set `action`='paid' where next_repayment<='"+currentDateandTime+"' and `transactiontype`='PAYMENTS' and `transactionoption`='ADVANCE' AND `account`='"+chooseaccounts.getSelectedItem().toString()+"' ";
                        HashMap postData = new HashMap();
                        postData.put("function", "transactions");
                        postData.put("sql1", StringPassed);
                        postData.put("sql2", sql2);
                        excecutingcategory="savepayadvance";

                        PostResponseAsyncTask loginTask =
                                new PostResponseAsyncTask(payadvance.this, postData,
                                        payadvance.this);
                        loginTask.execute(getString(R.string.url));
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = input.getText().toString();
                        dialog.cancel();
                    }
                });

                builder.show();

            }else{


                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MONTH, 1);



                StringPassed = "insert into `transactions`(`memberid`,`date`,`account`,`paymentmode`," +
                        "`transactionnumber`,`transactiontype`,`transactionoption`,`repaymentperiod`,`next_repayment`,`amount`,`type_id`,`status`,`Action`)" +
                        "VALUES('" + Selectedmember + "','" + currentDateandTime + "','"+chooseaccounts.getSelectedItem().toString()+"','" +
                        SelectedModeOfDisbursement + "','" + m_Text + "','PAYMENTS','ADVANCE','','"+adddates(1)+"','" + savingamount.getText() + "','" + advanceid.getText().toString() + "','Active','Pending')";
                String sql2="update transactions set `action`='paid' where next_repayment<='"+currentDateandTime+"' and `transactiontype`='PAYMENTS' and `transactionoption`='ADVANCE' and account='"+chooseaccounts.getSelectedItem().toString()+"'" ;
                HashMap postData = new HashMap();
                postData.put("function", "transactions");
                postData.put("sql1", StringPassed);
                postData.put("sql2", sql2);

                excecutingcategory="savepayadvance";

                PostResponseAsyncTask loginTask =
                        new PostResponseAsyncTask(payadvance.this, postData,
                                payadvance.this);
                loginTask.execute(getString(R.string.url));

            }

        }



    }

}
