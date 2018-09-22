package ir.dorsa.totalpayment.payment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import ir.dorsa.totalpayment.R;
import ir.dorsa.totalpayment.dialog.DialogMessage;
import ir.dorsa.totalpayment.dialog.DialogSendKey;
import ir.dorsa.totalpayment.dialog.DialogSendPhoneNumber;
import ir.dorsa.totalpayment.tools.Utils;

import static android.app.Activity.RESULT_OK;
import static ir.dorsa.totalpayment.payment.Payment.KEY_APP_CODE;
import static ir.dorsa.totalpayment.payment.Payment.KEY_MARKET_ID;
import static ir.dorsa.totalpayment.payment.Payment.KEY_PRODUCT_CODE;
import static ir.dorsa.totalpayment.payment.Payment.KEY_SKU;
import static ir.dorsa.totalpayment.service.SmsListener.BROADCAST_UPDATE;

public class FragmentPayment extends Fragment implements IVPayment {

    private View pView;
    private interactionPayment mListener;

    private String productCode;
    private String sku;
    private String appCode;
    private String marketId;

    private static final String KEY_MESSAGE_SEND_PHONE_NUMBER = "KEY_MESSAGE_SEND_PHONE_NUMBER";

    private String textSendPhonenumber="";

    private PPayment pPayment;
    private ProgressDialog pDialog;
    private DialogSendPhoneNumber dialogSendPhoneNumber;
    private DialogSendKey dialogSendKey;

    private String phoneNumber;
    private int PERMISSION_REQUEST_CODE = 12;

    public void setListener(interactionPayment mListener) {
        this.mListener = mListener;
    }

    public static FragmentPayment newInstance(
            String textSendPhoneNumber,
            String productCode,
            String appCode,
            String sku
    ) {
        FragmentPayment fragment = new FragmentPayment();
        Bundle args = new Bundle();

        args.putString(KEY_MESSAGE_SEND_PHONE_NUMBER, textSendPhoneNumber);

        args.putString(KEY_PRODUCT_CODE, productCode);
        args.putString(KEY_SKU, sku);
        args.putString(KEY_APP_CODE, appCode);
        fragment.setArguments(args);
        return fragment;
    }

    public static FragmentPayment newInstance(
            String textSendPhoneNumber,
            String productCode,
            String appCode,
            String sku,
            String marketId
    ) {
        FragmentPayment fragment = new FragmentPayment();
        Bundle args = new Bundle();

        args.putString(KEY_MESSAGE_SEND_PHONE_NUMBER, textSendPhoneNumber);

        args.putString(KEY_PRODUCT_CODE, productCode);
        args.putString(KEY_SKU, sku);
        args.putString(KEY_APP_CODE, appCode);
        args.putString(KEY_MARKET_ID, marketId);
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
            marketId = getArguments().getString(KEY_MARKET_ID);

            textSendPhonenumber=getArguments().getString(KEY_MESSAGE_SEND_PHONE_NUMBER);

        }
        pPayment = new PPayment(this, appCode, productCode,sku);
        if(marketId!=null){
            pPayment.setMarketingId(marketId);
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        pView = inflater.inflate(R.layout.fragment_payment, container, false);
        pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("لطفا صبور باشید...");

        dialogSendPhoneNumber = new DialogSendPhoneNumber(getContext());
        dialogSendPhoneNumber.setListener(new DialogSendPhoneNumber.interactionPhoneNumber() {
            @Override
            public void requestPermission(String phoneNumber) {
                FragmentPayment.this.phoneNumber = phoneNumber;
                requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS}, PERMISSION_REQUEST_CODE);
            }

            @Override
            public void hasKey(String phoneNumber) {
                if(pPayment.checkPhoneNumber(phoneNumber)) {
                    FragmentPayment.this.phoneNumber = phoneNumber;
                    dialogSendPhoneNumber.cancel();
                    dialogSendKey.setPhoneNumber(phoneNumber);
                    dialogSendKey.setMessage(getContext().getString(R.string.desc_send_has_key));
                    pPayment.setPhoneNumber(phoneNumber);
                    pPayment.setHasKey(true);
                    dialogSendKey.show();
                }
            }

            @Override
            public void sendPhoneNumber(String phoneNumber) {
                pPayment.doSendPhoneNumber(phoneNumber);
            }

            @Override
            public void cancelSendPhoneNumber() {
                if (mListener != null) {
                    mListener.onBackPressed();
                }
            }
        });
        dialogSendPhoneNumber.setMessage(textSendPhonenumber);
        dialogSendPhoneNumber.setCancelable(false);


        dialogSendKey = new DialogSendKey(getContext());
        dialogSendKey.setListener(new DialogSendKey.interactionSendKey() {
            @Override
            public void changePhoneNumber() {
                dialogSendKey.cancel();
                pPayment.setHasKey(false);
                dialogSendPhoneNumber.show();
            }

            @Override
            public void doSendKey(String key) {
                pPayment.doSendKey(key);
            }

            @Override
            public void cancelSendKey() {
                pPayment.setHasKey(false);
                dialogSendPhoneNumber.show();
            }
        });
        dialogSendKey.setMessage(getString(R.string.desc_send_code));
        dialogSendKey.setCancelable(false);


        if (!"-1".equals(pPayment.getPhoneNumber())) {
            dialogSendPhoneNumber.setPhoneNumber(pPayment.getPhoneNumber());
            dialogSendPhoneNumber.doShowHasKey(true);
        } else {
            dialogSendPhoneNumber.setPhoneNumber("");
            dialogSendPhoneNumber.doShowHasKey(false);
        }

        pPayment.setHasKey(false);
        dialogSendPhoneNumber.show();

        return pView;
    }


    @Override
    public Fragment getFragment() {
        return this;
    }

    ///////////////////////////////////////////////////////////////////////////
    // payment implementation
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public void onStartSendPhoneNumber() {
        pDialog.show();
        dialogSendPhoneNumber.hideError();
    }

    @Override
    public void onSuccessSendPhoneNumber() {
        pDialog.cancel();
        dialogSendPhoneNumber.cancel();
        dialogSendKey.setPhoneNumber(pPayment.getLocalPhoneNumber());
        dialogSendKey.setMessage(getContext().getString(R.string.desc_send_code));
        dialogSendKey.show();
    }

    @Override
    public void onFailedSendPhoneNumber(String errorMessage) {
        dialogSendPhoneNumber.showError(errorMessage);
        pDialog.cancel();
    }

    @Override
    public void onStartSendKey() {
        dialogSendKey.hideError();
        pDialog.show();
    }

    @Override
    public void onFailedSendKey(String errorMessage) {
        pDialog.cancel();
        dialogSendKey.showError(errorMessage);
    }

    @Override
    public void onSuccessSubscribe(String expiredDate) {
        pDialog.cancel();
        dialogSendKey.cancel();

        DialogMessage dialogMessage=new DialogMessage(getContext());
        dialogMessage.setMessage(expiredDate);
        dialogMessage.setTextButtonOk("تائید");
        dialogMessage.setCancelable(false);
        dialogMessage.setClickListnerPosetive(new DialogMessage.ClickListnerPosetive() {
            @Override
            public void onClick() {
                if (mListener != null) {
                    mListener.onSuccessSubscribe();
                }
            }
        });
        dialogMessage.show();

    }

    @Override
    public void onFailedSubscribe(String errorMessage) {
        pDialog.cancel();
        dialogSendKey.showError(errorMessage);
    }

    @Override
    public void onStartCheckStatus() {

    }

    @Override
    public void onFailedCheckStatus(int errorCode, String errorMessage) {

    }

    @Override
    public void onSuccessCheckStatus() {

    }

    @Override
    public void onStartPurchaseIrancell() {
        dialogSendPhoneNumber.hideError();
        dialogSendPhoneNumber.cancel();
        pDialog.show();
    }

    @Override
    public void onSuccessPurchaseIrancell(String message) {
        pDialog.cancel();

        DialogMessage dialogMessage=new DialogMessage(getContext());
        dialogMessage.setMessage(message);
        dialogMessage.setTextButtonOk("تائید");
        dialogMessage.setCancelable(false);
        dialogMessage.setClickListnerPosetive(new DialogMessage.ClickListnerPosetive() {
            @Override
            public void onClick() {
                if (mListener != null) {
                    mListener.onSuccessSubscribe();
                }
            }
        });
        dialogMessage.show();

        pPayment.disposeHelper();
    }

    @Override
    public void onFailedpurchaseIrancell(String message) {
        pDialog.cancel();
        dialogSendPhoneNumber.showError(message);
        pPayment.setHasKey(false);
        dialogSendPhoneNumber.show();

        pPayment.disposeHelper();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof interactionPayment) {
            mListener = (interactionPayment) context;
        }
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(onGotKey);
        super.onPause();
    }

    @Override
    public void onResume() {
        IntentFilter iff = new IntentFilter(BROADCAST_UPDATE);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(onGotKey, iff);
        super.onResume();
    }

    private BroadcastReceiver onGotKey = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            dialogSendKey.setKey(intent.getStringExtra("code"));
            pPayment.doSendKey(intent.getStringExtra("code"));
        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== PPayment.RC_REQUEST){
            pPayment.checkActivityResult(requestCode,resultCode,data);
            if (resultCode == RESULT_OK) {
                String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
                String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");
                try {
                    JSONObject json = new JSONObject(purchaseData);
                    String sku = json.getString("productId");
                    String developerPayload = json.getString("developerPayload");
                    String purchaseToken = json.getString("purchaseToken");
                    Utils.setStringPreference(getContext(), Utils.PURCHASETOKEN, Utils.PURCHASETOKENKEY, purchaseToken);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            /*if(pPayment.checkActivityResult(requestCode,resultCode,data)){
                onSuccessPurchaseIrancell("");
            }else{
                onFailedpurchaseIrancell("");
            }*/
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        pPayment.doSendPhoneNumber(phoneNumber);
    }

    public interface interactionPayment {
        void onSuccessSubscribe();

        void onFailedSubscribe();

        void onBackPressed();
    }
}
