package com.example.bentest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
//import com.example.ses.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.example.bentest.LoanApplicationForm.memid;



/**
 * Created by User on 2/28/2017.
 */

public class Tab1Fragment extends Fragment {
    private static final String TAG = "Tab1Fragment";
    static String groupid = "";
    static String memidno = "";
    static String account = "";
    static String Selectedmember = "";
    static EditText etamount;
    static Spinner Spinner;
    static Spinner Spinner2;
    static Spinner Spinner3;
    static AutoCompleteTextView autoCompleteTextView;

    static TextView textView,loanid;
    String StringPassed = "";
    ViewPager viewPager;
    ArrayList<String> Members = new ArrayList<>();
//textView11

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab1_fragment, container, false);

        Button btnTEST = (Button) view.findViewById(R.id.btnTEST);

        viewPager = (ViewPager) getActivity().findViewById(R.id.container);
        autoCompleteTextView =  (AutoCompleteTextView)view. findViewById(R.id.editText);

        btnTEST.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent myIntent = new Intent(getActivity(), MainActivity.class);
                getActivity().startActivity(myIntent);

            }
        });
loanid=view.findViewById(R.id.loanid);
        Spinner = (Spinner) view.findViewById(R.id.Spinner);
        etamount = (EditText) view.findViewById(R.id.editText2);
        if(Spinner.getAdapter()==null){
            getData();

        }
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Selectedmember = parent.getItemAtPosition(position).toString().split(" ")[0];
                Response.Listener<String> responseListener1 = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            System.out.println(response);
                            JSONObject jsonObject = new JSONObject(response);

                            boolean success = jsonObject.getBoolean("success");

                            if (success ) {

                                JSONArray jsonMainNode = jsonObject.optJSONArray("data");
                                for (int i = 0; i < jsonMainNode.length(); i++) {

                                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                                    if(jsonChildNode.getInt("advance")>0){

                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                        builder.setMessage("Member not eligible to a loan due to an outstanding advance balance!").setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(getContext(), MainActivity.class);
                                                startActivity(intent);

                                            }
                                        }).create().show();

                                         etamount.setText("");
                                        autoCompleteTextView.setText("");
                                        etamount.setText("");


                                    }

                                }

                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, Members);
                                autoCompleteTextView.setAdapter(adapter);


                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                           System.out.println(e);
                        }
                    }
                };

                memid = autoCompleteTextView.getText().toString().replaceAll("[^\\d]", "");
                String function = "query";
                String sql = "SELECT ifnull(sum( if( `transactionoption` = 'ADVANCE' AND `transactiontype` = 'BORROW' ,(amount), 0 )" +
                        " - if(  `transactionoption` = 'ADVANCE' AND `transactiontype` = 'PAYMENTS' ,(amount), 0 ) ),0) AS `advance` " +
                        " FROM `transactions` WHERE  `memberid` = '"+autoCompleteTextView.getText().toString().replaceAll("[^\\d]", "") +"'";
                System.out.println(sql);
                ActionRequest driverLoginRequest = new ActionRequest("dbqueries.php", function, sql, responseListener1);
                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                requestQueue.add(driverLoginRequest);
            }
        });
        Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText =  Spinner.getItemAtPosition(position).toString();

                if (position > 0) {
                    autoCompleteTextView.setEnabled(true);
                    StringPassed = selectedItemText;

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
                                        Members.add(jsonChildNode.getString("memberid")+" "+jsonChildNode.getString("name"));
                                        memidno = jsonChildNode.getString("memberid");

                                    }

                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, Members);
                                    autoCompleteTextView.setAdapter(adapter);


                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                System.out.println(e);
                            }
                        }
                    };
                    groupid = selectedItemText.replaceAll("[^\\d]", "");
                    String function = "query";
                    String sql = "select memberid, concat(firstname,' ',secondname,' ',surname) as name from members where `group` = '"+selectedItemText.replaceAll("[^\\d]", "") +"'";
                    System.out.println(sql);
                    ActionRequest driverLoginRequest = new ActionRequest("dbqueries.php", function, sql, responseListener1);
                    RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                    requestQueue.add(driverLoginRequest);
                }
                else{
                    autoCompleteTextView.setEnabled(false);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner2 = (Spinner) view.findViewById(R.id.Spinner2);
        String[] String = new String[]{
                "Select Account",
//                "SAVINGS",
                "EDUCATION"
        };

        List<String> plantsList2 = new ArrayList<>(Arrays.asList(String));

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter2 = new ArrayAdapter<String>(
                getActivity(), R.layout.spinner_item, plantsList2) {
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
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        Spinner2.setAdapter(spinnerArrayAdapter2);

        Spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                account = Spinner2.getItemAtPosition(position).toString();
if(position>0){

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
                                        Double amountavailable=jsonChildNode.getDouble("totalsavings")*3-jsonChildNode.getDouble("totalloans");
                                        loanid.setText(jsonChildNode.getString("loan_id"));
                                        Members.add(jsonChildNode.getString("totalsavings"));


                                        Toast.makeText(getContext(), Double.toString(amountavailable), Toast.LENGTH_SHORT).show();
                                        if(amountavailable>0) {
                                            textView.setText("Available: KES " +
                                                    new DecimalFormat("#,###.##").
                                                            format(amountavailable));


                                        }
                                        else if(amountavailable<=0 ){
                                            bbulder("You are not qualified please make savings!");
                                        }
                                      int k= (int)Math.round(amountavailable);
                                        etamount.setFilters(new InputFilter[]{ new MinMaxFilter("1", java.lang.String.valueOf(k))});



                                    }


                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, Members);
                                    autoCompleteTextView.setAdapter(adapter);


                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                               System.out.println(e);
                            }
                        }
                    };

                    memid = autoCompleteTextView.getText().toString().replaceAll("[^\\d]", "");

                    String function = "query";
                //    String sql = "SELECT ifnull(sum( if(  `transactiontype` = 'SAVINGS',(amount), 0 ) ),0) AS `totalsavings` FROM `transactions` WHERE `transactiontype`='"+Spinner2.getItemAtPosition(position).toString()+"' and `memberid`='"+autoCompleteTextView.getText().toString().replaceAll("[^\\d]", "") +"'";

              String sql=  "SELECT ifnull((select sum(amount) from `transactions` where `transactionoption`='SAVINGS' and `transactiontype`='SAVINGS' AND `memberid`='"+memid+"' and `account`='"+Spinner2.getSelectedItem().toString()+"'order by ref DESC LIMIT 1 ),0) " +
                      "as totalsavings,ifnull((select sum(`amount`) from loans WHERE " +
                      "`transactionoption`='Borrow' and `transactiontype`='Loan' and `memberid`='"+autoCompleteTextView.getText().toString().replaceAll("[^\\d]", "") +"' and `status`='waiting'),0) as totalloans,IF((select `loanid` " +
                      "from `loans` where `transactiontype`='Loan' AND `transactionoption`='Borrow' and " +
                      "`status`='waiting' and `memberid`='"+memid+"' ORDER by `ref` ASC LIMIT 1) is not null,(select `loanid` from `loans`   where `transactiontype`='Loan' AND `transactionoption`='Borrow' and `status`='waiting' and `memberid`='"+memid+"' ORDER by ref ASC LIMIT 1)," +
                      "ifnull( (select CONCAT(`ref`+1,'/', YEAR(CURDATE()),'/','1','/','LN') " +
                      "from `loans`   where `transactiontype`='Loan' AND `transactionoption`='Borrow' ORDER by `ref` DESC LIMIT 1), (SELECT CONCAT(sum(0+1),'/', YEAR(CURDATE()),'/','1','/','LN'))))as " +
                      "loan_id ";
                    System.out.println(sql);
                    ActionRequest driverLoginRequest = new ActionRequest("dbqueries.php", function, sql, responseListener1);
                    RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                    requestQueue.add(driverLoginRequest);

            }}
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        Spinner3 = (Spinner) view.findViewById(R.id.Spinner3);
        String = new String[]{"Repayment Period (Years)", "1", "2", "3"
        };

        List<String> plantsList3 = new ArrayList<>(Arrays.asList(String));

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter3 = new ArrayAdapter<String>(
                getActivity(), R.layout.spinner_item, plantsList3) {
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
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        Spinner3.setAdapter(spinnerArrayAdapter3);
        Spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);

                if (position > 0) {


                    //Toast.makeText(parent.getContext(), "Selected: " +selectedItemText, Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        textView = (TextView) view.findViewById(R.id.textView11);


        return view;
    }

    private HashMap<String, String> createOwner(String name, String number) {
        HashMap<String, String> employeeNameNo = new HashMap<String, String>();
        employeeNameNo.put(name, number);
        return employeeNameNo;
    }

    public void getData() {
        final List<Map<String, String>> ownersList = new ArrayList<Map<String, String>>(

        );

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    System.err.println(jsonObject.getString("data"));
                    if (success) {
                        //set this to the spinner
                        ownersList.add(createOwner("owners", "Select Group"));
                        JSONArray jsonMainNode = jsonObject.optJSONArray("data");
                        for (int i = 0; i < jsonMainNode.length(); i++) {
                            JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                            String name = jsonChildNode.getString("name");
                            String id = jsonChildNode.getString("id");
                            String location = jsonChildNode.getString("location");
                            String outPut = id +"-"+ name + " (" + location+" )";
                            ownersList.add(createOwner("owners", outPut));
                        }

                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("Could not load groups").setNegativeButton("Retry", null).create().show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                SimpleAdapter simpleAdapter = new SimpleAdapter(getContext(), ownersList,
                        android.R.layout.simple_list_item_1,
                        new String[]{"owners"}, new int[]{android.R.id.text1});
                Spinner.setAdapter(simpleAdapter);


            }
        };

        String sql = "select id, name,  location from seskenya.groups";
        ActionRequest recordOffence = new ActionRequest("dbqueries.php", "query", sql, responseListener);
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(recordOffence);
    }
    public  void bbulder(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(message).setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

getActivity().finish();

            }
        }).create().show();

    }

}
