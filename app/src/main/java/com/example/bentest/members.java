package com.example.bentest;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class members extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Toolbar toolbar;
    public EditText Totalmembers,savingss,loans_savings,advances_savings,loans_edu,advances_edu;
    PieChart pieChart;
    PieData pieData;
    DrawerLayout mdrawerLayout;
    PieDataSet pieDataSet;
    ArrayList pieEntries;
    ArrayList PieEntryLabels;
    BarChart barChart;
    BarData barData;
    BarDataSet barDataSet;
    ArrayList barEntries;
    ProgressBar pgr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);
        pieChart = findViewById(R.id.pieChart);
        Totalmembers=findViewById(R.id.totalmembers);
        savingss=findViewById(R.id.savings);
        loans_savings=findViewById(R.id.loans_savings);
        advances_savings=findViewById(R.id.advances_savings);
        loans_edu=findViewById(R.id.loans_edu);
        advances_edu=findViewById(R.id.advances_edu);
        pgr=findViewById(R.id.progressBar3);



//        getEntries();
//        pieDataSet = new PieDataSet(pieEntries, "");
//        pieData = new PieData(pieDataSet);
//        pieChart.setData(pieData);
//        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
//        pieDataSet.setSliceSpace(2f);
//        pieDataSet.setValueTextColor(Color.WHITE);
//        pieDataSet.setValueTextSize(10f);
//        pieDataSet.setSliceSpace(5f);
//        pieChart.animateX(5000);
//        barChart = findViewById(R.id.BarChart);
//        getEntries2();
//        barDataSet = new BarDataSet(barEntries, "");
//        barData = new BarData(barDataSet);
//        barChart.setData(barData);
//        barDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
//        barDataSet.setValueTextColor(Color.BLACK);
//        barDataSet.setValueTextSize(18f);
//        barChart.animateX(5000);
        getmembersdetails();
    }
    private void getEntries() {
        pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(12f, 0));
        pieEntries.add(new PieEntry(40f, 1));
        pieEntries.add(new PieEntry(60f, 2));
        pieEntries.add(new PieEntry(80f, 3));
        pieEntries.add(new PieEntry(70f, 4));
        pieEntries.add(new PieEntry(30f, 5));
    }
    private void getEntries2() {
        barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(2f, 0));
        barEntries.add(new BarEntry(4f, 1));
        barEntries.add(new BarEntry(6f, 1));
        barEntries.add(new BarEntry(8f, 3));
        barEntries.add(new BarEntry(7f, 4));
        barEntries.add(new BarEntry(3f, 3));
    }


    public void getmembersdetails(){

        pgr.setVisibility(View.VISIBLE);
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

                            int totalmember=jsonChildNode.getInt("totalmembers");
                            int totalsavings=jsonChildNode.getInt("totalsavings");
                            int advance_savings=jsonChildNode.getInt("advance_savings");
                            int Loans_savings=jsonChildNode.getInt("Loans_savings");
                            int Advance_edu=jsonChildNode.getInt("advance_edu");
                            int Loans_advance=jsonChildNode.getInt("Loans_advance");
                            int savingsnu=jsonChildNode.getInt("savingsnu");
                            int advancenu=jsonChildNode.getInt("advancenu");
                            int Loansnu=jsonChildNode.getInt("Loansnu");
                            pgr.setVisibility(View.GONE);

                                Totalmembers.setText(String.valueOf(totalmember));
                                savingss.setText(String.valueOf(totalsavings));
                                advances_savings.setText(String.valueOf(advance_savings));
                                loans_savings.setText(String.valueOf(Loans_savings));
                           advances_edu.setText(String.valueOf(Advance_edu));
                           loans_edu.setText(String.valueOf(Loans_advance));





                            pieEntries = new ArrayList<>();

                            //int totalsi=Integer.parseInt(k);
                            pieEntries.add(new PieEntry(totalmember, "MEMBERS"));
                            pieEntries.add(new PieEntry(savingsnu, "SAVINGS"));
                            pieEntries.add(new PieEntry(Loansnu, "Loans"));
                            pieEntries.add(new PieEntry(advancenu, "advances"));

                            pieDataSet = new PieDataSet(pieEntries, "Summary");
                            pieData = new PieData(pieDataSet);
                            pieChart.setData(pieData);
                            pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                            pieDataSet.setSliceSpace(2f);
                            pieDataSet.setValueTextColor(Color.BLUE);
                            pieDataSet.setValueTextSize(10f);
                            pieDataSet.setSliceSpace(5f);
                            pieChart.animateX(5000);




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


                String sql="SELECT ifnull((select count(*) from members),0) as totalmembers,ifnull((select sum(`amount`) " +
                        "from `transactions` where `transactiontype`='SAVINGS' AND `transactionoption`='SAVINGS' ),0) as" +
                        " totalsavings,ifnull((select sum(`amount`) from `transactions` where `transactiontype`='BORROW' AND " +
                        "`transactionoption`='ADVANCE' AND `status`='Active' ),0)- ifnull((select sum(`amount`) from " +
                        "`transactions` where `transactiontype`='PAYMENT' AND `transactionoption`='ADVANCE' AND `status`='Active'" +
                        " AND `account`='SAVINGS' ),0) as advance_savings,ifnull((select sum(`amount`) from `loans` where " +
                        "`transactiontype`='Loan' AND `transactionoption`='Borrow' AND `Active`='active' ),0)- " +
                        "ifnull((select sum(`amount`) from `loans` where `transactiontype`='Loan' AND `transactionoption`=" +
                        "'PAYMENT' AND `Active`='active' AND `account`='SAVINGS' ),0) as Loans_savings,ifnull((select sum(`amount`) " +
                        "from `transactions` where `transactiontype`='BORROW' AND `transactionoption`='ADVANCE' AND `status`='Active' and `account`='EDUCATION' ),0)- " +
                        "ifnull((select sum(`amount`) from `transactions` where `transactiontype`='PAYMENT' AND `transactionoption`='ADVANCE' AND `status`='Active' AND `account`='EDUCATION' ),0)" +
                        " as advance_edu,ifnull((select sum(`amount`) from `loans` where `transactiontype`='Loan' AND `transactionoption`='Borrow' AND `status`='Waiting' AND `account`='EDUCATION' ),0)-" +
                        " ifnull((select sum(`amount`) from `loans` where `transactiontype`='Loan' AND `transactionoption`='PAYMENT' AND `status`='waiting' AND `account`='EDUCATION' ),0) as" +
                        " Loans_advance,ifnull((select count(DISTINCT `memberid`)" +
                        " from `transactions` where `transactionoption`='SAVINGS' AND `transactiontype`='SAVINGS'),0)" +
                        " as savingsnu,ifnull((select count(DISTINCT `memberid`) from `transactions` where `transactionoption`" +
                        "='ADVANCE' AND `transactiontype`" +
                        "='BORROW' and `status`='Active'),0) as advancenu,ifnull((select count(DISTINCT `memberid`)" +
                        " from `loans` where `transactionoption`='Borrow' AND `transactiontype`='Loan'),0) as Loansnu";





                params.put("function", "getresult");
                params.put("sql",sql);



                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);



    }


    public void drawable(){
        ActionBarDrawerToggle darwertoggle=new ActionBarDrawerToggle(this,mdrawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close);
        mdrawerLayout.addDrawerListener(darwertoggle);
        darwertoggle.syncState();
        NavigationView nav_view=findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {


            int id = menuItem.getItemId();

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


        mdrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(mdrawerLayout.isDrawerOpen(GravityCompat.START)){
            mdrawerLayout.closeDrawer(GravityCompat.START);
        } else{
            super.onBackPressed();}

    }







}