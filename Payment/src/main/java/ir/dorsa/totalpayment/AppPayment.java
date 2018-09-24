package ir.dorsa.totalpayment;

import android.content.Context;
import android.provider.Settings;
import android.support.multidex.MultiDexApplication;

import net.jhoobin.jhub.CharkhoneSdkApp;


public class AppPayment extends MultiDexApplication {

    public static final String TAG="DORSA_PAYMENT";

    private static Context context;
    public static String activityName;
    public static String LOG_TAG="FANDOGH";

    public static String getDeviceID(){
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    @Override
    public void onCreate() {
        context = getApplicationContext();


        CharkhoneSdkApp.initSdk(this, getSecrets(), R.drawable.dorsa_icon);

        super.onCreate();
    }

    public String[] getSecrets(){
        return getResources().getStringArray(
                R.array.secrets);
    }


    public static Context getContext(){
        return context;
    }


}
