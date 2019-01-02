package ir.dorsa.totalpayment.dialog;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import ir.dorsa.totalpayment.R;


public class DialogSendPhoneNumber extends Fragment {

    public static final String TAG_FRG_SEND_PHONE_NUMBER="TAG_FRG_SEND_PHONE_NUMBER";

    private static final String KEY_MESSAGE = "KEY_MESSAGE";
    private static final String KEY_PHONE_NUMBER = "KEY_PHONE_NUMBER";
    private static final String KEY_ERROR_MESSAGE = "KEY_ERROR_MESSAGE";
    private static final String KEY_HAS_KEY = "KEY_HAS_KEY";
    private interactionPhoneNumber mListener;

    private EditText textPhoneNumber;
    private TextView textPhoneNumberHint;
    private TextView textPhoneNumberHasKey;

    private TextView titleSendPhoneNumber;

    private View btnPhoneNumberAcept;
    private View btnPhoneNumberBack;

    private View pView;

    private String message = "";
    private String phoneNumber = "";
    private String error = "";
    private boolean hasKey = false;


    public DialogSendPhoneNumber getInstance(
            String message,
            String phoneNumber,
            String errorMessage,
            boolean hasKey
    ) {
        DialogSendPhoneNumber dialogSendPhoneNumber = new DialogSendPhoneNumber();
        Bundle args = new Bundle();
        args.putString(KEY_MESSAGE, message);
        args.putString(KEY_PHONE_NUMBER, phoneNumber);
        args.putString(KEY_ERROR_MESSAGE, errorMessage);
        args.putBoolean(KEY_HAS_KEY, hasKey);
        dialogSendPhoneNumber.setArguments(args);

        return dialogSendPhoneNumber;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            message = savedInstanceState.getString(KEY_MESSAGE, "");
            phoneNumber = savedInstanceState.getString(KEY_PHONE_NUMBER, "");
            error = savedInstanceState.getString(KEY_ERROR_MESSAGE, "");
            hasKey = savedInstanceState.getBoolean(KEY_ERROR_MESSAGE, false);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        pView = inflater.inflate(R.layout.payment_dialog_register_phone, container, false);
        init();
        return pView;
    }


    public void setListener(interactionPhoneNumber mListener) {
        this.mListener = mListener;
    }




    private void init() {

        textPhoneNumber = pView.findViewById(R.id.dialog_register_phone_number);
        textPhoneNumberHint = pView.findViewById(R.id.dialog_register_hint);
        textPhoneNumberHasKey = pView.findViewById(R.id.dialog_register_has_key);

        btnPhoneNumberAcept = pView.findViewById(R.id.dialog_register_btn_send);
        btnPhoneNumberBack = pView.findViewById(R.id.dialog_register_btn_cancel);

        titleSendPhoneNumber = pView.findViewById(R.id.title_register);

        refreshViews();


        textPhoneNumberHasKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textPhoneNumber.getText().toString().length() == 0) {
                    showError("لطفا شماره تماس را وارد نمایید");
                } else if (!textPhoneNumber.getText().toString().startsWith("09")) {
                    showError("لطفا شماره موبایل را به صورت صحیح وارد نمایید");
                } else if (textPhoneNumber.getText().length() < 11) {
                    showError("لطفا شماره موبایل را به صورت صحیح وارد نمایید");
                } else if (mListener != null) {
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
                if (mListener != null) {
                    mListener.cancelSendPhoneNumber();
                }
            }
        });

    }





    public void refreshViews() {
        applyMessage();
        applyPhoneNumber(this.phoneNumber);
        applyError(error);
        applyHasKey(hasKey);

    }

    private void applyMessage() {
        if (titleSendPhoneNumber != null) {
            titleSendPhoneNumber.setText(this.message);
        }
    }

    private void applyPhoneNumber(String phoneNumber) {
        if (textPhoneNumber != null) {
            textPhoneNumber.setText(phoneNumber);
        }
    }

    private void applyError(String error) {
        if (error != null && !error.isEmpty()) {
            showError(error);
        } else {
            hideError();
        }
    }


    private void applyHasKey(boolean show) {
        if (textPhoneNumberHasKey != null) {
            textPhoneNumberHasKey.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }


    public void setMessage(String message) {
        this.message = message;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setHasKey(boolean hasKey) {
        this.hasKey = hasKey;
    }

    private void showError(String errorMessage) {
        if (textPhoneNumberHint != null) {
            textPhoneNumberHint.setText(errorMessage);
            textPhoneNumberHint.setVisibility(View.VISIBLE);
        }
    }


    private void hideError() {
        if (textPhoneNumberHint != null) {
            textPhoneNumberHint.setVisibility(View.GONE);
        }
    }


    public interface interactionPhoneNumber {
        void requestPermission(String phoneNumber);

        void hasKey(String phoneNumber);

        void sendPhoneNumber(String phoneNumber);

        void cancelSendPhoneNumber();

    }
}
