package ir.dorsa.totalpayment.payment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ir.dorsa.totalpayment.R;
import ir.dorsa.totalpayment.dialog.DialogMessage;

import static ir.dorsa.totalpayment.payment.IMPayment.STATUS_INTERNRT_CONNECTION;
import static ir.dorsa.totalpayment.payment.IMPayment.STATUS_NO_CHARGE;

public class FragmentCheckStatus extends Fragment implements IVPayment {

    private View pView;
    private ProgressDialog pDialog;

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
        pView = inflater.inflate(R.layout.fragment_check_status, container, false);
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
            dialog.setMessage("شارژ شما برای ادامه کم می باشد\nاعتبار خود را افزایش دهید و یا از شماره دیگری استفاده نمایید");
            dialog.setTextButtonOk("افزایش اعتبار");
            dialog.setTextButtonCancel("تغییر شماره");

            dialog.setClickListnerPosetive(new DialogMessage.ClickListnerPosetive() {
                @Override
                public void onClick() {
                    if (onCheckStatus != null) {
                        onCheckStatus.onExit("کمبود اعتبار");
                    }
                }
            });

            dialog.setClickListenerNegative(new DialogMessage.ClickListenerNegative() {
                @Override
                public void onClick() {
                    pPayment.clearUserInfo();
                    if (onCheckStatus != null) {
                        onCheckStatus.startPaymentFlow(false);
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

            dialog.setClickListnerPosetive(new DialogMessage.ClickListnerPosetive() {
                @Override
                public void onClick() {
                    pPayment.checkStatus();
                }
            });

            dialog.setClickListenerNegative(new DialogMessage.ClickListenerNegative() {
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


    public interface OnCheckStatus{
        void startPaymentFlow(boolean showIntro);
        void alreadySubscribed();
        void onExit(String message);


    }

}
