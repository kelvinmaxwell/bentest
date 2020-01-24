package com.example.bentest;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
//import com.example.ses.R;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import static com.example.bentest.LoanApplicationForm.memid;
import static com.example.bentest.Tab1Fragment.autoCompleteTextView;
import static com.example.bentest.Tab1Fragment.etamount;
import static com.example.bentest.Tab2Fragment.guarontorid;


public class Tab3Fragment extends Fragment {
    private static final String TAG = "Tab3Fragment";
    EditText col1,col2,col3,val1,val2,val3;
    Button process;
    ViewPager viewPager;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab3_fragment,container,false);

        viewPager = (ViewPager) getActivity().findViewById(R.id.container);



        TextInputLayout phoneLayout = (TextInputLayout) view.findViewById(R.id
                .phoneLayout);
        // phoneLayout.setError("Please enter a phone number");
        //Displaying EditText Error
        process = (Button)view.findViewById(R.id.processbutton);
        col1 = (EditText) view.findViewById(R.id.col1);
        col1.setError("Required");
        val1 = (EditText) view.findViewById(R.id.col1a);
        val1.setError("Required");
        val2 = (EditText) view.findViewById(R.id.col2a);
        val3 = (EditText) view.findViewById(R.id.col3a);
        col2 = (EditText) view.findViewById(R.id.col2);
        col3 = (EditText) view.findViewById(R.id.col3);

        process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Response.Listener<String> responseListener1 = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            System.out.println(response);
                            JSONObject jsonObject = new JSONObject(response);

                            boolean success = jsonObject.getBoolean("success");

                            if (success) {


                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setMessage("Loan issued successfully!").setPositiveButton("Okay", null).create().show();

                                etamount.setText("");
                                autoCompleteTextView.setText("");
                                etamount.setText("");
                                col1.setText("");
                                val1.setText("");
                                col2.setText("");
                                val2.setText("");
                                col3.setText("");
                                val3.setText("");
                                Tab2Fragment.autoCompleteTextView.setText("");
                                Tab2Fragment.balance.setText("");
                                Tab2Fragment.idno.setText("");
                                Tab2Fragment.phone.setText("");

                                Intent intent = new Intent(getContext(), MainActivity.class);
                                startActivity(intent);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Loan could not be approved " + e,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                };

                String account = null;
                String sql = "INSERT INTO `seskenya`.`loans` " +
                        "(`Memberid`, `guarantor`, `transactiontype`, `account`, `col1`, `val1`,`col2`, `val2`,`col3`, `val3`, `amount`)" +
                        " VALUES ('"+memid+"', '"+guarontorid+"', 'Borrow', '"+account+"', '"+col1.getText().toString()+"', '"+val1.getText().toString()+"', '"+col2.getText().toString()+"', '"+val2.getText().toString()+"', '"+col3.getText().toString()+"', '"+val3.getText().toString()+"','"+ etamount.getText().toString()+"');";
                String function = "action";
                ActionRequest driverLoginRequest = new ActionRequest("dbqueries.php", function, sql, responseListener1);
                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                requestQueue.add(driverLoginRequest);
            }
        });

        return view;
    }
}
