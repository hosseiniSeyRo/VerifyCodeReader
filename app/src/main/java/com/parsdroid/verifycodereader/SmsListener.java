package com.parsdroid.verifycodereader;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class SmsListener extends BroadcastReceiver {

    private SharedPreferences preferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub

        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
            SmsMessage[] msgs = null;
            String msg_from;
            if (bundle != null) {
                //---retrieve the SMS message received---
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    String msgBody = "";
                    for (int i = 0; i < msgs.length; i++) {
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();
                        msgBody = msgs[i].getMessageBody();
                    }

                    Log.e("RHLog", msgBody);
                    String verifyCode = getVerifyCodeFromSmsText(msgBody);
                    copyToClipboard(context, "verifyCode", verifyCode);
                    Toast.makeText(context, verifyCode + "copied to clipboard", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
//                            Log.d("Exception caught",e.getMessage());
                }
            }
        }
    }

    private String getVerifyCodeFromSmsText(String smsText) {
        ArrayList<String> digitList = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < smsText.length(); i++) {
            char c = smsText.charAt(i);
            if (Character.isDigit(c)) {
                builder.append(c);
            } else if (builder.length() > 0) {
                Log.e("RHLog", "digit: " + builder.toString());
                digitList.add(builder.toString());
                Log.e("RHLog", "digitList: " + digitList.toString());
                builder.setLength(0);
            }
        }
        if (builder.length() > 0) {
            Log.e("RHLog", "digit: " + builder.toString());
            digitList.add(builder.toString());
            Log.e("RHLog", "digitList: " + digitList.toString());
            builder.setLength(0);
        }
        return digitList.get(digitList.size() - 1);
    }

    private void copyToClipboard(Context context, String label, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);
    }
}
