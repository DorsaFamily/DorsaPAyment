package ir.dorsa.totalpayment.tools;
/*
 * Created by AMiR Ehsan on 7/22/2017.
 */

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import ir.dorsa.totalpayment.R;


public class Utils {

    public static final int RECORD_REQUEST_CODE = 101;
    public static String PURCHASETOKEN = "PURCHASETOKEN";
    public static String REGISTER_INFO = "REGISTER_INFO";
    public static String REGISTER_INFO_INSTALL_KEY = "REGISTER_INFO_INSTALL_KEY";
    public static String REGISTER_INFO_ACTIVE_KEY = "REGISTER_INFO_ACTIVE_KEY";
    public static String REGISTER_INFO_DEACTIVE_KEY = "REGISTER_INFO_DEACTIVE_KEY";
    public static String PURCHASETOKENKEY = "PURCHASETOKENKEY";


    public static void setBooleanPreference(Context context, String masterKey, String key, boolean value) {
        SharedPreferences settings = context.getSharedPreferences(masterKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBooleanPreference(Context context, String masterKey, String key) {
        SharedPreferences settings = context.getSharedPreferences(masterKey, Context.MODE_PRIVATE);
        return settings.getBoolean(key, false);
    }

    public static boolean getPermission(Activity activity) {
        int READ_PHONE_STATE = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE);
        int RECEIVE_SMS = ContextCompat.checkSelfPermission(activity, Manifest.permission.RECEIVE_SMS);
        if (READ_PHONE_STATE != PackageManager.PERMISSION_GRANTED ||
                RECEIVE_SMS != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    public static void requestPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.RECEIVE_SMS},
                RECORD_REQUEST_CODE);
    }

    public static void setStringPreference(Context context, String masterKey, String key, String value) {
        SharedPreferences settings = context.getSharedPreferences(masterKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getStringPreference(Context context, String masterKey, String key, String defeult) {
        SharedPreferences settings = context.getSharedPreferences(masterKey, Context.MODE_PRIVATE);
        return settings.getString(key, defeult);
    }

    public static String[] getSecrets(Context context){
        return context.getResources().getStringArray(R.array.secrets);
    }
}