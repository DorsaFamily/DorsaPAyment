package ir.dorsa.totalpayment.irancell;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.concurrent.TimeUnit;

import ir.dorsa.totalpayment.payment.IMPayment;
import ir.dorsa.totalpayment.payment.PPayment;
import ir.dorsa.totalpayment.payment.Payment;
import ir.dorsa.totalpayment.registerInformation.RegisterInfo;
import ir.dorsa.totalpayment.tools.Utils;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class IrancellCancel {

    private Context context;
    private onIrancellCanceled getResult;

    public IrancellCancel(Context context, onIrancellCanceled getResult){
        this.context = context;

        this.getResult = getResult;
    }

    public static final String BASE_URL_CHAHARKHONE = "https://seller.jhoobin.com/ws/androidpublisher/";
    private static Retrofit retrofitChaharkhone;
    public static Retrofit getClientChaharkhone() {

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS).addInterceptor(loggingInterceptor)
                .build();

        if (retrofitChaharkhone == null) {
            retrofitChaharkhone = new Retrofit.Builder()
                    .baseUrl(BASE_URL_CHAHARKHONE)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }
        return retrofitChaharkhone;
    }


    public void cancelPurchase(String irancellSku) {
        Get_Cancel_Purchase service = getClientChaharkhone().create(Get_Cancel_Purchase.class);
        Call<Object> call = service.cancel(context.getPackageName(),
                irancellSku,
                Utils.getStringPreference(context,Utils.PURCHASETOKEN, Utils.PURCHASETOKENKEY,""), PPayment.access_token
        );
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.code() == 200) {
                    new RegisterInfo(context).deactive(new Payment(context).getPhoneNumber());
                    clearUserInfo();
                    getResult.resultSuccess();
                } else {
                    getResult.resultFailed("اشکال در لغو اشتراک");
                }
            }
            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                t.printStackTrace();
                getResult.resultFailed("اشکال در سرویس");
            }
        });
    }


    public void clearUserInfo(){
        SharedPreferences sharedPrefrece = context.getSharedPreferences(IMPayment.SH_P_BUY_IN_APP, context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefereceEditor = sharedPrefrece.edit();
        sharedPrefereceEditor.putString(IMPayment.SH_P_BUY_IN_APP_ACCESS_TOKEN, null);

        Utils.setBooleanPreference(context, Utils.REGISTER_INFO, Utils.REGISTER_INFO_INSTALL_KEY, false);
        Utils.setBooleanPreference(context, Utils.REGISTER_INFO, Utils.REGISTER_INFO_ACTIVE_KEY, false);
        Utils.setBooleanPreference(context, Utils.REGISTER_INFO, Utils.REGISTER_INFO_DEACTIVE_KEY, false);

        sharedPrefereceEditor.putString(IMPayment.SH_P_BUY_IN_APP_PHONE_NUMBER, "");

        sharedPrefereceEditor.putBoolean(IMPayment.SH_P_BUY_IN_APP_HAS_KEY, false);

        sharedPrefereceEditor.putBoolean(IMPayment.SH_P_BUY_IN_APP_REGISTERED, false);

        sharedPrefereceEditor.putString(IMPayment.SH_P_BUY_IN_APP_DATE, "");

        sharedPrefereceEditor.commit();
    }

    public interface Get_Cancel_Purchase {
        @GET("v2/applications/{packageName}/purchases/subscriptions/{subscriptionId}/tokens/{token}:cancel")
        Call<Object> cancel(@Path("packageName") String packageName,
                            @Path("subscriptionId") String subscriptionId,
                            @Path("token") String token,
                            @Query("access_token") String access_token);
    }

    public interface onIrancellCanceled {
        void resultSuccess();
        void resultFailed(String msg);
    }
}
