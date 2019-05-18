package ir.dorsa.totalpayment.tools;

import android.content.Context;
import android.util.Log;

import net.jhoobin.amaroidsdk.Amaroid;

public class UncaughtExHandler implements Thread.UncaughtExceptionHandler {
    private static UncaughtExHandler uncaughtExHandler;
    private Thread.UncaughtExceptionHandler androidDefaultUncaughtExceptionHandler;
    private static Context context;
    public static UncaughtExHandler getInstance(Context mContext) {
        context=mContext;
        if (uncaughtExHandler == null)
            uncaughtExHandler = new UncaughtExHandler();
        return uncaughtExHandler;
    }

    private UncaughtExHandler() {
        androidDefaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        try {
            Amaroid.getInstance().submitEventException(context,throwable);
        } catch (Throwable e) {
            Log.e("UncaughtExHandler", "Unable to submitEventException", e);
        } finally {
            androidDefaultUncaughtExceptionHandler.uncaughtException(thread, throwable);
        }
    }
}
