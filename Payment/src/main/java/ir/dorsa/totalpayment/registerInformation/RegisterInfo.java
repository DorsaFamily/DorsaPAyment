package ir.dorsa.totalpayment.registerInformation;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.google.gson.Gson;

import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import ir.dorsa.totalpayment.registerInformation.model.ParamsRegisterInformation;
import ir.dorsa.totalpayment.tools.Utils;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class RegisterInfo {

    private Retrofit retrofit;
    private final String BASE_URL = " https://79.175.155.152:2243/api/app/";
    private Context context;

    public RegisterInfo(Context context) {
        this.context = context;
    }

    private Retrofit getRetrofit() {


        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getUnsafeOkHttpClient())
                    .build();
        }

        return retrofit;
    }


    public void install() {
        if (!Utils.getBooleanPreference(context, Utils.REGISTER_INFO, Utils.REGISTER_INFO_INSTALL_KEY)) {

            ApiRequestInstall service = getRetrofit().create(ApiRequestInstall.class);
            Call<ResponseBody> call = service.installApp(getParamsInformation(null));
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.d("Payment", "onResponse: "+response.code());
                    if (response.code() == 200) {
                        Utils.setBooleanPreference(context, Utils.REGISTER_INFO, Utils.REGISTER_INFO_INSTALL_KEY, true);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }

    public void active(String phoneNumber) {
        if (!Utils.getBooleanPreference(context, Utils.REGISTER_INFO, Utils.REGISTER_INFO_ACTIVE_KEY)) {
            ApiRequestActive service = getRetrofit().create(ApiRequestActive.class);
            Call<ResponseBody> call = service.activeApp(getParamsInformation(phoneNumber));
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.code() == 200) {
                        Utils.setBooleanPreference(context, Utils.REGISTER_INFO, Utils.REGISTER_INFO_ACTIVE_KEY, true);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }

    public void deactive(String phoneNumber) {

        ApiRequestDeactive service = getRetrofit().create(ApiRequestDeactive.class);
            Call<ResponseBody> call = service.deactiveApp(getParamsInformation(phoneNumber));
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Utils.setBooleanPreference(context, Utils.REGISTER_INFO, Utils.REGISTER_INFO_INSTALL_KEY, false);
                    Utils.setBooleanPreference(context, Utils.REGISTER_INFO, Utils.REGISTER_INFO_ACTIVE_KEY, false);
                    Utils.setBooleanPreference(context, Utils.REGISTER_INFO, Utils.REGISTER_INFO_DEACTIVE_KEY, false);
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Utils.setBooleanPreference(context, Utils.REGISTER_INFO, Utils.REGISTER_INFO_INSTALL_KEY, false);
                    Utils.setBooleanPreference(context, Utils.REGISTER_INFO, Utils.REGISTER_INFO_ACTIVE_KEY, false);
                    Utils.setBooleanPreference(context, Utils.REGISTER_INFO, Utils.REGISTER_INFO_DEACTIVE_KEY, false);
                    t.printStackTrace();
                }
            });

    }


    private ParamsRegisterInformation getParamsInformation(String phoneNumber) {

        ApplicationInfo applicationInfo = context.getApplicationInfo();
        String appName = applicationInfo.name;

        String pkgName = context.getPackageName();
        String versionName = null;
        try {
            ApplicationInfo ai;
            try {
                ai = context.getPackageManager().getApplicationInfo(pkgName, 0);
            } catch (final PackageManager.NameNotFoundException e) {
                ai = null;
            }
            appName = (String) (ai != null ? context.getPackageManager().getApplicationLabel(ai) : "(unknown)");
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;

        } catch (PackageManager.NameNotFoundException e) {
            versionName = "";
            e.printStackTrace();
        }

        ParamsRegisterInformation params = new ParamsRegisterInformation();

        params.setAppName(appName);
        params.setPkgName(pkgName);
        params.setVarsion(versionName);

        params.setDeviceModel(Build.MANUFACTURER);
        params.setDeviceName(Build.MODEL);
        params.setUniqueCode(Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
        params.setNumber(phoneNumber);

        Log.d("payment", "getParamsInformation: "+new Gson().toJson(params));

        return params;
    }


    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .addInterceptor(loggingInterceptor)
                    .hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            OkHttpClient okHttpClient = builder.build();

            return okHttpClient;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    interface ApiRequestInstall {
        @POST("InstallApp")
        Call<ResponseBody> installApp(@Body() ParamsRegisterInformation params);
    }
    interface ApiRequestActive {
        @POST("ActiveApp")
        Call<ResponseBody> activeApp(@Body() ParamsRegisterInformation params);
    }
    interface ApiRequestDeactive {
        @POST("DeactiveApp")
        Call<ResponseBody> deactiveApp(@Body() ParamsRegisterInformation params);

    }


}
