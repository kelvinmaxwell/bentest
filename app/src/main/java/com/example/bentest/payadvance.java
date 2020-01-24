package com.example.bentest;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.PostResponseAsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class payadvance extends AppCompatActivity implements AsyncResponse, AdapterView.OnItemClickListener, View.OnClickListener {


    private static final String URL = "http://192.168.43.78/www/html/seskenya/";
    String Paymntsmode[] = {
            "Disbursement Method",
            "CASH",
            "MPESA",
            "CHEQUE"
    };


    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String currentDateandTime = sdf.format(new Date());

    TextView totalsaving;
    TextView lastmonthsaving;
    ArrayList<String> Members = new ArrayList<>();

    EditText savingamount;
    AutoCompleteTextView autoCompleteTextView;
    String Selectedmember = "";
    String StringPassed = "";
    String function = "";
    String excecutingcategory = "";
    private android.widget.Spinner Spinner;
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


        Spinner = (android.widget.Spinner) findViewById(R.id.spinner);
        Spinnerdisbursementmode=(android.widget.Spinner) findViewById(R.id.Spinnerdisbursementmode);

        MemberGroups.add("Select Group");

        HashMap postData = new HashMap();
        postData.put("function", "query");
        postData.put("StringPassed", "select name from groups");
        excecutingcategory = "MemberGroups";


        PostResponseAsyncTask loginTask =
                new PostResponseAsyncTask(payadvance.this, postData,
                        payadvance.this);
        loginTask.execute(URL + "dboperations.php");




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
                    driverLoginRequest = new ActionRequest("dboperations.php", function, StringPassed, responseListener1);
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

        savingamount= (EditText) findViewById(R.id.savingamount);

        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.Membernu);
        autoCompleteTextView.setEnabled(false);
        autoCompleteTextView.setClickable(true);
        autoCompleteTextView.setOnClickListener(this);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Selectedmember = parent.getItemAtPosition(position).toString().split(" ")[0];

                StringPassed = "SELECT  Ifnull( (sum( if( `transactiontype` = 'BORROW',(amount), 0 ) )-" +
                        "sum( if(  `transactiontype` = 'PAYMENTS' ,(amount), 0 ) ) )  ,0)    AS `advancebalance`," +
                        " Ifnull((select DATEDIFF(DATE_ADD(date, INTERVAL repaymentperiod MONTH),NOW()) FROM  " +
                        " `advances` WHERE `account`='SAVINGS' AND   `memberid`='"+Selectedmember+"' AND " +
                        " transactiontype='BORROW' ORDER BY ref DESC LIMIT 1),0)" +
                        " AS remainingdays FROM `advances` WHERE `account`='SAVINGS' AND   `memberid`='"+Selectedmember+"' " +
                        " ORDER BY ref DESC LIMIT 1 " ;

                HashMap postData = new HashMap();
                postData.put("function", "query");
                postData.put("StringPassed", StringPassed);
                excecutingcategory="avancebalance";

                PostResponseAsyncTask loginTask =
                        new PostResponseAsyncTask(payadvance.this, postData,
                                payadvance.this);
                loginTask.execute(URL + "dboperations.php");

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

        }else if(SelectedModeOfDisbursement.isEmpty()){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Please select mode of Payment !")
                    .setNegativeButton("Cancel",null).create().show();

        }else if(SelectedModeOfDisbursement.equals("CASH")){

            StringPassed = "insert into `advances`(`memberid`,`date`,`account`,`paymentmode`," +
                    "`transactionnumber`,`transactiontype`,`repaymentperiod`,`amount`)" +
                    "VALUES('" + Selectedmember + "','" + currentDateandTime + "','SAVINGS','" +
                    SelectedModeOfDisbursement + "','','PAYMENTS','','" + savingamount.getText() + "')";

            HashMap postData = new HashMap();
            postData.put("function", "action");
            postData.put("StringPassed", StringPassed);
            excecutingcategory="savepayadvance";

            PostResponseAsyncTask loginTask =
                    new PostResponseAsyncTask(payadvance.this, postData,
                            payadvance.this);
            loginTask.execute(URL + "dboperations.php");

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
                                SelectedModeOfDisbursement + "','" + m_Text + "','PAYMENTS','','" + savingamount.getText() + "')";


                        HashMap postData = new HashMap();
                        postData.put("function", "action");
                        postData.put("StringPassed", StringPassed);
                        excecutingcategory="savepayadvance";

                        PostResponseAsyncTask loginTask =
                                new PostResponseAsyncTask(payadvance.this, postData,
                                        payadvance.this);
                        loginTask.execute(URL + "dboperations.php");
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
                        SelectedModeOfDisbursement + "','" + m_Text + "','PAYMENTS','','" + savingamount.getText() + "')";


                HashMap postData = new HashMap();
                postData.put("function", "action");
                postData.put("StringPassed", StringPassed);
                excecutingcategory="savepayadvance";

                PostResponseAsyncTask loginTask =
                        new PostResponseAsyncTask(payadvance.this, postData,
                                payadvance.this);
                loginTask.execute(URL + "dboperations.php");

            }

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

                        SpannableString content = new SpannableString("KES "+new DecimalFormat("#,###.##").
                                format(jsonChildNode.getDouble("advancebalance")));
                        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                        totalsaving.setText(content);

                        SpannableString remainingdys = new SpannableString(jsonChildNode.getString("remainingdays")+" Days ");
                        lastmonthsaving.setText(remainingdys);

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


                JSONObject jsonObject = new JSONObject(output);
                boolean success = jsonObject.getBoolean("success");
                if (success) {


                    StringPassed = "SELECT  Ifnull( (sum( if( `transactiontype` = 'BORROW',(amount), 0 ) )-" +
                            "sum( if(  `transactiontype` = 'PAYMENTS' ,(amount), 0 ) ) )  ,0)    AS `advancebalance`," +
                            " Ifnull((select DATEDIFF(DATE_ADD(date, INTERVAL repaymentperiod MONTH),NOW()) FROM  " +
                            " `advances` WHERE `account`='SAVINGS' AND   `memberid`='"+Selectedmember+"' AND " +
                            " transactiontype='BORROW' ORDER BY ref DESC LIMIT 1),0)" +
                            " AS remainingdays FROM `advances` WHERE `account`='SAVINGS' AND   `memberid`='"+Selectedmember+"' " +
                            " ORDER BY ref DESC LIMIT 1 " ;

                    HashMap postData = new HashMap();
                    postData.put("function", "query");
                    postData.put("StringPassed", StringPassed);
                    excecutingcategory="avancebalance";

                    savingamount.setText("");

                    PostResponseAsyncTask loginTask =
                            new PostResponseAsyncTask(payadvance.this, postData,
                                    payadvance.this);
                    loginTask.execute(URL + "dboperations.php");

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
}
