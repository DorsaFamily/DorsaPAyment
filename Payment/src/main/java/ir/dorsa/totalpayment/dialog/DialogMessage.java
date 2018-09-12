package ir.dorsa.totalpayment.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ir.dorsa.totalpayment.R;


public class DialogMessage extends Dialog {

    private ClickListnerPosetive clickListnerPosetive;
    private ClickListenerNegative clickListenerNegative;
    private TextView textMessage;
    private Button btnOk;
    private Button btnCancel;

    public DialogMessage(@NonNull Context context) {
        super(context, android.R.style.Theme_Holo_Dialog_NoActionBar);
        init();
    }

    public DialogMessage(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    protected DialogMessage(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }


    public void setClickListnerPosetive(ClickListnerPosetive clickListnerPosetive) {
        this.clickListnerPosetive = clickListnerPosetive;
    }

    public void setClickListenerNegative(ClickListenerNegative clickListenerNegative) {
        this.clickListenerNegative = clickListenerNegative;
        if (clickListenerNegative != null) {
            btnCancel.setVisibility(View.VISIBLE);
        }
    }

    private void init(){
        setContentView(R.layout.dialog_msg);
        setCancelable(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().setDimAmount(0.0f);

        textMessage=findViewById(R.id.textView24);
        btnOk=findViewById(R.id.button3);
        btnCancel=findViewById(R.id.button4);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickListnerPosetive != null) {
                    clickListnerPosetive.onClick();
                }
            }
        });


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickListenerNegative != null) {
                    clickListenerNegative.onClick();
                }
            }
        });
    }

    public void setTextButtonOk(String text){
        btnOk.setText(text);
    }

    public void setTextButtonCancel(String msg){
        btnCancel.setText(msg);
    }

    public void setMessage(String message){
        textMessage.setText(message);
    }


    public interface ClickListnerPosetive {
        void onClick();
    }

    public interface ClickListenerNegative{
        void onClick();
    }

}
