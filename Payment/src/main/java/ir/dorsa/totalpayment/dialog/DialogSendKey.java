package ir.dorsa.totalpayment.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import ir.dorsa.totalpayment.R;


public class DialogSendKey extends Fragment {

    public static final String TAG_FRG_SEND_KEY = "TAG_FRG_SEND_KEY";

    private TextView textKeyChangeNumber;
    private TextView textKeyTitle;
    private TextView textError;
    private EditText textKeyKey;
    private TextView textDescSendKey;
    private View btnKeySend;
    private View btnKeyCancel;

    private interactionSendKey mListener;

    private String phoneNumber;
    private String message;
    private String error;


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
        }
    }

    public void applyMessage() {
        if (textDescSendKey != null) {
            textDescSendKey.setText(message);
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface interactionSendKey {
        void changePhoneNumber();

        void doSendKey(String key);

        void cancelSendKey();
    }
}
