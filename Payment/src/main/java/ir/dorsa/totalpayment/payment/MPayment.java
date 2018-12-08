package ir.dorsa.totalpayment.payment;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import ir.dorsa.totalpayment.BuildConfig;
import ir.dorsa.totalpayment.payment.models.ResponseAuthentication;
import ir.dorsa.totalpayment.payment.models.ResponseAuthenticationRequest;
import ir.dorsa.totalpayment.payment.models.ResponseSubscribeSecend;
import ir.dorsa.totalpayment.payment.models.ResponseVerifyAuthentication;
import ir.dorsa.totalpayment.registerInformation.RegisterInfo;
import ir.dorsa.totalpayment.tools.CalTool;
import ir.dorsa.totalpayment.tools.Utils;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

import static android.content.ContentValues.TAG;

/**
 * Created by mehdi on 12/28/16.
 */

public class MPayment implements IMPayment {
    private Context context;
    private IPPayment ipBuy;
    private String accessToke = "";
    private Retrofit clientRetrofit;
    private String productCode = "";
    private String appCode;
    private static final int expiredDays = 7;
    private String phoneNumber;
    private String marketingId;

    public MPayment(IPPayment ipBuy, String appCode, String productCode) {
        this.ipBuy = ipBuy;
        this.appCode = appCode;
        this.productCode = productCode;

        setContext(ipBuy.getContext());

        setContext(ipBuy.getContext());


        final OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(50, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                ;


        if(BuildConfig.DEBUG){
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            okHttpClientBuilder.addInterceptor(loggingInterceptor);
        }

        final OkHttpClient okHttpClient=okHttpClientBuilder.build();

        if (clientRetrofit == null) {
            clientRetrofit = new Retrofit.Builder()
                    .baseUrl("http://79.175.155.135:9090/api/v2/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public Context getContext() {
        return this.context;
    }

    public String getPhoneNumber() {
        SharedPreferences sharedPrefrece = context.getSharedPreferences(SH_P_BUY_IN_APP, getContext().MODE_PRIVATE);
        return sharedPrefrece.getString(SH_P_BUY_IN_APP_PHONE_NUMBER, "");
    }

    protected String getReferenceCode(){
        return Utils.getStringPreference(getContext(), "reference_code", "reference_code", "");
    }

    protected String getIrancellToken(){
        return Utils.getStringPreference(context,Utils.PURCHASETOKEN, Utils.PURCHASETOKENKEY,"");
    }

    public String getLocalPhoneNumber() {
        return phoneNumber;
    }

    public String getExpiredDate() {
        SharedPreferences sharedPrefrece = context.getSharedPreferences(SH_P_BUY_IN_APP, context.MODE_PRIVATE);
        return sharedPrefrece.getString(SH_P_BUY_IN_APP_DATE, "0000-00-00 00:00:00");
    }

    public String getMarketingId() {
        return marketingId;
    }

    public void setMarketingId(String marketingId) {
        this.marketingId = marketingId;
    }

    private String getAccessToken() {
        SharedPreferences sharedPrefrece = context.getSharedPreferences(SH_P_BUY_IN_APP, context.MODE_PRIVATE);
        return sharedPrefrece.getString(SH_P_BUY_IN_APP_ACCESS_TOKEN, "0");
    }

    public boolean isRegistered() {
        SharedPreferences sharedPrefrece = context.getSharedPreferences(SH_P_BUY_IN_APP, context.MODE_PRIVATE);
        return sharedPrefrece.getBoolean(SH_P_BUY_IN_APP_REGISTERED, false);

    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void savePhoneNumber(String phoneNumber) {


        SharedPreferences sharedPrefrece = getContext().getSharedPreferences(SH_P_BUY_IN_APP, getContext().MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefereceEditor = sharedPrefrece.edit();
        sharedPrefereceEditor.putString(SH_P_BUY_IN_APP_PHONE_NUMBER, phoneNumber);
        sharedPrefereceEditor.commit();
    }

    public void saveBuyDetails() {
        SharedPreferences sharedPrefrece = getContext().getSharedPreferences(SH_P_BUY_IN_APP, getContext().MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefereceEditor = sharedPrefrece.edit();
        sharedPrefereceEditor.putString(SH_P_BUY_IN_APP_ACCESS_TOKEN, accessToke);

        sharedPrefereceEditor.putBoolean(SH_P_BUY_IN_APP_REGISTERED, true);

        sharedPrefereceEditor.putString(SH_P_BUY_IN_APP_DATE, getDate());

        sharedPrefereceEditor.commit();
    }

    public boolean isEnableIrancell() {
        SharedPreferences sharedPrefrece = getContext().getSharedPreferences(SH_P_BUY_IN_APP, context.MODE_PRIVATE);
        return sharedPrefrece.getBoolean(SH_P_BUY_IN_APP_ENABLE_IRANCELL, true);
    }

    public void clearUserInfo() {
        new RegisterInfo(getContext()).deactive(getPhoneNumber());

        SharedPreferences sharedPrefrece = getContext().getSharedPreferences(SH_P_BUY_IN_APP, getContext().MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefereceEditor = sharedPrefrece.edit();
        sharedPrefereceEditor.putString(SH_P_BUY_IN_APP_ACCESS_TOKEN, null);
        sharedPrefereceEditor.putString(SH_P_BUY_IN_APP_PHONE_NUMBER, "");
        sharedPrefereceEditor.putBoolean(SH_P_BUY_IN_APP_REGISTERED, false);

        sharedPrefereceEditor.putString(SH_P_BUY_IN_APP_DATE, "");

        sharedPrefereceEditor.commit();
    }

    public void setHasKey(boolean hasKey) {

        SharedPreferences sharedPrefrece = getContext().getSharedPreferences(SH_P_BUY_IN_APP, getContext().MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefereceEditor = sharedPrefrece.edit();
        sharedPrefereceEditor.putBoolean(SH_P_BUY_IN_APP_HAS_KEY, hasKey);

        sharedPrefereceEditor.commit();
    }

    public boolean getHasKey() {
        SharedPreferences sharedPrefrece = context.getSharedPreferences(SH_P_BUY_IN_APP, context.MODE_PRIVATE);
        return sharedPrefrece.getBoolean(SH_P_BUY_IN_APP_HAS_KEY, false);
    }

    private String getDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, expiredDays);
//        cal.add(Calendar.MINUTE, 2);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(cal.getTime());
    }

    public String getPersianDate() {
        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.DAY_OF_MONTH,expiredDays);
        cal.add(Calendar.MINUTE, 2);
        CalTool perCal = new CalTool(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
        return perCal.getIranianDate() + " ساعت " + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE);
    }

    public void atemptSendPhoneNumber(final String phoneNumber) {

        interfaceSendPhoneNumber apiService = clientRetrofit.create(interfaceSendPhoneNumber.class);
        Call<ResponseAuthenticationRequest> call = apiService.deviceInfo(phoneNumber, appCode, productCode);
        call.enqueue(new Callback<ResponseAuthenticationRequest>() {
            @Override
            public void onResponse(Call<ResponseAuthenticationRequest> call, Response<ResponseAuthenticationRequest> response) {
                if (response.code() == 200) {
                    if (response.body().getStatus().equals("0")) {
                        MPayment.this.phoneNumber = phoneNumber;
                        ipBuy.onSuccessSendPhoneNumber();
                    } else {
                        ipBuy.onFailedSendPhoneNumber("خطا در سرور٬ مجددا تلاش نمایید");
                    }
                } else {
                    try {
                        String errorResponse = response.errorBody().string();
                        JSONObject joError = new JSONObject(errorResponse);
                        if ("POL0510".equals(joError.getString("status"))) {
                            ipBuy.onFailedSendPhoneNumber(joError.getString("message"));
                        } else if ("SVC0001".equals(joError.getString("status"))) {
                            ipBuy.onFailedSendPhoneNumber("خطا؛ لطفا 5 دقیقه دیگر امتحان نمایید.");
                        } else {
                            ipBuy.onFailedSendPhoneNumber(joError.getString("message"));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        ipBuy.onFailedSendPhoneNumber("خطا در سرور مجددا امتحان نمایید");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ipBuy.onFailedSendPhoneNumber("خطا در سرور مجددا امتحان نمایید");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseAuthenticationRequest> call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
                ipBuy.onFailedSendPhoneNumber("اشکال در برقراری اینترنت");
            }
        });
    }

    public void doSendKey(String key) {
        if (getHasKey()) {
            doSendKey2G(key);
        } else {
            doSendKeyNormal(key);
        }
    }

    protected void doSendKeyNormal(String key) {
        interfaceSendKey apiService = clientRetrofit.create(interfaceSendKey.class);
        Call<ResponseVerifyAuthentication> call = apiService.app_info(phoneNumber, appCode, key, productCode,getMarketingId());
        call.enqueue(new Callback<ResponseVerifyAuthentication>() {
            @Override
            public void onResponse(Call<ResponseVerifyAuthentication> call, Response<ResponseVerifyAuthentication> response) {
                if (response.code() == 200) {
                    if (response.body().getStatus().equals("0")) {
                        accessToke = response.body().getAccessToken();
                        Utils.setStringPreference(getContext(), "reference_code", "reference_code", accessToke);
                        doSubscribe();
                    } else {
                        ipBuy.onFailedSendKey(response.body().getMessage());
                    }
                } else {
                    try {
                        String errorResponse = response.errorBody().string();
                        JSONObject joError = new JSONObject(errorResponse);
                        if ("POL0503".equals(joError.getString("status"))) {
                            doSubscribe();
                        } else {
                            ipBuy.onFailedSendKey(joError.getString("message"));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        ipBuy.onFailedSendKey("خطا در سرور مجددا امتحان نمایید");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ipBuy.onFailedSendKey("خطا در سرور مجددا امتحان نمایید");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseVerifyAuthentication> call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
                ipBuy.onFailedSendKey("اشکال در برقراری اینترنت");

            }
        });
    }

    protected void doSendKey2G(String key) {
        iAuthenticationByKey apiService = clientRetrofit.create(iAuthenticationByKey.class);
        Call<ResponseAuthentication> call = apiService.authentication(phoneNumber, appCode, productCode, key);
        call.enqueue(new Callback<ResponseAuthentication>() {
            @Override
            public void onResponse(Call<ResponseAuthentication> call, Response<ResponseAuthentication> response) {
                if (response.code() == 200) {
                    if (response.body().getStatus().equals("0")) {
                        accessToke = response.body().getReferenceCode();
                        Utils.setStringPreference(getContext(), "reference_code", "reference_code", accessToke);
                        doSubscribe();
                    } else {
                        ipBuy.onFailedSendKey(response.body().getMessage());
                    }
                } else {
                    try {
                        String errorResponse = response.errorBody().string();
                        JSONObject joError = new JSONObject(errorResponse);
                        if ("POL0503".equals(joError.getString("status"))) {
                            doSubscribe();
                        } else {
                            ipBuy.onFailedSendKey(joError.getString("message"));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        ipBuy.onFailedSendKey("خطا در سرور مجددا امتحان نمایید");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ipBuy.onFailedSendKey("خطا در سرور مجددا امتحان نمایید");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseAuthentication> call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
                ipBuy.onFailedSendKey("اشکال در برقراری اینترنت");

            }
        });
    }

    private void doSubscribe() {
        interfaceSubscribe apiService = clientRetrofit.create(interfaceSubscribe.class);
        Call<ResponseSubscribeSecend> call = apiService.subscribe_2(
                phoneNumber,
                appCode,
                Utils.getStringPreference(getContext(), "reference_code", "reference_code", ""),
                productCode
        );

        call.enqueue(new Callback<ResponseSubscribeSecend>() {
            @Override
            public void onResponse(Call<ResponseSubscribeSecend> call, Response<ResponseSubscribeSecend> response) {
                if (response.code() == 200) {
                    if ("0".equals(response.body().getStatus()) || "POL0503".equals(response.body().getStatus())) {

                        savePhoneNumber(phoneNumber);

                        new RegisterInfo(getContext()).active(phoneNumber);

                        saveBuyDetails();

                        ipBuy.onSuccessSubscribe("با تشکر از شما \nسرویس شما در تاریخ " + getPersianDate() + " فعال گردید");
                    } else {
                        ipBuy.onFailedSubscribe(response.body().getStatus());
                    }
                } else {
                    try {
                        String strError = response.errorBody().string();
                        if (strError.contains("POL0503")) {
                            saveBuyDetails();
                            ipBuy.onSuccessSubscribe("با تشکر از شما\nسرویس دنیای درسا برای شما از قبل فعال می باشد");
                        } else {
                            JSONObject joError = new JSONObject(strError);
                            ipBuy.onFailedSubscribe(joError.getString("message"));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        ipBuy.onFailedSubscribe("۱.خطا در سرور مجددا امتحان نمایید");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ipBuy.onFailedSubscribe("۲.خطا در سرور مجددا امتحان نمایید");
                    }
//                    ipBuy.onFailedSubscribe(response.body().getStatus());
                }
            }

            @Override
            public void onFailure(Call<ResponseSubscribeSecend> call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
                ipBuy.onFailedSubscribe("اشکال در برقراری اینترنت");
            }
        });
    }

    public void checkStatus() {

        interfaceSubscribe apiService = clientRetrofit.create(interfaceSubscribe.class);
        Call<ResponseSubscribeSecend> call =
                apiService.subscribe_2(getPhoneNumber(),
                        "" + appCode,
                        getReferenceCode(),
                        productCode
        );
        call.enqueue(new Callback<ResponseSubscribeSecend>() {
            @Override
            public void onResponse(Call<ResponseSubscribeSecend> call, Response<ResponseSubscribeSecend> response) {
                if (response.code() == 200) {
                    if ("0".equals(response.body().getStatus())) {
                       /* if("active".equals(response.body().getData().get(0).getProductStatus().toLowerCase()) &&
                                "active".equals(response.body().getData().get(0).getAutoCharge().toLowerCase())){*/
                        if("active".equals(response.body().getData().get(0).getProductStatus().toLowerCase())){
                            ipBuy.onSuccessCheckStatus();
                        }else if("inactive".equals(response.body().getData().get(0).getProductStatus().toLowerCase()) &&
                                "active".equals(response.body().getData().get(0).getAutoCharge().toLowerCase())){
                            ipBuy.onFailedCheckStatus(STATUS_NO_CHARGE, "شارژ ناکافی");
                        }else {
                            clearUserInfo();
                            ipBuy.onFailedCheckStatus(1, response.body().getMessage());
                        }

                    } else {
                        clearUserInfo();
                        ipBuy.onFailedCheckStatus(2, response.body().getMessage());
                    }
                } else {
                    try {
                        JSONObject joError = new JSONObject(response.errorBody().string());
                        clearUserInfo();
                        ipBuy.onFailedCheckStatus(3, joError.getString("message"));
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        clearUserInfo();
                        ipBuy.onFailedCheckStatus(4, "خطا در سرور مجددا امتحان نمایید");
                    }
//                    ipBuy.onFailedSubscribe(response.body().getStatus());
                }
            }

            @Override
            public void onFailure(Call<ResponseSubscribeSecend> call, Throwable t) {
                Log.e(TAG, t.toString());
                ipBuy.onFailedCheckStatus(STATUS_INTERNRT_CONNECTION, "اشکال در اتصال به اینترنت");
            }
        });
    }

    //----- start interFace retrofit ----------------------------------
    private interface interfaceSendPhoneNumber {
        @Headers("Content-Type: application/json")
        @POST("subscription/subscribe")
        Call<ResponseAuthenticationRequest> deviceInfo(@Query("user_number") String user_number, @Query("service_code") String service_code,
                                                       @Query("product_code") String product_code);
    }

    //------- end interFace retrofit ----------------------------------
    //----- start interFace retrofit ----------------------------------
    private interface interfaceSendKey {
        @Headers("Content-Type: application/json")
        @POST("subscription/subscribe/confirm")
        Call<ResponseVerifyAuthentication> app_info(@Query("user_number") String user_number, @Query("service_code") String app_code,
                                                    @Query("pin") String pin, @Query("product_code") String product_code, @Query("entrance_path") String entrancePath);
    }

    //------- end interFace retrofit ----------------------------------
    //----- start interFace buy ----------------------------------
    private interface interfaceSubscribe {
        @Headers("Content-Type: application/json")
        @GET("subscription")
        Call<ResponseSubscribeSecend> subscribe_2(@Query("user_number") String user_number,
                                                  @Query("service_code") String service_code,
                                                  @Query("reference_code") String reference_code,
                                                  @Query("product_code") String product_code);
    }

    //------- end interFace retrofit ----------------------------------
    //----- start interFace buy ----------------------------------
    private interface iAuthenticationByKey {
        @Headers("Content-Type: application/json")
        @GET("authentication/reference_code")
        Call<ResponseAuthentication> authentication(@Query("user_number") String user_number,
                                                    @Query("service_code") String service_code,
                                                    @Query("product_code") String product_code,
                                                    @Query("pin") String pin);
    }

}
