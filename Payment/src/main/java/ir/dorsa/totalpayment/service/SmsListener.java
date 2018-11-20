package ir.dorsa.totalpayment.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * Created by Mehdi Shojaeian on 5/19/2017.
 */

public class SmsListener extends BroadcastReceiver {

    public static String BROADCAST_UPDATE = "BROADCAST_UPDATE";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs = null;
            String msg_from;

            if (bundle != null){
                //---retrieve the SMS message received---
                try{
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for(int i=0; i<msgs.length; i++){
                        msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();
                        String msgBody = msgs[i].getMessageBody();
                        if(msg_from.equals("983003")){

                            /*String from="کد فعالسازی:";
                            msgBody = msgBody.substring(from.length(), msgBody.indexOf("\n")-1);
                            Intent in = new Intent(BROADCAST_UPDATE);
                            in.putExtra("code", msgBody);
                            LocalBroadcastManager.getInstance(context).sendBroadcast(in);
*/
                            Intent in = new Intent(BROADCAST_UPDATE);
                            in.putExtra("code", stripNonDigits(msgBody));
                            LocalBroadcastManager.getInstance(context).sendBroadcast(in);
                        }
                        break;
                    }
                }catch(Exception e){
                }
            }
        }
    }

    public static String stripNonDigits(
            final CharSequence input /* inspired by seh's comment */) {
        final StringBuilder sb = new StringBuilder(
                input.length() /* also inspired by seh's comment */);
        for (int i = 0; i < input.length(); i++) {
            final char c = input.charAt(i);
            if (c > 47 && c < 58) {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}