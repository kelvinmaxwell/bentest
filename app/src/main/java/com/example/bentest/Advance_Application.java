package com.example.bentest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Advance_Application extends AppCompatActivity implements AsyncResponse, AdapterView.OnItemClickListener, View.OnClickListener {


    private JSONArray result;
    private Map<java.lang.String, java.lang.String> params;

    AutoCompleteTextView autoCompleteTextView;
    TextView totalsavings;
    TextView available;
    EditText AmountRequested;

    private static final java.lang.String URL = "http://192.168.43.78/www/html/seskenya/";

    ArrayList<java.lang.String> Members = new ArrayList<>();
    ArrayList<java.lang.String> MemberGroups = new ArrayList<>();
    java.lang.String function = "";
    java.lang.String StringPassed = "";
    ActionRequest driverLoginRequest;
    RequestQueue requestQueue;
    java.lang.String Selectedmember = "";
    java.lang.String m_Text = "";
    java.lang.String SelectedModeOfDisbursement = "";

    private android.widget.Spinner Spinner;
    private android.widget.Spinner Spinnerdisbursementmode;
    private android.widget.Spinner Spinnerrepaymentperiod;

    java.lang.String Paymntsmode[] = {
            "Disbursement Method",
            "CASH",
            "MPESA",
            "CHEQUE"
    };

    java.lang.String[] String = new String[]{
            "Repayment Period","1", "2","3"
    };

    java.lang.String excecutingcategory = "";

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    java.lang.String currentDateandTime = sdf.format(new Date());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advance__application);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AmountRequested= (EditText) findViewById(R.id.AmountRequested);

        Spinner = (android.widget.Spinner) findViewById(R.id.spinner);
        Spinnerdisbursementmode=(android.widget.Spinner) findViewById(R.id.Spinnerdisbursementmode);

        MemberGroups.add("Select Group");

        HashMap postData = new HashMap();
        postData.put("function", "query");
        postData.put("StringPassed", "select name from groups");
        excecutingcategory = "MemberGroups";


        PostResponseAsyncTask loginTask =
                new PostResponseAsyncTask(Advance_Application.this, postData,
                        Advance_Application.this);
        loginTask.execute(URL + "dbqueries.php");




        // Initializing an ArrayAdapter
        final ArrayAdapter<java.lang.String> spinnerArrayAdapter = new ArrayAdapter<java.lang.String>(
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
                java.lang.String selectedItemText = (java.lang.String) parent.getItemAtPosition(position);


                if (position > 0) {
                    autoCompleteTextView.setEnabled(true);

                    StringPassed = "select concat(memberid,' ',firstname,' ',secondname) as name " +
                            " from members inner join groups on groups.id=members.group WHERE `name`='"+selectedItemText+"'  ";
                    Members.clear();



                    Response.Listener<java.lang.String> responseListener1 = new Response.Listener<java.lang.String>() {
                        @Override
                        public void onResponse(java.lang.String response) {
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

                                    ArrayAdapter<java.lang.String> adapter = new ArrayAdapter<java.lang.String>(getApplicationContext(), android.R.layout.simple_list_item_1, Members);
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

            }
        });


        totalsavings = (TextView) findViewById(R.id.TotalSavings);
        available = (TextView) findViewById(R.id.Available);



        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.Membernu);
        autoCompleteTextView.setEnabled(false);
        autoCompleteTextView.setClickable(true);
        autoCompleteTextView.setOnClickListener(this);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Selectedmember = parent.getItemAtPosition(position).toString().split(" ")[0];
//get advancebalance



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



                                    if(jsonChildNode.getInt("advancebalance")>0){

                                        totalsavings.setVisibility(View.INVISIBLE);
                                        available.setVisibility(View.INVISIBLE);
                                           bbulder();



                                    }
                                }

                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(Advance_Application.this);
                                builder.setMessage("Could not load groups").setNegativeButton("Retry", null).create().show();
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


                     String sql="SELECT  Ifnull( (sum( if( `transactiontype` = 'BORROW',(amount), 0 ) )-\n" +
                             "                           sum( if(  `transactiontype` = 'PAYMENTS' ,(amount), 0 ) ) )  ,0)    AS `advancebalance`\n" +
                             "                 FROM \n" +
                             "                            `transactions` WHERE `account`='SAVINGS' AND   `memberid`='"+Selectedmember+"' \n" +
                             "                            \n" +
                             "                            ORDER BY ref DESC LIMIT 1";





                        params.put("function", "getadvancebalances");
                        params.put("sql",sql);



                        return params;
                    }
                };
                MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);




                StringPassed = "SELECT IF((select count(memberid) from loans where memberid='"+Selectedmember+"' and " +
                        "transactionoption='BORROW' AND status='Active') >0,(select COUNT(ref) from loans where " +
                        "transactionoption='PAYMENT' AND status='Active'),-1 ) AS `no` FROM loans   GROUP BY memberid" ;

                HashMap postData = new HashMap();
                postData.put("function", "query");
                postData.put("StringPassed", StringPassed);
                excecutingcategory="checkqualification";

                PostResponseAsyncTask loginTask =
                        new PostResponseAsyncTask(Advance_Application.this, postData,
                                Advance_Application.this);
                loginTask.execute(URL + "dbqueries.php");




            }
        });


// guarantor autocomplete textfield------------------------------------------------------------









        final List<java.lang.String> plantsList = new ArrayList<>(Arrays.asList(Paymntsmode));

        // Initializing an ArrayAdapter
        final ArrayAdapter<java.lang.String> spinnerArrayAdapter2 = new ArrayAdapter<java.lang.String>(
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
                java.lang.String selectedItemText = (java.lang.String) parent.getItemAtPosition(position);

                if(position > 0){

                    SelectedModeOfDisbursement=selectedItemText;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        Spinnerrepaymentperiod=(android.widget.Spinner) findViewById(R.id.Spinnerrepaymentperiod);

        List<java.lang.String> plantsList3 = new ArrayList<>(Arrays.asList(String));

        final ArrayAdapter<java.lang.String> spinnerArrayAdapter3 = new ArrayAdapter<java.lang.String>(
                getApplicationContext(),R.layout.spinner_item,plantsList3){
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
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        Spinnerrepaymentperiod.setAdapter(spinnerArrayAdapter3);

        Spinnerrepaymentperiod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position > 0){
                    repaymentperiod = (java.lang.String) parent.getItemAtPosition(position);


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });





    }


    java.lang.String repaymentperiod="";





    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


    }

    @Override
    public void onClick(View view) {

    }


    public void backtomainpage(View view) {
        finish();
    }



    @RequiresApi(api = Build.VERSION_CODES.O)

    public void SaveSavings(View view) {

        if(AmountRequested.getText().toString().isEmpty()){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Please Provide amount requested !")
                    .setNegativeButton("Cancel",null).create().show();

        }else if(SelectedModeOfDisbursement.isEmpty()){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Please select mode of disbursement !")
                    .setNegativeButton("Cancel",null).create().show();

        }else if(repaymentperiod.isEmpty()){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Please select payment period !")
                    .setNegativeButton("Cancel",null).create().show();

        }else if(SelectedModeOfDisbursement.equals("CASH")){

            StringPassed = "insert into `transactions`(`memberid`,`date`,`account`,`paymentmode`," +
                    "`transactionnumber`,`transactiontype`,`transactionoption`,`repaymentperiod`,`amount`)" +
                    "VALUES('"  +Selectedmember+ "','" + currentDateandTime + "','SAVINGS','" +
                    SelectedModeOfDisbursement + "','','BORROW','ADVANCE','"+repaymentperiod+"','" + AmountRequested.getText() + "')";

            HashMap postData = new HashMap();
            postData.put("function", "action");
            postData.put("StringPassed", StringPassed);
            excecutingcategory="saveadvace";

            PostResponseAsyncTask loginTask =
                    new PostResponseAsyncTask(Advance_Application.this, postData,
                            Advance_Application.this);
            loginTask.execute(URL + "dbqueries.php");

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

                        StringPassed = "insert into `advances`(`memberid`,`date`,`account`,`paymentmode`," +
                                "`transactionnumber`,`transactiontype`,`repaymentperiod`,`amount`)" +
                                "VALUES('" + Selectedmember + "','" + currentDateandTime + "','SAVINGS','" +
                                SelectedModeOfDisbursement + "','" + m_Text + "','BORROW','"+repaymentperiod+"','" + AmountRequested.getText() + "')";

                        HashMap postData = new HashMap();
                        postData.put("function", "action");
                        postData.put("StringPassed", StringPassed);
                        excecutingcategory="saveadvace";

                        PostResponseAsyncTask loginTask =
                                new PostResponseAsyncTask(Advance_Application.this, postData,
                                        Advance_Application.this);
                        loginTask.execute(URL + "dbqueries.php");
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

                StringPassed = "insert into `advances`(`memberid`,`date`,`account`,`paymentmode`," +
                        "`transactionnumber`,`transactiontype`,`repaymentperiod`,`amount`)" +
                        "VALUES('" + Selectedmember + "','" + currentDateandTime + "','SAVINGS','" +
                        SelectedModeOfDisbursement + "','" + m_Text + "','BORROW','"+repaymentperiod+"','" + AmountRequested.getText() + "')";

                HashMap postData = new HashMap();
                postData.put("function", "action");
                postData.put("StringPassed", StringPassed);
                excecutingcategory="saveadvace";

                PostResponseAsyncTask loginTask =
                        new PostResponseAsyncTask(Advance_Application.this, postData,
                                Advance_Application.this);
                loginTask.execute(URL + "dbqueries.php");

            }

        }


    }


    @Override
    public void processFinish(java.lang.String output) {


        if (excecutingcategory.equals("MemberGroups")) {

            try {

                System.out.println(output);

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

        } else if (excecutingcategory.equals("checkqualification")) {

            try {

                totalsavings.setText("");
                available.setText("");

                System.out.println(output);
                JSONObject jsonObject = new JSONObject(output);
                boolean success = jsonObject.getBoolean("success");
                if (success) {

                    JSONArray jsonMainNode = jsonObject.optJSONArray("data");
                    for (int i = 0; i < jsonMainNode.length(); i++) {

                        JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                        int content = jsonChildNode.getInt("no");

                        if(content>=0&&content<4){

                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setMessage("Your not qualified for an advance because you have an existing loan. Please make payments for " +
                                    "atleast FOUR months !");

                            builder.setNegativeButton("Cancel", new
                                    DialogInterface.OnClickListener()
                                    {
                                        public void onClick(DialogInterface dialog, int id)
                                        {

                                            finish();

                                        }
                                    });
                            builder.setCancelable(false);
                            builder.show();


                        }else{


                            StringPassed = "SELECT (SELECT IFNULL((SUM(IF(transactions.`transactiontype` = 'SAVINGS',(transactions.amount), 0))),0)" +
                                    "FROM transactions WHERE transactions.`account`='SAVINGS' AND memberid='"+Selectedmember+"') AS `totalsavings`, " +
                                    "IFNULL(((SELECT (SUM(IF(transactions.`transactiontype` = 'SAVINGS',(transactions.amount), 0)))" +
                                    "FROM transactions WHERE transactions.`account`='SAVINGS' AND memberid='"+Selectedmember+"') *2)- " +
                                    " ABS((SELECT IFNULL((SUM(IF(`transactiontype` = 'PAYMENTS',(amount), 0)))-" +
                                    "(SUM(IF(`transactiontype` = 'BORROW',(amount), 0))),0) FROM transactions WHERE `account`='SAVINGS' " +
                                    " AND memberid='"+Selectedmember+"')),0) AS availableforfortakingadvance" ;

                            HashMap postData = new HashMap();
                            postData.put("function", "query");
                            postData.put("StringPassed", StringPassed);
                            excecutingcategory="Membersavings";

                            PostResponseAsyncTask loginTask =
                                    new PostResponseAsyncTask(Advance_Application.this, postData,
                                            Advance_Application.this);
                            loginTask.execute(URL + "dbqueries.php");




                        }

                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "failed getting member's Balance " + e,
                        Toast.LENGTH_LONG).show();
            }

        }


        else if (excecutingcategory.equals("Membersavings")) {

            try {

                totalsavings.setText("");
                available.setText("");

                System.out.println(output);
                JSONObject jsonObject = new JSONObject(output);
                boolean success = jsonObject.getBoolean("success");
                if (success) {

                    JSONArray jsonMainNode = jsonObject.optJSONArray("data");
                    for (int i = 0; i < jsonMainNode.length(); i++) {

                        JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                        SpannableString content = new SpannableString("KES "+new DecimalFormat("#,###.##").
                                format(jsonChildNode.getDouble("totalsavings")));
                        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                        totalsavings.setText(content);

                        SpannableString contenavailable = new SpannableString("KES "+new DecimalFormat("#,###.##").
                                format(jsonChildNode.getDouble("availableforfortakingadvance")));


                        available.setText(contenavailable);
                       int maxamountrequested=jsonChildNode.getInt("availableforfortakingadvance");

                        AmountRequested.setFilters(new InputFilter[]{ new MinMaxFilter("1",Integer.toString(maxamountrequested) )});

                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "failed getting member's Balance " + e,
                        Toast.LENGTH_LONG).show();
            }

        }


        else if (excecutingcategory.equals("saveadvace")) {

            try {

                System.out.println(output);
                JSONObject jsonObject = new JSONObject(output);
                boolean success = jsonObject.getBoolean("success");
                if (success) {

                    StringPassed = "SELECT (SELECT IFNULL((SUM(IF(savings.`transactiontype` = 'SAVINGS',(savings.amount), 0))),0)" +
                            "FROM savings WHERE savings.`account`='SAVINGS' AND memberid='"+Selectedmember+"') AS `totalsavings`, " +
                            "IFNULL(((SELECT (SUM(IF(savings.`transactiontype` = 'SAVINGS',(savings.amount), 0)))" +
                            "FROM savings WHERE savings.`account`='SAVINGS' AND memberid='"+Selectedmember+"') *2)- " +
                            " ABS((SELECT IFNULL((SUM(IF(`transactiontype` = 'PAYMENTS',(amount), 0)))-" +
                            "(SUM(IF(`transactiontype` = 'BORROW',(amount), 0))),0) FROM advances WHERE `account`='SAVINGS' " +
                            " AND memberid='"+Selectedmember+"')),0) AS availableforfortakingadvance" ;

                    HashMap postData = new HashMap();
                    postData.put("function", "query");
                    postData.put("StringPassed", StringPassed);
                    excecutingcategory="Membersavings";


                    AmountRequested.setText("");
                    repaymentperiod="";


                    PostResponseAsyncTask loginTask =
                            new PostResponseAsyncTask(Advance_Application.this, postData,
                                    Advance_Application.this);
                    loginTask.execute(URL + "dbqueries.php");

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Saving saved successfully !")
                            .setNegativeButton("Cancel",null).create().show();

                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Operation canceled due to " + e,
                        Toast.LENGTH_LONG).show();
            }

        }


    }

    public  void bbulder(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Advance_Application.this);
        builder.setMessage("Member not eligible to an Advance du to an outstanding advance balance!").setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Advance_Application.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }).create().show();

    }





}