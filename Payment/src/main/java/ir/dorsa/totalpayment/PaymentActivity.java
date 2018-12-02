package ir.dorsa.totalpayment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import ir.dorsa.totalpayment.intro.FragmentIntro;
import ir.dorsa.totalpayment.payment.FragmentCheckStatus;
import ir.dorsa.totalpayment.payment.FragmentPayment;
import ir.dorsa.totalpayment.payment.Payment;
import ir.dorsa.totalpayment.toolbarHandler.ToolbarHandler;

import static ir.dorsa.totalpayment.payment.Payment.KEY_APP_CODE;
import static ir.dorsa.totalpayment.payment.Payment.KEY_MARKET_ID;
import static ir.dorsa.totalpayment.payment.Payment.KEY_MESSAGE;
import static ir.dorsa.totalpayment.payment.Payment.KEY_PRODUCT_CODE;
import static ir.dorsa.totalpayment.payment.Payment.KEY_SKU;
import static ir.dorsa.totalpayment.payment.Payment.KEY_SPLASH;
import static ir.dorsa.totalpayment.payment.Payment.KEY_TEXT_SEND_PHONE_NUMBER;

public class PaymentActivity extends AppCompatActivity implements
        FragmentCheckStatus.OnCheckStatus,
        FragmentIntro.interaction,
        FragmentPayment.interactionPayment {


    private static final String KEY_FRG_CHECK_STATUS = "KEY_FRG_CHECK_STATUS";
    private static final String KEY_FRG_INTRO = "KEY_FRG_INTRO";
    private static final String KEY_FRG_PAYMENT = "KEY_FRG_PAYMENT";

    private String textSendPhoneNumber;
    private String paymentAppCode;
    private String paymentProductCode;
    private String paymentIrancellSku;
    private int[] splashLayoutResource;
    private String marketId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_activity_payment);

        if (getIntent().getBooleanExtra(Payment.KEY_IS_LANDSCAP, false)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }


        if (getIntent().getBooleanExtra(Payment.KEY_IS_FULLSCREEN, false)) {
            makeFullScreen(getWindow());
            makeTansluteNavigation(this, getWindow(), getWindow().getDecorView());
            createHelperWnd();
        }


        new ToolbarHandler().makeTansluteToolbar(this, getWindow(), getWindow().getDecorView());


       /* try {
            CharkhoneSdkApp.initSdk(getApplicationContext(), Utils.getSecrets(this));
        } catch (Exception ex) {

        }*/

        try {

            textSendPhoneNumber = getIntent().getExtras().getString(KEY_TEXT_SEND_PHONE_NUMBER);
            paymentAppCode = getIntent().getExtras().getString(KEY_APP_CODE);
            paymentProductCode = getIntent().getExtras().getString(KEY_PRODUCT_CODE);
            paymentIrancellSku = getIntent().getExtras().getString(KEY_SKU);

            splashLayoutResource = getIntent().getExtras().getIntArray(KEY_SPLASH);

            marketId = getIntent().getExtras().getString(KEY_MARKET_ID);

            if (textSendPhoneNumber == null || paymentAppCode == null || paymentProductCode == null) {
                onExit("مقادیر ناقص می باشد");
                return;
            }

            Payment payment = new Payment(this);
            if (payment.getPhoneNumber() == null && payment.getPhoneNumber().isEmpty()) {
                startPaymentFlow(true);
            } else {

                new ToolbarHandler().makeTansluteToolbar(this, getWindow(), getWindow().getDecorView());
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_fragment,
                                new FragmentCheckStatus().newInstance(
                                        paymentProductCode,
                                        paymentAppCode,
                                        paymentIrancellSku
                                ), KEY_FRG_CHECK_STATUS).commit();
            }


        } catch (Exception ex) {
            ex.printStackTrace();
            Intent returnIntent = new Intent();
            returnIntent.putExtra("message", "ایراد ورود اطلاعات");
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
        }
    }

    @Override
    public void onSuccessSubscribe() {
        setResult(Activity.RESULT_OK);
        finish();
    }

    @Override
    public void onFailedSubscribe() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("message", "مشکل فعال سازی");
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("message", "مشکل فعال سازی");
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    @Override
    public void onEnterSelected() {
        startPaymentFlow(false);
    }


    @Override
    public void startPaymentFlow(boolean showIntro) {
        if (showIntro && splashLayoutResource != null && splashLayoutResource.length > 0) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_fragment, new FragmentIntro().newInstance(splashLayoutResource), KEY_FRG_INTRO).commit();
        } else {
            new ToolbarHandler().makeTansluteToolbar(this, getWindow(), getWindow().getDecorView());
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_fragment,
                    new FragmentPayment().newInstance(
                            textSendPhoneNumber,
                            paymentProductCode,
                            paymentAppCode,
                            paymentIrancellSku,
                            marketId
                    ), KEY_FRG_PAYMENT).commit();
        }
    }


    @Override
    public void onExit(String message) {
        Intent intent = new Intent();
        intent.putExtra(KEY_MESSAGE, message);
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    @Override
    public void alreadySubscribed() {
        onSuccessSubscribe();
    }


    ///////////////////////////////////////////////////////////////////////////
    // make activity transluted
    ///////////////////////////////////////////////////////////////////////////

    public static void makeFullScreen(Window window) {
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    public static void makeTansluteNavigation(Context context, Window w, View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            w.setStatusBarColor(ContextCompat.getColor(context, android.R.color.transparent));
        }
    }


    public void createHelperWnd() {
        final ViewGroup rootView = findViewById(android.R.id.content);
        final WindowManager.LayoutParams p = new WindowManager.LayoutParams();
        p.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        p.gravity = Gravity.RIGHT | Gravity.TOP;
        p.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        p.width = 1;
        p.height = RelativeLayout.LayoutParams.MATCH_PARENT;
        p.format = PixelFormat.TRANSPARENT;
        final View helperWnd = new View(this); //View helperWnd;

        rootView.addView(helperWnd, p);
        final ViewTreeObserver vto = helperWnd.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                if (isStatusBarVisible()) {
                    makeFullScreen(getWindow());
                }
            }
        });

    }

    public boolean isStatusBarVisible() {
        Rect rectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        int statusBarHeight = rectangle.top;
        return statusBarHeight != 0;
    }

    public boolean isNavigationBarVisible() {
        Rect rectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        int statusBarHeight = rectangle.right;
        return statusBarHeight != 0;
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent().getBooleanExtra(Payment.KEY_IS_FULLSCREEN, false)) {
            createHelperWnd();
        }
    }



}
