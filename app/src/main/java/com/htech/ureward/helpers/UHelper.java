package com.htech.ureward.helpers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.htech.ureward.dialog.ProgressLoad;


public class UHelper {
    private static ProgressLoad mLoading;

    public static void showDialog(Context context,String message){
     mLoading=new ProgressLoad(context,message);
     mLoading.setCancelable(false);
     mLoading.show();
    }
    public static void hideDialog(){
        if (mLoading!=null&&mLoading.isShowing()){
            mLoading.dismiss();
        }
    }
    public static void showToast(Context context,String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    public static void LogCat(String message){
        Log.d("MyTag",message);
    }
}
