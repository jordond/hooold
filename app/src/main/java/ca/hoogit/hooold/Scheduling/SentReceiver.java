package ca.hoogit.hooold.Scheduling;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ca.hoogit.hooold.Utils.Consts;

public class SentReceiver extends BroadcastReceiver {

    private static final String TAG = SentReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (getResultCode()) {
            case Activity.RESULT_OK:
                long id = intent.getLongExtra(Consts.KEY_MESSAGE_ID, -1);
                String recip = intent.getStringExtra(Consts.KEY_SMS_RECIPIENT_PHONE);
                int max = intent.getIntExtra(Consts.KEY_SMS_RECIPIENT_TOTAL, 1);
                int count = intent.getIntExtra(Consts.KEY_SMS_RECIPIENT_COUNT, 1);
                int part = intent.getIntExtra(Consts.KEY_SMS_PART, 1);
                int parts = intent.getIntExtra(Consts.KEY_SMS_PARTS, 1);
                Log.d(TAG, "Message receiver MSG_IG: " + id + " PHONE: " + recip + " Recip: " + count + " of " + max + " part: " + part + " of " + parts);
                break;
            default:
                // TODO handle messages not sending
                Log.e(TAG, "Message could not be sent, result code: " + getResultCode());
        }
    }
}
