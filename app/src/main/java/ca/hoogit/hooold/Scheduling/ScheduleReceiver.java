package ca.hoogit.hooold.Scheduling;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ca.hoogit.hooold.Message.MessageService;
import ca.hoogit.hooold.Utils.Consts;

public class ScheduleReceiver extends BroadcastReceiver {

    public static final String TAG = ScheduleReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            Sms sms = (Sms) intent.getSerializableExtra(Consts.KEY_MESSAGE_SMS);
            if (sms != null) {
                Log.i(TAG, "No time like the present, alarm has been called, lets do it");
                SchedulingService.startSendMessage(context, sms);
            } else {
                Log.e(TAG, "No SMS object was supplied in the intent");
            }
        }
    }
}
