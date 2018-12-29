package ir.dorsa.totalpayment.payment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ir.dorsa.totalpayment.R;
import ir.dorsa.totalpayment.dialog.DialogMessage;

import static ir.dorsa.totalpayment.payment.IMPayment.STATUS_INTERNRT_CONNECTION;
import static ir.dorsa.totalpayment.payment.IMPayment.STATUS_NO_CHARGE;

public class FragmentCheckStatus extends Fragment implements IVPayment {

    private static final int REQUEST_PHONE_CALL = 12;
    private View pView;
    private ProgressDialog pDialog;

    private String chargeNumber="tel:"+Uri.encode("*3003*1*2#");

    private static final String KEY_PRODUCT_CODE = "KEY_PRODUCT_CODE";
    private static final String KEY_APP_CODE = "KEY_APP_CODE";
    private static final String KEY_SKU = "KEY_SKU";

    private String productCode;
    private String sku;
    private String appCode;

    private OnCheckStatus onCheckStatus;

    private  PPayment pPayment;

    public static FragmentCheckStatus newInstance(
            String productCode,
            String appCode,
            String sku
    ) {
        FragmentCheckStatus fragment = new FragmentCheckStatus();
        Bundle args = new Bundle();

        args.putString(KEY_PRODUCT_CODE, productCode);
        args.putString(KEY_SKU, sku);
        args.putString(KEY_APP_CODE, appCode);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productCode = getArguments().getString(KEY_PRODUCT_CODE);
            sku = getArguments().getString(KEY_SKU);
            appCode = getArguments().getString(KEY_APP_CODE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        pView = inflater.inflate(R.layout.payment_fragment_check_status, container, false);
        pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("لطفا صبور باشید...");

        pPayment= new PPayment(this, appCode, productCode, sku);
        pPayment.checkStatus();

        return pView;
    }


    @Override
    public void onStartCheckStatus() {
        pDialog.show();
    }

    @Override
    public void onSuccessCheckStatus() {
        pDialog.cancel();
        if (onCheckStatus != null) {
            onCheckStatus.alreadySubscribed();
        }

    }

    @Override
    public void onFailedCheckStatus(int errorCode, final String errorMessage) {
        pDialog.cancel();
        if(errorCode==STATUS_NO_CHARGE){
            final DialogMessage dialog=new DialogMessage(getContext());
            dialog.setCancelable(false);
            dialog.setMessage("دوست عزیز اعتبار خط شما به پایان رسیده است. \nبرای استفاده از این برنامک اعتبار خود را افزایش دهید یا از شماره دیگری استفاده کنید");
            dialog.setTextButtonOk("خروج");
            dialog.setTextButtonCancel("تغییر شماره");
            dialog.setTextButtonOthers("شارژ بخر فندق بگیر");

            dialog.setClickListenerPositive(new DialogMessage.ClickListener() {
                @Override
                public void onClick() {
                    if (onCheckStatus != null) {
                        onCheckStatus.onExit("کمبود اعتبار");
                    }
                }
            });

            dialog.setClickListenerNegative(new DialogMessage.ClickListener() {
                @Override
                public void onClick() {
                    pPayment.clearUserInfo(false);
                    if (onCheckStatus != null) {
                        onCheckStatus.startPaymentFlow(false);
                    }
                }
            });

            dialog.setClickListenerOthers(new DialogMessage.ClickListener() {
                @Override
                public void onClick() {
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_CALL);

                        intent.setData(Uri.parse(chargeNumber));
                        startActivity(intent);

                        if (onCheckStatus != null) {
                            onCheckStatus.onExit("کمبود اعتبار");
                        }
                    }






                }
            });


            dialog.show();
        }else if(errorCode==STATUS_INTERNRT_CONNECTION){
            final DialogMessage dialog=new DialogMessage(getContext());
            dialog.setCancelable(false);
            dialog.setMessage(errorMessage);
            dialog.setTextButtonOk("تلاش مجدد");
            dialog.setTextButtonCancel("خروج");

            dialog.setClickListenerPositive(new DialogMessage.ClickListener() {
                @Override
                public void onClick() {
                    pPayment.checkStatus();
                }
            });

            dialog.setClickListenerNegative(new DialogMessage.ClickListener() {
                @Override
                public void onClick() {
                    if (onCheckStatus != null) {
                        onCheckStatus.onExit(errorMessage);
                    }
                }
            });

            dialog.show();
        }else{
            if (onCheckStatus != null) {
                onCheckStatus.startPaymentFlow(true);
            }
        }

    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnCheckStatus){
            onCheckStatus=(OnCheckStatus)context;
        }else if (getParentFragment() instanceof OnCheckStatus){
            onCheckStatus=(OnCheckStatus)getParentFragment();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onCheckStatus=null;
    }


    @Override
    public Fragment getFragment() {
        return this;
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            //permission granted
            Intent intent = new Intent(Intent.ACTION_CALL);

            intent.setData(Uri.parse(chargeNumber));
            startActivity(intent);

        }else{
            //permission not granted
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse(chargeNumber));
            startActivity(intent);
        }

        if (onCheckStatus != null) {
            onCheckStatus.onExit("کمبود اعتبار");
        }
    }

    public interface OnCheckStatus{
        void startPaymentFlow(boolean showIntro);
        void alreadySubscribed();
        void onExit(String message);


    }

}
