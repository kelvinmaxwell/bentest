package com.example.bentest;

import android.app.AlertDialog;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static com.example.bentest.Tab1Fragment.groupid;
import static com.example.bentest.Tab1Fragment.memidno;
import static com.example.bentest.Tab1Fragment.Selectedmember;

import static com.example.bentest.Tab1Fragment.account;


import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
//import com.example.ses.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by User on 2/28/2017.
 */

public class Tab2Fragment extends Fragment {
    private static final String TAG = "Tab2Fragment";

    static AutoCompleteTextView autoCompleteTextView;
    static   String selectedmembergarontor;
    static TextView balance;
    static TextView idno;
    static TextView phone;
    static String guarontorid = "";
    String StringPassed = "";
    ArrayList<String> Members = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.tab2_fragment,container,false);

        autoCompleteTextView = (AutoCompleteTextView)  view.findViewById(R.id.Membernu);
        balance = (TextView) view.findViewById(R.id.tvDisplay);
        idno = (TextView) view.findViewById(R.id.idnu);
        phone = (TextView) view.findViewById(R.id.mobileno);

            autoCompleteTextView.setEnabled(true);
         //   Members.clear();

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
                                guarontorid = jsonChildNode.getString("memberid");


                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, Members);
                            autoCompleteTextView.setAdapter(adapter);


                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "failed getting members " + e,
                                Toast.LENGTH_LONG).show();
                    }
                }
            };
            String function = "query";
            String sql = "select `memberid`, concat(memberid,' ',firstname,' ',secondname,' ',surname) as name from members where `group` = '"+groupid+"' and `memberid` != '"+memidno+"'";
            System.out.println("members"+sql);
            ActionRequest driverLoginRequest = new ActionRequest("dbqueries.php", function, sql, responseListener1);
            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(driverLoginRequest);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                selectedmembergarontor= parent.getItemAtPosition(position).toString().split(" ")[0];
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
                                  //  Members.add(jsonChildNode.getString("totalsavings"));
                                    balance.setText("Available: KES "+
                                            new DecimalFormat("#,###.##").
                                                    format(jsonChildNode.getDouble("totalsavings")));
                                    idno.setText(jsonChildNode.getString("memberid"));
                                    phone.setText(jsonChildNode.getString("phone"));
                                    Toast.makeText(getContext(),"maxi"+ guarontorid, Toast.LENGTH_SHORT).show();
                                }

                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, Members);
                                autoCompleteTextView.setAdapter(adapter);


                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "failed getting members " + e,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                };
                String function = "query";
             String   Selectedmember = parent.getItemAtPosition(position).toString().split(" ")[0];
                String sql = "SELECT `memberid`,phone,CONCAT(firstname,' ',secondname,' '," +
                        "surname) as NAME ,ifnull((select sum(`amount`) from `transactions` where `transactionoption`='"+Selectedmember+"' "
                        +
                        " AND `memberid`='"+Selectedmember+"'),0) as `totalsavings`" +
                        " from members  where `memberid` = '"+Selectedmember+"'";
                System.out.println("maxi2"+sql);
                ActionRequest driverLoginRequest = new ActionRequest("dbqueries.php", function, sql, responseListener1);
                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                requestQueue.add(driverLoginRequest);
            }
        });






        return view;
    }

}



