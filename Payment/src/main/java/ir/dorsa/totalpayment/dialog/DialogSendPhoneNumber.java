package ir.dorsa.totalpayment.dialog;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import ir.dorsa.totalpayment.R;


public class DialogSendPhoneNumber extends Dialog {

    private interactionPhoneNumber mListener;

    private EditText textPhoneNumber;
    private TextView textPhoneNumberHint;
    private TextView textPhoneNumberHasKey;

    private TextView titleSendPhoneNumber;

    private Button btnPhoneNumberAcept;
    private Button btnPhoneNumberBack;

    public DialogSendPhoneNumber(@NonNull Context context) {
        super(context, android.R.style.Theme_Holo_Dialog_NoActionBar);
        init();
    }

    public DialogSendPhoneNumber(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    protected DialogSendPhoneNumber(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    @Override
    public void show() {
        hideError();
        super.show();
    }

    public void setListener(interactionPhoneNumber mListener) {
        this.mListener = mListener;
    }

    private void init() {
        setContentView(R.layout.payment_dialog_register_phone);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        textPhoneNumber = findViewById(R.id.dialog_register_phone_number);
        textPhoneNumberHint = findViewById(R.id.dialog_register_hint);
        textPhoneNumberHasKey = findViewById(R.id.dialog_register_has_key);

        btnPhoneNumberAcept = findViewById(R.id.dialog_register_btn_send);
        btnPhoneNumberBack = findViewById(R.id.dialog_register_btn_cancel);

        titleSendPhoneNumber= findViewById(R.id.title_register);


        textPhoneNumberHasKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textPhoneNumber.getText().toString().length() == 0) {
                    showError("لطفا شماره تماس را وارد نمایید");
                } else if (!textPhoneNumber.getText().toString().startsWith("09")) {
                    showError("لطفا شماره موبایل را به صورت صحیح وارد نمایید");
                } else if (textPhoneNumber.getText().length() < 11) {
                    showError("لطفا شماره موبایل را به صورت صحیح وارد نمایید");
                } else if(mListener!=null){
                    mListener.hasKey(textPhoneNumber.getText().toString());
                }

            }
        });

        btnPhoneNumberAcept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textPhoneNumber.getText().toString().length() == 0) {
                    showError("لطفا شماره تماس را وارد نمایید");
                } else if (!textPhoneNumber.getText().toString().startsWith("09")) {
                    showError("لطفا شماره موبایل را به صورت صحیح وارد نمایید");
                } else if (textPhoneNumber.getText().length() < 11) {
                    showError("لطفا شماره موبایل را به صورت صحیح وارد نمایید");
                } else if (mListener != null) {
                    int permission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECEIVE_SMS);
                    if (permission != PackageManager.PERMISSION_GRANTED) {
                        if (mListener != null) {
                            mListener.requestPermission(textPhoneNumber.getText().toString());
                        }
                    } else if (mListener != null) {
                        mListener.sendPhoneNumber(textPhoneNumber.getText().toString());
                    }


                }
            }
        });

        btnPhoneNumberBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
                if (mListener != null) {
                    mListener.cancelSendPhoneNumber();
                }
            }
        });

    }

    public void setMessage(String message){
        titleSendPhoneNumber.setText(message);
    }

    public void setPhoneNumber(String phoneNumber){
        textPhoneNumber.setText(phoneNumber);
    }

    public void showError(String errorMessage) {
        if (textPhoneNumberHint != null) {
            textPhoneNumberHint.setText(errorMessage);
            textPhoneNumberHint.setVisibility(View.VISIBLE);
        }
    }


    public void hideError() {
        if (textPhoneNumberHint != null) {
            textPhoneNumberHint.setVisibility(View.GONE);
        }
    }

    public void doShowHasKey(boolean show) {
        textPhoneNumberHasKey.setVisibility(show ? View.VISIBLE : View.GONE);
    }


    public interface interactionPhoneNumber {
        void requestPermission(String phoneNumber);

        void hasKey(String phoneNumber);

        void sendPhoneNumber(String phoneNumber);

        void cancelSendPhoneNumber();

    }
}
