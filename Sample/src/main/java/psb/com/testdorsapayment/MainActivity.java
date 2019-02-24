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

    public static final String appCode="1234";
    public static final String productCode="1234";
    public static final String irancellSku="1234";
    private String MarketingId = "12345";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button btnCancelIrancel = findViewById(R.id.button2);

        final Payment payment = new Payment(this);


//        payment.setMarketId(MarketingId);//در صورتی که خروجی برای مارکتینگ می باشد از این دستور استفاده گردد

//        payment.setEnableIrancell(false);// فعال یا غیر فعال کردن پرداخت برای سیم کارت ایرانسل
//        payment.isUserPremium();// بررسی اینکه کاربر قبلا ثبت نام نموده است یا نه

//        String userPhoneNumber=payment.getPhoneNumber(); //دریافت شماره موبایل کاربر

        payment.checkStatus(
                appCode,
                productCode,
                irancellSku,
                new Payment.onCheckFinished() {
                    @Override
                    public void result(boolean status, int errorCode, String message) {//true فعال می باشد

                        if (status) {//فعال می باشد
                            //نمایش یا عدم نمایش دکمه لغو ایرانسل
                            if (payment.showCancelSubscribtion()) {
                                btnCancelIrancel.setVisibility(View.VISIBLE);
                            } else {
                                btnCancelIrancel.setVisibility(View.GONE);
                            }
                        } else {//غیر فعال می باشد
//                        if(errorCode==Payment.ERROR_CODE_USER_NOT_REGISTERED)  // کاربر ثبت نام ننموده است
//                        if(errorCode == Payment.ERROR_CODE_USER_HAS_NO_CHARGE)   // شماره کاربر دارای شارژ نمی باشد
//                        if(errorCode==Payment.ERROR_CODE_INTERNET_CONNECTION)  // کاربر در اتصال به اینترنت مشکل دارد
//
//                            if (errorCode == Payment.ERROR_CODE_INTERNET_CONNECTION) {
                                Intent intentDorsaPayment = payment.getPaymentIntent(
                                        300,//mci daily price
                                        200,//irancell daily price
                                        //false,// if you want be full screen
                                        appCode,
                                        productCode,
                                        irancellSku,
                                        new int[]{R.layout.intri_0, R.layout.intri_1, R.layout.intri_2, R.layout.intri_3}
                                );
                                startActivityForResult(intentDorsaPayment, REQUEST_CODE_REGISTER);
//                            }
                        }
                    }
                });


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
                        finish();
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
            } else {
                Toast.makeText(this, "اشکال در پرداخت به دلیل " + data.getStringExtra("message"), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
