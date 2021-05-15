package com.htech.ureward.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {
    private SharedPreferences mSharedPreferences;
    private final String KEY_USER_PREF = "KEY_USER_PREFERENCES";
    private final String KEY_ISREG="KEY_ISREG";
    private final String KEY_PASS="KEY_PASS";
    private final String KEY_NAME="KEY_NAME";
    private final String KEY_IMAGE="KEY_IMAGE";
    private final String KEY_MAIL="KEY_MAIL";

    private static volatile PreferencesManager Instance = null;

    public static PreferencesManager getInstance() {
        PreferencesManager localInstance = Instance;
        if (localInstance == null) {
            synchronized (PreferencesManager.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new PreferencesManager();
                }
            }
        }
        return localInstance;
    }

    public void setIsReg(Context context,boolean registered){
      mSharedPreferences=context.getSharedPreferences(KEY_USER_PREF,Context.MODE_PRIVATE);
      SharedPreferences.Editor editor=mSharedPreferences.edit();
      editor.putBoolean(KEY_ISREG,registered);
      editor.apply();
    }

    public Boolean getIsReg(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        return mSharedPreferences.getBoolean(KEY_ISREG, false);
    }

    public void setPass(Context context,String pass){
        mSharedPreferences=context.getSharedPreferences(KEY_USER_PREF,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=mSharedPreferences.edit();
        editor.putString(KEY_PASS,pass);
        editor.apply();
    }

    public String getPass(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        return mSharedPreferences.getString(KEY_PASS, null);
    }

    public void setName(Context context,String name){
        mSharedPreferences=context.getSharedPreferences(KEY_USER_PREF,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=mSharedPreferences.edit();
        editor.putString(KEY_NAME,name);
        editor.apply();
    }

    public String getName(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        return mSharedPreferences.getString(KEY_NAME, null);
    }

    public void setMail(Context context,String mail){
        mSharedPreferences=context.getSharedPreferences(KEY_USER_PREF,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=mSharedPreferences.edit();
        editor.putString(KEY_MAIL,mail);
        editor.apply();
    }

    public String getMail(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        return mSharedPreferences.getString(KEY_MAIL, null);
    }

    public void setImage(Context context,String image){
        mSharedPreferences=context.getSharedPreferences(KEY_USER_PREF,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=mSharedPreferences.edit();
        editor.putString(KEY_IMAGE,image);
        editor.apply();
    }

    public String getImage(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(KEY_USER_PREF, Context.MODE_PRIVATE);
        return mSharedPreferences.getString(KEY_IMAGE, null);
    }

}
