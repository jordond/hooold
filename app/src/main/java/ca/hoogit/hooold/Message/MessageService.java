package ca.hoogit.hooold.Message;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import ca.hoogit.hooold.Utils.Consts;

public class MessageService extends IntentService {

    private static final String TAG = MessageService.class.getSimpleName();

    public static void startMessageSuccess(Context context, long messageId) {
        Intent intent = new Intent(context, MessageService.class);
        intent.setAction(Consts.ACTION_MESSAGE_SUCCESS);
        intent.putExtra(Consts.KEY_MESSAGE_ID, messageId);
        context.startService(intent);
    }

    public static void startMessageFailed(Context context, long messageId, int errorCode) {
        Intent intent = new Intent(context, MessageService.class);
        intent.setAction(Consts.ACTION_MESSAGE_FAILED);
        intent.putExtra(Consts.KEY_MESSAGE_ID, messageId);
        intent.putExtra(Consts.KEY_MESSAGE_ERROR_CODE, errorCode);
        context.startService(intent);
    }

    public MessageService() {
        super("MessageService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            final long messageId = intent.getLongExtra(Consts.KEY_MESSAGE_ID, -1L);
            switch (action) {
                case Consts.ACTION_MESSAGE_SUCCESS:
                    Log.d(TAG, "Handling success message");
                    handleMessageSuccess(messageId);
                    break;
                case Consts.ACTION_MESSAGE_FAILED:
                    final int errorCode = intent.getIntExtra(Consts.KEY_MESSAGE_ERROR_CODE, 0);
                    Log.d(TAG, "Handling failed message, code: " + errorCode);
                    handleMessageFailed(messageId, errorCode);
                    break;
            }
        }
    }

    private void handleMessageSuccess(long messageId) {
        Message message = Message.findById(Message.class, messageId);
        if (message != null) {
            message.setCategory(Consts.MESSAGE_CATEGORY_RECENT);
            message.setStatus(Consts.MESSAGE_STATUS_SUCCESS);
            message.save(false);
            broadcast(messageId);
        }
    }

    private void handleMessageFailed(long messageId, int errorCode) {
        Message message = Message.findById(Message.class, messageId);
        if (message != null) {
            message.setCategory(Consts.MESSAGE_CATEGORY_RECENT);
            message.setStatus(Consts.MESSAGE_STATUS_FAILED);
            message.setErrorCode(errorCode);
            message.save(false);
            broadcast(messageId);
        }
    }

    private void broadcast(long messageId) {
        Intent refresh = new Intent(Consts.INTENT_MESSAGE_REFRESH);
        refresh.putExtra(Consts.KEY_MESSAGE_ID, messageId);
        LocalBroadcastManager.getInstance(getApplication()).sendBroadcast(refresh);
        Log.i(TAG, "Broadcasting update to all who will listen");
    }
}
