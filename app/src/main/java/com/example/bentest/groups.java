
package com.example.bentest;

        import android.graphics.Color;
        import android.os.Bundle;

        import com.android.volley.AuthFailureError;
        import com.android.volley.Request;
        import com.android.volley.Response;
        import com.android.volley.VolleyError;
        import com.android.volley.toolbox.StringRequest;
        import com.github.mikephil.charting.charts.BarChart;
        import com.github.mikephil.charting.charts.PieChart;
        import com.github.mikephil.charting.components.AxisBase;
        import com.github.mikephil.charting.components.XAxis;
        import com.github.mikephil.charting.data.BarData;
        import com.github.mikephil.charting.data.BarDataSet;
        import com.github.mikephil.charting.data.BarEntry;
        import com.github.mikephil.charting.data.PieData;
        import com.github.mikephil.charting.data.PieDataSet;
        import com.github.mikephil.charting.data.PieEntry;
        import com.github.mikephil.charting.formatter.IAxisValueFormatter;
        import com.github.mikephil.charting.utils.ColorTemplate;
        import com.google.android.material.floatingactionbutton.FloatingActionButton;
        import com.google.android.material.snackbar.Snackbar;

        import androidx.appcompat.app.AppCompatActivity;
        import androidx.appcompat.widget.Toolbar;

        import android.view.View;
        import android.widget.Toast;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.Map;

public class groups extends AppCompatActivity  implements IAxisValueFormatter {
    PieChart pieChart;
    PieData pieData;
    PieDataSet pieDataSet;
    ArrayList pieEntries;
    ArrayList PieEntryLabels;
    BarChart barChart;
    BarData barData;
    BarDataSet barDataSet;
    ArrayList barEntries;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        barChart = findViewById(R.id.BarChart);

        getmembersdetails();

    }

    public void getmembersdetails(){


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

                            int savingcount=jsonChildNode.getInt("savingcount");
                            int Advancecount=jsonChildNode.getInt("Advancecount");
                            int loanscount=  jsonChildNode.getInt ("loanscount");
                            //  fromdetails.setText("form1:   Form2:  Form3:   Form4:  Absent:" + absent+"/"+totals);
                            //  movie.setYear(dataobj.getString("date"));




                            barEntries = new ArrayList<>();

                            //int totalsi=Integer.parseInt(k);
                            barEntries.add(new BarEntry(1f, savingcount,"meru"));
                            barEntries.add(new BarEntry(3f,Advancecount));
                            barEntries.add(new BarEntry(4f, loanscount));
                            barDataSet = new BarDataSet(barEntries, "Data");
                            barData = new BarData(barDataSet);
                            barChart.setData(barData);
                            barDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
                            barDataSet.setValueTextColor(Color.BLACK);
                            barDataSet.setValueTextSize(18f);
                            barChart.animateX(5000);




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


                String sql="SELECT (select count(*) from transactions where account='SAVINGS') " +
                        "as savingcount,(select count(*) from transactions where account='EDUCATION')" +
                        " as Advancecount,(select count(*) from loans " +
                        "where status='waiting' and transactionoption='Borrow') as loanscount";





                params.put("function", "getresult");
                params.put("sql",sql);



                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);



    }
    public void LabelFormatter(String[] labels) {
        final ArrayList<String> xAxisLabel = new ArrayList<>();
        xAxisLabel.add("label1");
        xAxisLabel.add("Label2");
        xAxisLabel.add("label 3");

    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return null;
    }
}
