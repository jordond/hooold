package ca.hoogit.hooold.Scheduling;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import ca.hoogit.hooold.Utils.Consts;

public class SchedulingService extends IntentService {

    public static void startAddMessage(Context context, Sms sms) {
        Intent intent = new Intent(context, SchedulingService.class);
        intent.setAction(Consts.ACTION_SCHEDULE_ADD);
        intent.putExtra(Consts.KEY_MESSAGE_SMS, sms);
        context.startService(intent);
    }

    public static void startDeleteMessage(Context context, Sms sms) {
        Intent intent = new Intent(context, SchedulingService.class);
        intent.setAction(Consts.ACTION_SCHEDULE_DELETE);
        intent.putExtra(Consts.KEY_MESSAGE_SMS, sms);
        context.startService(intent);
    }

    public SchedulingService() {
        super("SchedulingService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            final Sms sms = (Sms) intent.getSerializableExtra(Consts.KEY_MESSAGE_SMS);
            switch (action) {
                case Consts.ACTION_SCHEDULE_ADD:
                    handleActionAdd(sms);
                    break;
                case Consts.ACTION_SCHEDULE_DELETE:
                    handleActionDelete(sms);
                    break;
                case Consts.ACTION_SCHEDULE_SEND:
                    handleActionSend(sms);
                    break;
            }
        }
    }

    private void handleActionAdd(Sms sms) {
        // TODO: Handle action add
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void handleActionDelete(Sms sms) {
        // TODO: Handle action delete
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void handleActionSend(Sms sms) {
        // TODO: Handle action send
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
