package com.example.bentest;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ActionRequest2 extends StringRequest{

    private static  final String URL = "http://192.168.43.78/www/html/seskenya/";
    private Map<String,String> params;

    public ActionRequest2(String myurl, String fname, String lname, String surname, String idno,
                          String dob, String marital, String phone, String otherphone, String address,
                          String county, String town, String location, String subLocation,
                          String imageString, String group, Response.Listener<String> listener){
        super(Method.POST,URL+myurl,listener,null);
        params = new HashMap<>();
        params.put("fname",fname);
        params.put("lname",lname);
        params.put("surname",surname);
        params.put("idno",idno);
        params.put("dob",dob);
        params.put("marital",marital);
        params.put("phone",phone);
        params.put("otherphone",otherphone);
        params.put("address",address);
        params.put("county",county);
        params.put("town",town);
        params.put("location",location);
        params.put("sublocation",subLocation);
        params.put("image", imageString);
        params.put("group",group);
    }
    public ActionRequest2(String myurl, String gname, String glocation, Response.Listener<String> listener){
        super(Method.POST,URL+myurl,listener,null);
        params = new HashMap<>();
        params.put("name",gname);
        params.put("location",glocation);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
