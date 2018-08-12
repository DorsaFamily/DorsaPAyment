package ir.dorsa.totalpayment.tools;

import android.content.SharedPreferences;
import android.util.Log;

import static ir.dorsa.totalpayment.payment.IMPayment.SH_P_BUY_IN_APP;
import static ir.dorsa.totalpayment.payment.IMPayment.SH_P_BUY_IN_APP_ENABLE_IRANCELL;
import static ir.dorsa.totalpayment.payment.IMPayment.SH_P_BUY_IN_APP_PHONE_NUMBER;

public class Func {

    public static boolean isNumberMci(String phoneNumber) {
        phoneNumber = phoneNumber.
                replace("۰", "0").
                replace("۱", "1").
                replace("۲", "2").
                replace("۳", "3").
                replace("۴", "4").
                replace("۵", "5").
                replace("۶", "6").
                replace("۷", "7").
                replace("۸", "8").
                replace("۹", "9");

        Log.d("AppPayment.TAG", "isNumberMci: " + phoneNumber + ":" + (phoneNumber.startsWith("091") ||
                phoneNumber.startsWith("099")));

        if (phoneNumber.startsWith("091") ||
                phoneNumber.startsWith("099")
                ) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isNumberIrancell(String phoneNumber) {

        phoneNumber = phoneNumber.
                replace("۰", "0").
                replace("۱", "1").
                replace("۲", "2").
                replace("۳", "3").
                replace("۴", "4").
                replace("۵", "5").
                replace("۶", "6").
                replace("۷", "7").
                replace("۸", "8").
                replace("۹", "9");


        if (phoneNumber.startsWith("093") ||
                phoneNumber.startsWith("0901") ||
                phoneNumber.startsWith("0902") ||
                phoneNumber.startsWith("0903")
                ) {
            return true;
        } else {
            return false;
        }
    }
}