package ir.dorsa.totalpayment.payment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;

import com.psb.dorsa.R;

import net.jhoobin.jhub.CharkhoneSdkApp;

import ir.dorsa.totalpayment.PaymentActivity;
import ir.dorsa.totalpayment.irancell.IrancellCancel;
import ir.dorsa.totalpayment.tools.Func;
import ir.dorsa.totalpayment.tools.Utils;

import static ir.dorsa.totalpayment.payment.IMPayment.SH_P_BUY_IN_APP;
import static ir.dorsa.totalpayment.payment.IMPayment.SH_P_BUY_IN_APP_ENABLE_IRANCELL;
import static ir.dorsa.totalpayment.payment.IMPayment.SH_P_BUY_IN_APP_PHONE_NUMBER;

public class Payment {
    public static final String KEY_TEXT_SEND_PHONE_NUMBER = "KEY_TEXT_SEND_PHONE_NUMBER";
    public static final String KEY_APP_CODE = "KEY_APP_CODE";
    public static final String KEY_PRODUCT_CODE = "KEY_PRODUCT_CODE";
    public static final String KEY_SKU = "KEY_SKU";
    public static final String KEY_SPLASH = "KEY_SPLASH";
    public static final String KEY_MESSAGE = "message";

    private static  Context context;

    public Payment(Context context) {
        this.context = context;
        CharkhoneSdkApp.initSdk(context, Utils.getSecrets(context), R.drawable.dorsa_icon);
    }

    private onCheckFinished onCheckFinished;

    /**
     * متد دریافت intent فراخوانی پرداخت
     * @param textSendPhoneNumber  متن دیالوگ دریافت شماره موبایل (اجباری)
     * @param appCode شماره کد دریافت شده از درسا برای برنامه (اجباری)
     * @param productCode شماره محصول دریافت شده از درسا برای برنامه (اجباری)
     * @param irancellSku شماره کد دریافت شده برای پرداخت شماره های ایرانسل (اختیاری)
     * @param splashLayoutResource  آرایه لیست لایه های طراحی شده برای نمایش به کاربر (اختیاری)
     */
    public static Intent getPaymentIntent(
            String textSendPhoneNumber,
            String appCode,
            String productCode,
            String irancellSku,
            int[] splashLayoutResource

    ) {
        Intent intent = new Intent(context, PaymentActivity.class);
        intent.putExtra(KEY_TEXT_SEND_PHONE_NUMBER, textSendPhoneNumber);
        intent.putExtra(KEY_APP_CODE, appCode);
        intent.putExtra(KEY_PRODUCT_CODE, productCode);
        intent.putExtra(KEY_SKU, irancellSku);
        intent.putExtra(KEY_SPLASH, splashLayoutResource);
        return intent;
    }


    /**
     * متد بررسی وضعیت اشتراک کاربر
     * @param appCode شماره کد دریافت شده از درسا برای برنامه (اجباری)
     * @param productCode شماره محصول دریافت شده از درسا برای برنامه (اجباری)
     * @param sku شماره کد دریافت شده برای پرداخت شماره های ایرانسل (اختیاری)
     * @param onCheckFinished اینترفیس تکمیل بررسی
     */
    public void checkStatus(
            String appCode,
            String productCode,
            String sku,
            onCheckFinished onCheckFinished) {

        setOnCheckFinished(onCheckFinished);
        PPayment pPayment = new PPayment(ivPayment, appCode, productCode, sku);
        pPayment.setContext(context);
        pPayment.checkStatus();
    }


    public void setEnableIrancell(boolean isEnable){
        SharedPreferences sharedPrefrece = context.getSharedPreferences(SH_P_BUY_IN_APP, context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefereceEditor = sharedPrefrece.edit();
        sharedPrefereceEditor.putBoolean(SH_P_BUY_IN_APP_ENABLE_IRANCELL , isEnable);
        sharedPrefereceEditor.commit();
    }

    /**
     *بررسی ایرانسلی بودن کاربر
     */
    public boolean isUserIrancell(
            ) {
        PPayment pPayment = new PPayment(ivPayment, "", "", "");
        return !pPayment.getPhoneNumber().isEmpty() && Func.isNumberIrancell(pPayment.getPhoneNumber());
    }

    /**
     *بررسی ثبت نام کاربر
     */
    public boolean isUserPremium(
    ) {
        PPayment pPayment = new PPayment(ivPayment, "", "", "");
        return !pPayment.getPhoneNumber().isEmpty();
    }

    /**
     *نمایش اینکه آیا به کاربر دکمه انصراف از خرید را نمایش دهد
     */
    public boolean showCancelSubscribtion(){
        PPayment pPayment = new PPayment(ivPayment, "", "", "");
        if(isUserIrancell() && !pPayment.getHasKey()){
            return true;
        }

        return false;
    }


    /**
     * اینترفیس بررسی وضعیت اشتراک
     */
    public interface onCheckFinished {
        void result(boolean status, String message);
    }


    public void cancelIrancell(String irancellSku,IrancellCancel.onIrancellCanceled getResult){
        IrancellCancel irancellCancel = new IrancellCancel(context, getResult);
        irancellCancel.cancelPurchase(irancellSku);
    }

    public String getPhoneNumber(Context context){
        SharedPreferences sharedPrefrece = context.getSharedPreferences(SH_P_BUY_IN_APP, context.MODE_PRIVATE);
        return sharedPrefrece.getString(SH_P_BUY_IN_APP_PHONE_NUMBER, "");
    }

    private void setOnCheckFinished(Payment.onCheckFinished onCheckFinished) {
        this.onCheckFinished = onCheckFinished;
    }
    protected IVPayment ivPayment = new IVPayment() {
        @Override
        public Context getContext() {
            return context;
        }

        @Override
        public Fragment getFragment() {
            return null;
        }

        @Override
        public void onStartSendPhoneNumber() {

        }

        @Override
        public void onSuccessSendPhoneNumber() {

        }

        @Override
        public void onFailedSendPhoneNumber(String errorMessage) {

        }

        @Override
        public void onStartSendKey() {

        }

        @Override
        public void onFailedSendKey(String errorMessage) {

        }

        @Override
        public void onSuccessSubscribe(String expiredDate) {

        }

        @Override
        public void onFailedSubscribe(String errorMessage) {

        }

        @Override
        public void onStartPurchaseIrancell() {

        }

        @Override
        public void onSuccessPurchaseIrancell(String message) {

        }

        @Override
        public void onFailedpurchaseIrancell(String message) {

        }

        @Override
        public void onStartCheckStatus() {

        }

        @Override
        public void onFailedCheckStatus(int errorCode, String errorMessage) {
            if (onCheckFinished != null) {
                onCheckFinished.result(false, errorMessage);
            }
        }

        @Override
        public void onSuccessCheckStatus() {
            if (onCheckFinished != null) {
                onCheckFinished.result(true, "شارژینگ شما فعال است");
            }
        }
    };
}
