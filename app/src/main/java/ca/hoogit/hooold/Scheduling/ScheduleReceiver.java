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
            long id = intent.getLongExtra(Consts.KEY_MESSAGE_ID, -1);
            if (id != -1) {
                Log.i(TAG, "No time like the present, alarm has been called, lets do it");
                SchedulingService.startSendMessage(context, id);
            } else {
                Log.e(TAG, "No SMS id was supplied in the intent");
            }
        }
    }
}
