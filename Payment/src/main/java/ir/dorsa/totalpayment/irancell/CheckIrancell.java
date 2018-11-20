package ir.dorsa.totalpayment.irancell;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import ir.dorsa.totalpayment.BuildConfig;
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
import retrofit2.http.QueryMap;

import static ir.dorsa.totalpayment.payment.PPayment.access_token;

public class CheckIrancell {

    public static final String BASE_URL_CHAHARKHONE = "https://seller.jhoobin.com/ws/androidpublisher/";
    private static Retrofit retrofitChaharkhone;
    public static Retrofit getClientChaharkhone() {

         OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                ;
        if(BuildConfig.DEBUG){
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            okHttpClientBuilder.addInterceptor(loggingInterceptor);
        }

        final OkHttpClient okHttpClient=okHttpClientBuilder.build();

        if (retrofitChaharkhone == null) {
            retrofitChaharkhone = new Retrofit.Builder()
                    .baseUrl(BASE_URL_CHAHARKHONE)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }
        return retrofitChaharkhone;
    }

    public void checkIrancell(Context context,String irancellSku){
        IGetStatus service = getClientChaharkhone().create(IGetStatus.class);

        Map<String, String> params = new HashMap<String, String>();
        params.put("access_token", access_token);

        Call<Object> call = service.cancel(context.getPackageName(),
                irancellSku,
                Utils.getStringPreference(context, "reference_code", "reference_code", ""),
                params
        );
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {

                if (response.code() == 200) {

                } else {

                }
            }
            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                t.printStackTrace();

            }
        });
    }


    public interface IGetStatus {
        @GET("v2/applications/{packageName}/purchases/subscriptions/{subscriptionId}/tokens/{token}")
        Call<Object> cancel(@Path("packageName") String packageName,
                            @Path("subscriptionId") String subscriptionId,
                            @Path("token") String token,
                            @QueryMap Map<String, String> params);
    }
}
