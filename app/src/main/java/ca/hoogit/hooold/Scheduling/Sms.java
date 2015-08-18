/**
 * Copyright (C) 2015, Jordon de Hoog
 * <p/>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package ca.hoogit.hooold.Scheduling;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ca.hoogit.hooold.Main.MainActivity;
import ca.hoogit.hooold.Utils.Consts;

/**
 * @author jordon
 *
 * Date    17/08/15
 * Description
 *
 */
public class Sms implements Serializable {

    private static final String TAG = Sms.class.getSimpleName();

    public long id;
    public List<String> recipients;
    public String messageBody;

    public Sms(long id, String recipient, String messageBody) {
        this.id = id;
        this.recipients = new ArrayList<>();
        this.recipients.add(recipient);
        this.messageBody = messageBody;
        Log.d(TAG, "New SMS created with id of: " + this.id);
    }

    public Sms(long id, List<String> recipients, String messageBody) {
        this.id = id;
        this.recipients = recipients;
        this.messageBody = messageBody;
        if (this.recipients == null) {
            this.recipients = new ArrayList<>();
        }
        Log.d(TAG, "New SMS created with id of: " + this.id);
    }

    public void send(Context context) {
        int count = 1;
        SmsManager manager = SmsManager.getDefault();
        for (String recipient : this.recipients) {
            if (messageBody.length() <= Consts.MAX_SMS_LENGTH) {
                Log.i(TAG, "Message will be sent as single");
                single(context, manager, recipient, count);
            } else {
                Log.i(TAG, "Message will be sent in multiple parts");
                multiple(context, manager, recipient, count);
            }
            Log.d(TAG, "Sending message to: " + recipient);
            count++;
        }
        Log.i(TAG, "Message sent to " + this.recipients.size() + " recipients");
    }

    public void single(Context context, SmsManager manager, String recipient, int count) {
        Intent sent = generateIntent(Consts.INTENT_SMS_SENT, recipient, count);
        PendingIntent pendingSent = PendingIntent.getBroadcast(
                context, (int) id, sent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent delivered = generateIntent(Consts.INTENT_SMS_DELIVERED, recipient, count);
        PendingIntent pendingDelivered = PendingIntent.getBroadcast(
                context, (int)id, delivered, PendingIntent.FLAG_UPDATE_CURRENT);

        manager.sendTextMessage(recipient, null, messageBody, pendingSent, pendingDelivered);
    }

    private void multiple(Context context, SmsManager manager, String recipient, int count) {
        ArrayList<String> parts = manager.divideMessage(messageBody);
        Log.d(TAG, "Message length: " + messageBody.length() + " split into " + parts.size() + " parts.");

        ArrayList<PendingIntent> sent = generateIntents(
                context, Consts.INTENT_SMS_SENT, recipient, count, parts.size());
        ArrayList<PendingIntent> delivered = generateIntents(
                context, Consts.INTENT_SMS_DELIVERED, recipient, count, parts.size());

        manager.sendMultipartTextMessage(recipient, null, parts, sent, delivered);
    }

    private Intent generateIntent(String action, String recipient, int recipientCount) {
        Intent intent = new Intent(action);
        intent.putExtra(Consts.KEY_SMS_RECIPIENT_TOTAL, this.recipients.size());
        intent.putExtra(Consts.KEY_SMS_RECIPIENT_PHONE, recipient);
        intent.putExtra(Consts.KEY_SMS_RECIPIENT_COUNT, recipientCount);
        intent.putExtra(Consts.KEY_MESSAGE_ID, id);
        return intent;
    }

    private ArrayList<PendingIntent> generateIntents(
            Context context, String action, String recipient, int recipientCount, int partCount) {
        ArrayList<PendingIntent> pendingIntents = new ArrayList<>();
        for (int i = 1; i <= partCount; i++) {
            Intent intent = generateIntent(action, recipient, recipientCount);
            intent.putExtra(Consts.KEY_SMS_PARTS, partCount);
            intent.putExtra(Consts.KEY_SMS_PART, i);

            PendingIntent pending = PendingIntent.getBroadcast(
                    context, (int) id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            pendingIntents.add(pending);
        }
        return pendingIntents;
    }
}
