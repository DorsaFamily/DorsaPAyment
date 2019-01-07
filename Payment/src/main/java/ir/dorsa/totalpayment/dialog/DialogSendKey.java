package ir.dorsa.totalpayment.dialog;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import ir.dorsa.totalpayment.R;
import ir.dorsa.totalpayment.tools.Func;

import static ir.dorsa.totalpayment.service.SmsListener.BROADCAST_UPDATE;


public class DialogSendKey extends Fragment {

    public static final String TAG_FRG_SEND_KEY = "TAG_FRG_SEND_KEY";

    private TextView textKeyChangeNumber;
    private TextView textKeyTitle;
    private TextView textError;
    private EditText textKeyKey;
    private TextView textDescSendKey;
    private TextView textDailyPrice;
    private View btnKeySend;
    private View btnKeyCancel;

    private interactionSendKey mListener;

    private String phoneNumber;
    private String message;
    private String error;
    private String mciDailyPrice = "۳۰۰";
    private String irancellDailyPrice = "۳۰۰";


    private View pView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        pView = inflater.inflate(R.layout.payment_dialog_register_aouth_key, container, false);
        init();
        refreshViews();
        return pView;
    }


    private void init() {
        textKeyChangeNumber = pView.findViewById(R.id.dialog_register_changeNumber);
        textKeyTitle = pView.findViewById(R.id.dialog_register_desc);
        textError = pView.findViewById(R.id.dialog_register_hint);
        textKeyKey = pView.findViewById(R.id.dialog_register_code);
        textDailyPrice = pView.findViewById(R.id.dialog_register_daily_price);
        btnKeySend = pView.findViewById(R.id.dialog_register_btn_send);
        btnKeyCancel = pView.findViewById(R.id.dialog_register_btn_cancel);
        textDescSendKey = pView.findViewById(R.id.title_register);

        textKeyChangeNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.changePhoneNumber();
                }
            }
        });

        btnKeySend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textKeyKey.getText().toString().length() == 0) {
                    setError("کد فعالسازی را وارد نمایید");
                    applyError();
                } else {
                    if (mListener != null) {
                        mListener.doSendKey(textKeyKey.getText().toString());
                    }
                }

            }
        });

        btnKeyCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.cancelSendKey();
                }
            }
        });

    }

    public void refreshViews() {
        applyMessage();
        applyPhoneNumber();
        applyError();
        applyDailyPrice();
    }


    public void setListener(interactionSendKey mListener) {
        this.mListener = mListener;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setDailyPrice(String mciDailyPrice, String irancellDailyPrice) {
        this.mciDailyPrice = mciDailyPrice;
        this.irancellDailyPrice = irancellDailyPrice;
    }

    public void applyError() {
        if (textError == null) return;
        if (error != null && !error.isEmpty()) {
            textError.setText(error);
            textError.setVisibility(View.VISIBLE);
        } else {
            textError.setVisibility(View.GONE);
        }
    }

    public void applyPhoneNumber() {
        if (textKeyTitle != null) {
            textKeyTitle.setText(phoneNumber);
        }
    }

    private void applyKey(String key) {
        if (textKeyKey != null) {
            textKeyKey.setText(key);
            // shakeView(btnKeySend);
        }
    }

    public void applyMessage() {
        if (textDescSendKey != null) {
            textDescSendKey.setText(message);
        }

    }

    public void applyDailyPrice() {
        if (textDailyPrice != null) {
            if (Func.isNumberMci(phoneNumber)) {
                textDailyPrice.setText("هزینه روزانه " +
                        mciDailyPrice
                                .replace("0", "۰")
                                .replace("1", "۱")
                                .replace("2", "۲")
                                .replace("3", "۳")
                                .replace("4", "۴")
                                .replace("5", "۵")
                                .replace("6", "۶")
                                .replace("7", "۷")
                                .replace("8", "۸")
                                .replace("9", "۹")
                        + " تومان");
            } else {
                textDailyPrice.setText("هزینه روزانه " +
                        irancellDailyPrice
                                .replace("0", "۰")
                                .replace("1", "۱")
                                .replace("2", "۲")
                                .replace("3", "۳")
                                .replace("4", "۴")
                                .replace("5", "۵")
                                .replace("6", "۶")
                                .replace("7", "۷")
                                .replace("8", "۸")
                                .replace("9", "۹")
                        + " تومان");
            }

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        IntentFilter iff = new IntentFilter(BROADCAST_UPDATE);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(onGotKey, iff);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(onGotKey);
    }

    private BroadcastReceiver onGotKey = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                applyKey(intent.getStringExtra("code"));
                /*if (mListener != null) {
                    mListener.doSendKey(intent.getStringExtra("code"));

                }*/

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };

    private void shakeView(View target) {
        android.animation.AnimatorSet animationSet = new android.animation.AnimatorSet();
        animationSet.playTogether(
                ObjectAnimator.ofFloat(target, "translationX", 0, 25, -25, 25, -25, 15, -15, 6, -6, 0)

        );
        animationSet.setDuration(300);
        animationSet.start();
    }

    public interface interactionSendKey {
        void changePhoneNumber();

        void doSendKey(String key);

        void cancelSendKey();
    }
}
