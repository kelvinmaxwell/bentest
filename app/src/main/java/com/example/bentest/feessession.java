package com.example.bentest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

public class feessession {
    SharedPreferences sharedPreferences;
    public  SharedPreferences.Editor editor;
    public Context context;
    int PRIVATE_MODE=0;
    private  static final String PREF_NAME="LOGIN";
    private  static final String LOGIN="IS_LOGIN";


    public static final String loan_fee="loan_fee";
    public  static final String loan_intrests="loan_intrests";
    public  static final String advance_fee="advance_fee";
    public  static final String advance_intrests="advance_intrests";

    public  static final String advance_fines="advance_fines";
    public  static final String loans_fines="loans_fines";

    public feessession(Context context){
        this.context=context;
        sharedPreferences=context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        editor=sharedPreferences.edit();

    }
    public void createSession(String loan_fee,String loan_intrests,String advance_fee,String advance_intrests,String advance_fines,String loans_fines ){
        editor.putBoolean(LOGIN,true);
        editor.putString(loan_fee,loan_fee);
        editor.putString(loan_intrests,loan_intrests);
        editor.putString(advance_intrests,advance_intrests);

        editor.putString(advance_fee,advance_fee);
        editor.putString(advance_fines,advance_fines);
        editor.putString(loans_fines,loans_fines);


        editor.apply();
    }

    public boolean isLoggin(){
        return sharedPreferences.getBoolean(LOGIN,false);

    }
    /* public void checkLogin(){
         if(!this.isLoggin()){
             Intent i=new Intent(context,MainActivity.class);
             context.startActivity(i);
             ((addproducts)context).finish();
         }}
 */
    public HashMap<String,String> getUserDetail(){
        HashMap<String,String> user=new HashMap<>();
        user.put(loan_fee,sharedPreferences.getString(loan_fee,null));
        user.put(loan_intrests,sharedPreferences.getString(loan_intrests,null));
        user.put(advance_fee,sharedPreferences.getString(advance_fee,null));
        user.put(advance_fines,sharedPreferences.getString(advance_fines,null));
        user.put(loans_fines,sharedPreferences.getString(loans_fines,null));

        return user;
    }

    public void logout(){
        editor.clear();
        editor.commit();
        Intent i=new Intent(context,MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("EXIT", true);
        context.startActivity(i);

    }
}