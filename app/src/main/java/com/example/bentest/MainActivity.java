package com.example.bentest;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
//import com.example.ses.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
TextView loanstxt;
    QuickAction mLoans;
    Spinner spinner;
    ArrayList<String> Members = new ArrayList<>();
    static String memidno = "";
    static String groupid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

loanstxt=findViewById(R.id.loans);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);




       getrates();



        ActionItem requestAction = new ActionItem();
        requestAction.setTitle("Apply");
        requestAction.setIcon(getResources().getDrawable(R.drawable.savesaving));

        ActionItem payAction = new ActionItem();
        payAction.setTitle("Pay");
        payAction.setIcon(getResources().getDrawable(R.drawable.payadvance));

        //Save action item
        ActionItem SaveAction = new ActionItem();
        SaveAction.setTitle("Save");
        SaveAction.setIcon(getResources().getDrawable(R.drawable.savesaving));


        //Add action item
        ActionItem addAction = new ActionItem();
        addAction.setTitle("Request");
        addAction.setIcon(getResources().getDrawable(R.drawable.requestadvance));

        ActionItem accAction = new ActionItem();
        accAction.setTitle("Pay");
        accAction.setIcon(getResources().getDrawable(R.drawable.payadvance));


        final QuickAction mQuickAction = new QuickAction(this);
        mLoans = new QuickAction(this);

        mLoans.addActionItem(requestAction);
        mLoans.addActionItem(payAction);
        mLoans.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                if(pos==0){


                    Intent intent = new Intent(MainActivity.this, LoanApplicationForm.class);



                    startActivity(intent);
                }else  if(pos==1){
                    final AlertDialog dialogBuilder = new AlertDialog.Builder(MainActivity.this).create();
                    LayoutInflater inflater =LayoutInflater.from(MainActivity.this);
                    View dialogView = inflater.inflate(R.layout.popup, null);

                    final AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) dialogView.findViewById(R.id.edt_comment);
                    Button button1 = (Button) dialogView.findViewById(R.id.buttonSubmit);
                    Button button2 = (Button) dialogView.findViewById(R.id.buttonCancel);

                    spinner = (Spinner) dialogView.findViewById(R.id.spinner2);
                    if(spinner.getAdapter()==null){
                        getData();
                    }
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String selectedItemText =  spinner.getItemAtPosition(position).toString();
                            if (position > 0) {
                                autoCompleteTextView.setEnabled(true);
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


                                                }

                                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, Members);
                                                autoCompleteTextView.setAdapter(adapter);
                                                autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                    @Override
                                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                        String name=parent.getItemAtPosition(position).toString();
                                                        Toast.makeText(MainActivity.this, name, Toast.LENGTH_SHORT).show();

                                                        memidno = name.split(" ")[0];

                                                        SessionManager sessionManager=new SessionManager(getApplicationContext());

                                                        sessionManager.createSession(name,name,name,name);
                                                    }
                                                });


                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Toast.makeText(MainActivity.this, "failed getting members " + e,
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }
                                };
                                groupid = selectedItemText.replaceAll("[^\\d]", "");
                                String function = "query";
                                String sql = "select memberid,  concat(firstname,' ',secondname,' ',surname) as name from members where `group` = '"+selectedItemText.replaceAll("[^\\d]", "") +"'";
                                System.out.println(sql);
                                ActionRequest driverLoginRequest = new ActionRequest("dbqueries.php", function, sql, responseListener1);
                                RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
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
                    button2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogBuilder.dismiss();
                        }
                    });
                    button1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // DO SOMETHINGS

// set Fragmentclass Arguments
 Intent intent = new Intent(MainActivity.this, LoanRepayment.class);

                            intent.putExtra("name1","name1");
                            startActivity(intent);
                            dialogBuilder.dismiss();
                        }
                    });

                    dialogBuilder.setView(dialogView);
                    dialogBuilder.show();
                }
            }
        });


        mQuickAction.addActionItem(SaveAction);
        mQuickAction.addActionItem(addAction);
        mQuickAction.addActionItem(accAction);

        mQuickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
            @Override
            public void onItemClick(int pos) {

                if (pos == 0) { Intent intent = new Intent(MainActivity.this, MakeSaving.class);
                    startActivity(intent);
                } else if (pos == 1) { //Accept item selected
                    Intent intent = new Intent(MainActivity.this, Advance_Application.class);
                    startActivity(intent);
                } else if (pos == 2) {Intent intent = new Intent(MainActivity.this, payadvance.class);
                    startActivity(intent);
                }
            }
        });


        ImageView btn2 = (ImageView) this.findViewById(R.id.imageView6);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mQuickAction.show(v);
                mQuickAction.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
            }
        });


    }
    private HashMap<String, String> createOwner(String name, String number) {
        HashMap<String, String> employeeNameNo = new HashMap<String, String>();
        employeeNameNo.put(name, number);
        return employeeNameNo;
    }

    public void getData(){
        final List<Map<String, String>> ownersList = new ArrayList<Map<String, String>>(

        );

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    System.out.println(response);
                    JSONObject jsonObject = new JSONObject(response);
                    System.out.println(response);
                    boolean success = jsonObject.getBoolean("success");
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("Could not load groups").setNegativeButton("Retry", null).create().show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                SimpleAdapter simpleAdapter = new SimpleAdapter(MainActivity.this, ownersList,
                        android.R.layout.simple_list_item_1,
                        new String[]{"owners"}, new int[]{android.R.id.text1});
                spinner.setAdapter(simpleAdapter);


            }
        };

        String sql = "select id, name,  location from groups ORDER BY id";
        ActionRequest recordOffence = new ActionRequest("dboperations.php", "query", sql, responseListener);
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(recordOffence);
    }

    public void GotoLoanApplication(View view) {



        mLoans.show(view);
        mLoans.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);

    }







    public void GotoadvanceApplication(View view) {

        Intent intent = new Intent(MainActivity.this, Advance_Application.class);
        startActivity(intent);



    }


    ActionItem addAction = new ActionItem();


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_groups) {
            Intent i=new Intent(getApplicationContext(),groups.class);
            startActivity(i);
        } else if (id == R.id.nav_members) {
            Intent i=new Intent(getApplicationContext(),members.class);
            startActivity(i);
        } else if (id == R.id.nav_rates) {
Intent i=new Intent(getApplicationContext(),rates.class);
startActivity(i);
        } else if (id == R.id.nav_employees) {

        }else if (id == R.id.nav_regions) {

        }else if (id == R.id.nav_gallery) {

        }else if (id == R.id.nav_reports) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    public void getrates(){


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


                            feessession sessionManager1=new feessession(getApplicationContext());

                            sessionManager1.createSession(jsonChildNode.getString("loan_fee"),jsonChildNode.getString("loan_intrests")
                                    ,jsonChildNode.getString("advance_fee"),jsonChildNode.getString("advance_intrests"),jsonChildNode.getString("advance_fines"),jsonChildNode.getString("loans_fines"));


                        }

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


                String sql="select * from  fees";





                params.put("function", "getresult");
                params.put("sql",sql);



                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);



    }



}
