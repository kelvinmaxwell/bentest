package com.example.bentest;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ActionRequest extends StringRequest{

    private static  final String URL = "http://192.168.43.78/www/html/seskenya/";
    private Map<String,String> params;

    public ActionRequest(String path,String function,String StringPassed,Response.Listener<String> listener){

        super(Method.POST,URL+path,listener,null);
        params = new HashMap<>();
        params.put("path",path);
        params.put("function", function);
        params.put("StringPassed", StringPassed);

    }

    @Override
    public Map<String, String> getParams() {

        return params;
    }
}
