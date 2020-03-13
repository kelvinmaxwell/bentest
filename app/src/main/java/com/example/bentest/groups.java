
package com.example.bentest;

        import android.app.DownloadManager;
        import android.content.Intent;
        import android.os.Bundle;

        import androidx.appcompat.app.AppCompatActivity;
        import androidx.appcompat.widget.Toolbar;

        import android.util.Log;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.ListView;
        import android.widget.ProgressBar;
        import android.widget.Toast;

        import com.android.volley.AuthFailureError;
        import com.android.volley.Request;
        import com.android.volley.Response;
        import com.android.volley.VolleyError;
        import com.android.volley.VolleyLog;
        import com.android.volley.toolbox.JsonArrayRequest;
        import com.android.volley.toolbox.StringRequest;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;

        import static com.example.bentest.MainActivity.memidno;

public class groups extends AppCompatActivity {
    private CustomListAdapter adapter;
    private List<groupsclass> movieList = new ArrayList<groupsclass>();
    private ListView listView;
    ProgressBar pgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listView = (ListView) findViewById(R.id.list);
        adapter = new CustomListAdapter(this, movieList);
        listView.setAdapter(adapter);
        pgr=findViewById(R.id.progressBar);
        getlistview();
        pgr.setVisibility(View.VISIBLE);


    }


    public void getlistview(){
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
                        //set this to the spinner

                        JSONArray jsonMainNode = jsonObject.optJSONArray("data");
                        for (int i = 0; i < jsonMainNode.length(); i++) {

                            JSONObject obj = jsonMainNode.getJSONObject(i);


                            groupsclass movie = new groupsclass();
                            movie.setTitle(obj.getString("name"));
                            movie.setPrice(obj.getString("id"));
                            movie.setRating(obj.getString("Total"));
                            movieList.add(movie);
                            pgr.setVisibility(View.GONE);

                        }
                        adapter.notifyDataSetChanged();

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


                String sql="SELECT groups.id, groups.name, COUNT(members.group) AS" +
                        " Total FROM groups LEFT JOIN members ON groups.id = members.group GROUP BY groups.id,groups.name";




                params.put("function", "getresult");
                params.put("sql",sql);



                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);




    }

    }



