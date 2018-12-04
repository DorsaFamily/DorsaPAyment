package ir.dorsa.totalpayment.payment;

import android.content.Context;

/**
 * Created by mehdi on 12/28/16.
 */

public interface IMPayment {
    
    String SH_P_BUY_IN_APP="sh_p_buy_in_app";
    String SH_P_BUY_IN_APP_PHONE_NUMBER="sh_p_buy_in_app_phone_number";
    String SH_P_BUY_IN_APP_ACCESS_TOKEN ="sh_p_buy_in_app_ACCESS_TOKEN";
    String SH_P_BUY_IN_APP_DATE ="sh_p_buy_in_app_DATE";
    String SH_P_BUY_IN_APP_REGISTERED="sh_p_buy_in_app_registered";
    String SH_P_BUY_IN_APP_ENABLE_IRANCELL="SH_P_BUY_IN_APP_ENABLE_IRANCELL";
    String SH_P_BUY_IN_APP_HAS_KEY="SH_P_BUY_IN_APP_HAS_KEY";
    String BR_CAST_GOT_NUMBER="broadcast_get_key";

    int STATUS_USER_NOT_REGISTER_YET=9;
    int STATUS_NO_CHARGE=10;
    int STATUS_INTERNRT_CONNECTION=11;

    void setContext(Context context);
    Context getContext();
}
