package ca.hoogit.hooold.Scheduling;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.util.Calendar;

import ca.hoogit.hooold.Message.Message;
import ca.hoogit.hooold.Utils.Consts;

public class SchedulingService extends IntentService {

    private static final String TAG = SchedulingService.class.getSimpleName();

    public static void startAddMessage(Context context, Sms sms) {
        Intent intent = new Intent(context, SchedulingService.class);
        intent.setAction(Consts.ACTION_SCHEDULE_ADD);
        intent.putExtra(Consts.KEY_MESSAGE_SMS, sms);
        context.startService(intent);
    }

    public static void startUpdateMessage(Context context, Sms sms) {
        Intent intent = new Intent(context, SchedulingService.class);
        intent.setAction(Consts.ACTION_SCHEDULE_UPDATE);
        intent.putExtra(Consts.KEY_MESSAGE_SMS, sms);
        context.startService(intent);
    }

    public static void startDeleteMessage(Context context, Sms sms) {
        Intent intent = new Intent(context, SchedulingService.class);
        intent.setAction(Consts.ACTION_SCHEDULE_DELETE);
        intent.putExtra(Consts.KEY_MESSAGE_SMS, sms);
        context.startService(intent);
    }

    public static void startSendMessage(Context context, Sms sms) {
        Intent intent = new Intent(context, SchedulingService.class);
        intent.setAction(Consts.ACTION_SCHEDULE_SEND);
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
            if (sms != null) {
                switch (action) {
                    case Consts.ACTION_SCHEDULE_ADD:
                        handleActionAdd(sms);
                        break;
                    case Consts.ACTION_SCHEDULE_UPDATE:
                        handleActionUpdate(sms);
                        break;
                    case Consts.ACTION_SCHEDULE_DELETE:
                        handleActionDelete(sms);
                        break;
                    case Consts.ACTION_SCHEDULE_SEND:
                        handleActionSend(sms);
                }
            } else {
                Log.e(TAG, "No SMS object was supplied in the intent");
            }
        }
    }

    private void handleActionAdd(Sms sms) {
        PendingIntent pendingAlarm = getPendingIntent(sms);

        Calendar time = Calendar.getInstance();
        time.setTime(sms.date);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Alarming.getInstance().manager()
                    .setExact(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), pendingAlarm);
        } else {
            Alarming.getInstance().manager()
                    .set(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), pendingAlarm);
        }
        Log.i(TAG, "Scheduling new SMS message with id of: " + sms.id);
    }

    private void handleActionUpdate(Sms sms) {
        PendingIntent existing = getPendingIntent(sms);
        Alarming.getInstance().manager().cancel(existing);
        existing.cancel();
        Log.i(TAG, "Canceling scheduled message if it exists and re-adding");
        handleActionAdd(sms);
    }

    private void handleActionDelete(Sms sms) {
        PendingIntent pendingAlarm = getPendingIntent(sms);
        Alarming.getInstance().manager().cancel(pendingAlarm);
        pendingAlarm.cancel();
        Log.i(TAG, "Canceling the scheduled SMS message with id of: " + sms.id);
    }

    private void handleActionSend(Sms sms) {
        sms.send(getApplicationContext());
        Log.i(TAG, "Alarm has been met, time to send id: " + sms.id);
    }

    private PendingIntent getPendingIntent(Sms sms) {
        Intent alarm = new Intent(getApplicationContext(), ScheduleReceiver.class);
        alarm.putExtra(Consts.KEY_MESSAGE_ID, sms.id);
        alarm.putExtra(Consts.KEY_MESSAGE_SMS, sms);
        return PendingIntent.getBroadcast(getApplicationContext(),
                (int) sms.id, alarm, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}
