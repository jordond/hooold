package ca.hoogit.hooold.Message;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.widget.Switch;

import ca.hoogit.hooold.Utils.Consts;

public class MessageService extends IntentService {

    public static void startMessageSent(Context context, long messageId) {
        Intent intent = new Intent(context, MessageService.class);
        intent.setAction(Consts.ACTION_MESSAGE_SENT);
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
                case Consts.ACTION_MESSAGE_SENT:
                    handleMessageSent(messageId);
                    break;
                case Consts.ACTION_MESSAGE_FAILED:
                    final int errorCode = intent.getIntExtra(Consts.KEY_MESSAGE_ERROR_CODE, 0);
                    handleMessageFailed(messageId, errorCode);
                    break;
            }
        }
    }

    private void handleMessageSent(long messageId) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void handleMessageFailed(long messageId, int errorCode) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
