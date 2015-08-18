package ca.hoogit.hooold.Scheduling;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ca.hoogit.hooold.Message.MessageService;
import ca.hoogit.hooold.Utils.Consts;

public class SentReceiver extends BroadcastReceiver {

    private static final String TAG = SentReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            long id = intent.getLongExtra(Consts.KEY_MESSAGE_ID, -1);
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Log.i(TAG, "Message was sent, starting service");
                    MessageService.startMessageSuccess(context, id);
                    break;
                default:
                    Log.e(TAG, "Message could not be sent, result code: " + getResultCode());
                    MessageService.startMessageFailed(context, id, getResultCode());
            }
        }
    }
}
