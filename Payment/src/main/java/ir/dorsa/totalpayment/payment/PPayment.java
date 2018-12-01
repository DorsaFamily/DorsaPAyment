package ir.dorsa.totalpayment.payment;

import android.content.Context;
import android.content.Intent;

import com.android.billingclient.util.IabHelper;
import com.android.billingclient.util.IabResult;
import com.android.billingclient.util.Inventory;
import com.android.billingclient.util.MarketIntentFactorySDK;
import com.android.billingclient.util.Purchase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ir.dorsa.totalpayment.registerInformation.RegisterInfo;
import ir.dorsa.totalpayment.tools.Func;

/**
 * Created by mehdi on 12/28/16.
 */

public class PPayment implements IPPayment {
    protected static final int RC_REQUEST = 12;
    private Context context;
    private IVPayment ivBuy;
    protected MPayment mBuy;
    private IabHelper mHelper;

    private static String base64EncodedPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCZDqoyQQqRhx6ziHo++fDnCdtHPP3lEC8/QfKrdATOIMvTCqKs3Xsdi/lVpf6RzeKhY+n7M6K8qe6vm8cjWC76iNA5lEQOKkgDYaNr/OMqB7UUC+oOFRLMWTnL5a94nSgMXORDlwCr0Jo7wDj10DyvCtcKW9dR6+JLg7tm6cs0YwIDAQAB";
    public static final String access_token = "83caed2d-7110-31b8-be8c-7683728610b8";

    private String SKU_PREMIUM;

    public PPayment(IVPayment ivPayment, String appCode, String productCode, String sku) {
        this.ivBuy = ivPayment;
        context = ivBuy.getContext();
        this.SKU_PREMIUM = sku;
        mBuy = new MPayment(this, appCode, productCode);
    }

    protected String getPhoneNumber() {
        return mBuy.getPhoneNumber();
    }

    protected String getReferenceCode() {
        if (Func.isNumberMci(mBuy.getPhoneNumber())) {
            return mBuy.getReferenceCode();
        } else if (Func.isNumberIrancell(mBuy.getPhoneNumber()) && mBuy.getHasKey()) {
            return mBuy.getReferenceCode();
        }
        return null;
    }

    protected String getIrancellToken() {
        if (Func.isNumberIrancell(mBuy.getPhoneNumber()) && !mBuy.getHasKey()) {
            return mBuy.getIrancellToken();
        }
        return null;
    }

    public String getLocalPhoneNumber() {
        return mBuy.getLocalPhoneNumber();
    }

    protected boolean isRegistered() {
        return mBuy.isRegistered();
    }

    protected boolean isExpired() {
        boolean result = false;
        try {
            String inputFormat = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat df_input = new SimpleDateFormat(inputFormat);
            Calendar cal = Calendar.getInstance();
            Date parsedDate = df_input.parse(mBuy.getExpiredDate());
            cal.setTime(parsedDate);
            Calendar calNow = Calendar.getInstance();

            result = (calNow.getTimeInMillis() < cal.getTimeInMillis() ? false : true);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    protected void doSendPhoneNumber(String phoneNumber) {
        phoneNumber = phoneNumber
                .replace("۰", "0")
                .replace("۱", "1")
                .replace("۲", "2")
                .replace("۳", "3")
                .replace("۴", "4")
                .replace("۵", "5")
                .replace("۶", "6")
                .replace("۷", "7")
                .replace("۸", "8")
                .replace("۹", "9");
        if (phoneNumber.length() == 0) {
            ivBuy.onFailedSendPhoneNumber("شماره تلفن همراه را وارد نمایید");
        } else if (phoneNumber.length() < 11) {
            ivBuy.onFailedSendPhoneNumber("شماره تلفن همراه را کامل وارد نمایید");
        } else if (!phoneNumber.startsWith("09")) {
            ivBuy.onFailedSendPhoneNumber("شماره تلفن همراه را صحیح وارد نمایید");
        } else {
            if (Func.isNumberMci(phoneNumber)) {
                ivBuy.onStartSendPhoneNumber();
                mBuy.atemptSendPhoneNumber(phoneNumber);
            } else if (Func.isNumberIrancell(phoneNumber) && mBuy.isEnableIrancell()) {
                if (SKU_PREMIUM != null) {
                    ivBuy.onStartSendPhoneNumber();
                    startIrancellPayment(phoneNumber);
                } else {
                    ivBuy.onFailedSendPhoneNumber("در حال حاضر این شماره پشتیبانی نمی شود");
                }
            } else {
                ivBuy.onFailedSendPhoneNumber("در حال حاضر این شماره پشتیبانی نمی شود");
            }
        }
    }

    protected void doSendKey(String key) {
        key = key
                .replace("۰", "0")
                .replace("۱", "1")
                .replace("۲", "2")
                .replace("۳", "3")
                .replace("۴", "4")
                .replace("۵", "5")
                .replace("۶", "6")
                .replace("۷", "7")
                .replace("۸", "8")
                .replace("۹", "9");
        if (key.length() == 0) {
            ivBuy.onFailedSendKey("کد دریافتی را وارد نمایید");
        } else {
            ivBuy.onStartSendKey();
            mBuy.doSendKey(key);
        }
    }

    protected void checkStatus() {

        if (mBuy.getPhoneNumber().isEmpty()) {
            onFailedCheckStatus(1, "empty phone number");
            return;
        }
        ivBuy.onStartCheckStatus();

        if (Func.isNumberMci(mBuy.getPhoneNumber())) {
            mBuy.checkStatus();
        } else if (Func.isNumberIrancell(mBuy.getPhoneNumber()) && mBuy.getHasKey()) {
            mBuy.checkStatus();
        } else {
            checkIrancellStatus();
        }
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public void onSuccessSendPhoneNumber() {
        ivBuy.onSuccessSendPhoneNumber();
    }

    @Override
    public void onFailedSendPhoneNumber(String errorMessage) {
        ivBuy.onFailedSendPhoneNumber(errorMessage);
    }

    @Override
    public void onFailedSendKey(String errorMessage) {
        ivBuy.onFailedSendKey(errorMessage);
    }

    @Override
    public void onSuccessSubscribe(String expiredDate) {
        ivBuy.onSuccessSubscribe(expiredDate);
    }

    @Override
    public void onFailedSubscribe(String errorCode) {
        if (ivBuy != null) {
            ivBuy.onFailedSubscribe(errorCode);
        }
    }

    @Override
    public void onFailedCheckStatus(int errorCode, String errorMessage) {
        if (ivBuy != null) {
            ivBuy.onFailedCheckStatus(errorCode, errorMessage);
        }
    }

    @Override
    public void onSuccessCheckStatus() {
        if (ivBuy != null) {
            ivBuy.onSuccessCheckStatus();
        }
    }

    private void checkIrancellStatus() {
        if (mHelper == null) {

            mHelper = new IabHelper(getContext(), base64EncodedPublicKey, new MarketIntentFactorySDK(true));

            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                public void onIabSetupFinished(IabResult result) {
                    if (!result.isSuccess()) {
                        // Oh noes, there was a problem.
                        mBuy.clearUserInfo();
                        ivBuy.onFailedCheckStatus(1, "اشکال در بررسی شارژینگ");
                        return;
                    }

                    // Have we been disposed of in the meantime? If so, quit.
                    if (mHelper == null) {
                        ivBuy.onFailedCheckStatus(1, "اشکال در بررسی شارژینگ");
                        return;
                    }
                    checkMhelperIrancellStatus();

                }
            });
        } else {
            checkMhelperIrancellStatus();
        }
    }

    private void checkMhelperIrancellStatus() {
        try {
            mHelper.queryInventoryAsync(new IabHelper.QueryInventoryFinishedListener() {
                @Override
                public void onQueryInventoryFinished(IabResult iabResult, Inventory inventory) {
                    if (mHelper == null) {
                        ivBuy.onFailedCheckStatus(1, "اشکال در بررسی شارژینگ");
                        return;
                    }

                    // Is it a failure?
                    if (iabResult.isFailure()) {
                        ivBuy.onFailedCheckStatus(1, "اشکال در بررسی شارژینگ");
                        return;
                    }

                    Purchase premiumPurchase = inventory.getPurchase(SKU_PREMIUM);

                    boolean mIsPremium = (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase.getDeveloperPayload()));
                    if (mIsPremium) {
                        ivBuy.onSuccessCheckStatus();
                    } else {
                        mBuy.clearUserInfo();
                        ivBuy.onFailedCheckStatus(1, "اشکال در ارسال شماره موبایل");
                    }
                }
            });
        } catch (IabHelper.IabAsyncInProgressException e) {
            ivBuy.onFailedpurchaseIrancell("اشکال در ارسال شماره موبایل");
        }
    }

    public String getMarketingId() {
        return mBuy.getMarketingId();
    }

    public void setMarketingId(String marketingId) {
        mBuy.setMarketingId(marketingId);
    }

    protected boolean savePhoneNumber(String phoneNumber) {
        phoneNumber = phoneNumber
                .replace("۰", "0")
                .replace("۱", "1")
                .replace("۲", "2")
                .replace("۳", "3")
                .replace("۴", "4")
                .replace("۵", "5")
                .replace("۶", "6")
                .replace("۷", "7")
                .replace("۸", "8")
                .replace("۹", "9");
        if (phoneNumber.length() == 0) {
            ivBuy.onFailedSendPhoneNumber("شماره تلفن همراه را وارد نمایید");
        } else if (phoneNumber.length() < 11) {
            ivBuy.onFailedSendPhoneNumber("شماره تلفن همراه را کامل وارد نمایید");
        } else if (!phoneNumber.startsWith("09")) {
            ivBuy.onFailedSendPhoneNumber("شماره تلفن همراه را صحیح وارد نمایید");
        } else {
            if (Func.isNumberMci(phoneNumber)) {
                mBuy.savePhoneNumber(phoneNumber);
                return true;
            } else if (Func.isNumberIrancell(phoneNumber) && mBuy.isEnableIrancell()) {
                if (SKU_PREMIUM != null) {
                    mBuy.savePhoneNumber(phoneNumber);
                    return true;
                } else {
                    ivBuy.onFailedSendPhoneNumber("در حال حاضر این شماره پشتیبانی نمی شود");
                }
            } else {
                ivBuy.onFailedSendPhoneNumber("در حال حاضر این شماره پشتیبانی نمی شود");
            }
        }
        return false;

    }

    protected boolean checkPhoneNumber(String phoneNumber) {
        phoneNumber = phoneNumber
                .replace("۰", "0")
                .replace("۱", "1")
                .replace("۲", "2")
                .replace("۳", "3")
                .replace("۴", "4")
                .replace("۵", "5")
                .replace("۶", "6")
                .replace("۷", "7")
                .replace("۸", "8")
                .replace("۹", "9");
        if (phoneNumber.length() == 0) {
            ivBuy.onFailedSendPhoneNumber("شماره تلفن همراه را وارد نمایید");
        } else if (phoneNumber.length() < 11) {
            ivBuy.onFailedSendPhoneNumber("شماره تلفن همراه را کامل وارد نمایید");
        } else if (!phoneNumber.startsWith("09")) {
            ivBuy.onFailedSendPhoneNumber("شماره تلفن همراه را صحیح وارد نمایید");
        } else {
            if (Func.isNumberMci(phoneNumber)) {
                return true;
            } else if (Func.isNumberIrancell(phoneNumber) && mBuy.isEnableIrancell()) {
                if (SKU_PREMIUM != null) {
                    return true;
                } else {
                    ivBuy.onFailedSendPhoneNumber("در حال حاضر این شماره پشتیبانی نمی شود");
                }
            } else {
                ivBuy.onFailedSendPhoneNumber("در حال حاضر این شماره پشتیبانی نمی شود");
            }
        }
        return false;

    }

    public void clearUserInfo() {
        mBuy.clearUserInfo();
    }

    public void setHasKey(boolean hasKey) {
        mBuy.setHasKey(hasKey);
    }

    public void setPhoneNumber(String phoneNumber) {
        mBuy.setPhoneNumber(phoneNumber);
    }

    public boolean getHasKey() {
        return mBuy.getHasKey();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Irancell payument
    ///////////////////////////////////////////////////////////////////////////
    private void startIrancellPayment(final String phoneNumber) {

        ivBuy.onStartPurchaseIrancell();

        if (mHelper != null) {
            try {
                mHelper.dispose();
                mHelper = null;
            } catch (IabHelper.IabAsyncInProgressException e) {
                e.printStackTrace();
            }
        }

        mHelper = new IabHelper(getContext(), base64EncodedPublicKey, new MarketIntentFactorySDK(true));

        Intent fillInIntent = new Intent();
        fillInIntent.putExtra("msisdn", phoneNumber);
        fillInIntent.putExtra("editAble", false);
        fillInIntent.putExtra("delayPayment", 2);
        mHelper.setFillInIntent(fillInIntent);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    ivBuy.onFailedpurchaseIrancell("1اشکال در ارسال شماره موبایل");
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) {
                    ivBuy.onFailedpurchaseIrancell("2اشکال در ارسال شماره موبایل");
                    return;
                }

                try {
                    mHelper.queryInventoryAsync(new IabHelper.QueryInventoryFinishedListener() {
                        @Override
                        public void onQueryInventoryFinished(IabResult iabResult, Inventory inventory) {

                            if (mHelper == null) {
                                ivBuy.onFailedpurchaseIrancell("3اشکال در ارسال شماره موبایل");
                                return;
                            }


                            // Is it a failure?
                            if (iabResult.isFailure() && iabResult.getResponse() != 6 && iabResult.getResponse() != 0) {
                                ivBuy.onFailedpurchaseIrancell(iabResult.getMessage());
                                return;
                            }

                            Purchase premiumPurchase = inventory.getPurchase(SKU_PREMIUM);


                            boolean mIsPremium = (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase.getDeveloperPayload()));


                            if (mIsPremium) {
                                mBuy.savePhoneNumber(phoneNumber);
                                new RegisterInfo(getContext()).active(phoneNumber);
                                ivBuy.onSuccessPurchaseIrancell("با تشکر از شما \nسرویس شما در تاریخ " + mBuy.getPersianDate() + " فعال گردید");
                            } else {
                                try {
                                    mHelper.launchPurchaseFlow(ivBuy.getFragment(), SKU_PREMIUM, RC_REQUEST,
                                            new IabHelper.OnIabPurchaseFinishedListener() {
                                                @Override
                                                public void onIabPurchaseFinished(IabResult iabResult, Purchase purchase) {

                                                    // if we were disposed of in the meantime, quit.
                                                    if (mHelper == null) {
                                                        ivBuy.onFailedpurchaseIrancell("اشکال در ارسال شماره موبایل");
                                                        return;
                                                    }

                                                    if (iabResult.isFailure()) {
                                                        if (iabResult.getResponse() == -1005) {
                                                            ivBuy.onFailedpurchaseIrancell("لغو شد");
                                                        } else if (iabResult.getResponse() == 2) {
                                                            ivBuy.onFailedpurchaseIrancell("سرویس در دسترس نیست");
                                                        } else if (iabResult.getResponse() == 3) {
                                                            ivBuy.onFailedpurchaseIrancell("فروشگاه این درخواست را پشتیبانی نمی نماید");
                                                        } else if (iabResult.getResponse() == 4) {
                                                            ivBuy.onFailedpurchaseIrancell("این محصول قابل فروش نیست");
                                                        } else {
                                                            ivBuy.onFailedpurchaseIrancell("اشکال در ارسال شماره موبایل");
                                                        }
                                                        return;
                                                    }
                                                    if (!verifyDeveloperPayload(purchase.getDeveloperPayload())) {
                                                        ivBuy.onFailedpurchaseIrancell("اشکال در ارسال شماره موبایل");
                                                        return;
                                                    }

                                                    if (purchase.getSku().equals(SKU_PREMIUM)) {
                                                        mBuy.savePhoneNumber(phoneNumber);
                                                        new RegisterInfo(getContext()).active(phoneNumber);
                                                        mBuy.saveBuyDetails();
                                                        ivBuy.onSuccessPurchaseIrancell("با تشکر از شما \nسرویس شما در تاریخ " + mBuy.getPersianDate() + " فعال گردید");
                                                    }
                                                }
                                            }, getPlayLoad());
                                } catch (IabHelper.IabAsyncInProgressException e) {
                                    ivBuy.onFailedpurchaseIrancell("اشکال در ارسال شماره موبایل");
                                }
                            }
                        }
                    });
                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                    ivBuy.onFailedpurchaseIrancell("اشکال در ارسال شماره موبایل");
                }
            }
        });
    }

    public boolean checkActivityResult(int requestCode, int resultCode, Intent data) {
        if (mHelper == null) return false;
        return mHelper.handleActivityResult(requestCode, resultCode, data);
    }

    protected void disposeHelper() {
        if (mHelper != null) {
            mHelper.disposeWhenFinished();
            mHelper = null;
        }
    }

    private String getPlayLoad() {
        return "ABCD";
    }

    private boolean verifyDeveloperPayload(String playLoad) {
        return playLoad.equals("ABCD");
    }
}