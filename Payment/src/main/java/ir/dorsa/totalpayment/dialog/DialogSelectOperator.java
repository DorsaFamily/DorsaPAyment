package ir.dorsa.totalpayment.dialog;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ir.dorsa.totalpayment.R;


public class DialogSelectOperator extends Dialog {

    private RelativeLayout relEnter;
    private AppCompatImageView imageMci;
    private TextView textMci;
    private AppCompatImageView imageIrancell;
    private TextView textIrancell;

    private LinearLayout lnrSelectOprator;

    private onClick onClick;


    public static final int OPERATOR_NOT_SELECTED=-1;
    public static final int OPERATOR_MCI=1;
    public static final int OPERATOR_IRANCELL=2;
    private  int selectedOperator=OPERATOR_NOT_SELECTED;

    public void setOnclick(onClick onclick){
        this.onClick=onclick;
    }

    public int getSelectedOperator(){
        return selectedOperator;
    }

    public void shakeOperator(){
        ObjectAnimator animMove = ObjectAnimator.ofFloat(lnrSelectOprator, "translationX", 0, 25, -25, 25, -25,15, -15, 6, -6, 0);

        animMove.setDuration(500);
        final AnimatorSet movement = new AnimatorSet();
        movement.play(animMove);
        movement.start();
    }


    public DialogSelectOperator(@NonNull Context context) {
        super(context, android.R.style.Theme_Holo_Dialog_NoActionBar);
        init();

    }

    public DialogSelectOperator(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    protected DialogSelectOperator(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }



    private void init(){
        setContentView(R.layout.payment_dialog_select_operator);
        setCancelable(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().setDimAmount(0.0f);

        relEnter=findViewById(R.id.enter);
        imageMci=findViewById(R.id.image_mci);
        textMci=findViewById(R.id.text_mci);
        imageIrancell=findViewById(R.id.image_irancell);
        textIrancell=findViewById(R.id.text_irancell);

        lnrSelectOprator=findViewById(R.id.lnr_select_operator);


        imageMci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedOperator= OPERATOR_MCI;
                //set irancell color
                imageMci.setBackgroundResource(R.drawable.bgr_circle_blue);
                textMci.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));

                //deSelect mci color
                imageIrancell.setBackgroundResource(R.drawable.bgr_circle_gray);
                textIrancell.setTextColor(ContextCompat.getColor(getContext(), R.color.gray));
            }
        });

        imageIrancell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedOperator= OPERATOR_IRANCELL;
                //set irancell color
                imageIrancell.setBackgroundResource(R.drawable.bgr_circle_yellow);
                textIrancell.setTextColor(ContextCompat.getColor(getContext(), R.color.yellow));

                //deSelect mci color
                imageMci.setBackgroundResource(R.drawable.bgr_circle_gray);
                textMci.setTextColor(ContextCompat.getColor(getContext(), R.color.gray));
            }
        });


        relEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClick != null) {
                    onClick.onClicked();
                }
            }
        });

    }


    public interface onClick{
        void onClicked();
    }
}
