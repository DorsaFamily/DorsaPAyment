package psb.com.testdorsapayment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import ir.dorsa.totalpayment.irancell.IrancellCancel;
import ir.dorsa.totalpayment.payment.Payment;


public class MainActivity extends AppCompatActivity {

    private final int REQUEST_CODE_REGISTER = 123;

    private String appCode = "130";
    private String productCode = "130005";
    private String irancellSku = "bakh960824";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnStartPayment = findViewById(R.id.button);
        Button btnCancelIrancel = findViewById(R.id.button2);

        final Payment payment = new Payment(this);

//        payment.setEnableIrancell(false);// برای عدم قابلیت اشتراک سیم کارت های همراه اول
//        payment.isUserPremium();// بررسی اینکه کاربر قبلا ثبت نام نموده است یا نه



        btnStartPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payment.checkStatus(
                        appCode,
                        productCode,
                        irancellSku,
                        new Payment.onCheckFinished() {
                            @Override
                            public void result(boolean status, String message) {//true فعال می باشد
                                if (status) {//فعال می باشد

                                } else {//غیر فعال می باشد
                                    Intent intentDorsaPayment = payment.getPaymentIntent(
                                            "متن ارسال شماره موبایل",
                                            appCode,
                                            productCode,
                                            irancellSku,
                                            new int[]{R.layout.intri_0, R.layout.intri_1, R.layout.intri_2, R.layout.intri_3}
                                    );
                                    startActivityForResult(intentDorsaPayment, REQUEST_CODE_REGISTER);
                                }
                            }
                        });
            }
        });


        //نمایش یا عدم نمایش دکمه لغو ایرانسل
        if (payment.showCancelSubscribtion()) {
            btnCancelIrancel.setVisibility(View.VISIBLE);
        } else {
            btnCancelIrancel.setVisibility(View.GONE);
        }


        //لغو اشتراک ایرانسل
        btnCancelIrancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final ProgressDialog pDialog = new ProgressDialog(MainActivity.this);
                pDialog.setMessage("در حال انجام درخواست ...");

                pDialog.show();
                payment.cancelIrancell(irancellSku, new IrancellCancel.onIrancellCanceled() {
                    @Override
                    public void resultSuccess() {
                        pDialog.cancel();
                        Toast.makeText(MainActivity.this, "لغو موفقیت آمیز و خروج", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void resultFailed(String msg) {
                        pDialog.cancel();
                        Toast.makeText(MainActivity.this, msg + "اشکال در لغو به دلیل ", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_REGISTER) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "پرداخت موفق", Toast.LENGTH_SHORT).show();
                Payment payment=new Payment(this);
            } else {
                Toast.makeText(this, "اشکال در پرداخت به دلیل " + data.getStringExtra("message"), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
