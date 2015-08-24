package ca.hoogit.hooold;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ca.hoogit.hooold.Scheduling.SchedulingService;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = BootReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Boot notification was received, starting service");
        SchedulingService.startAddAll(context);
    }
}
