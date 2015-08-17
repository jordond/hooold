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

import android.content.Context;
import android.telephony.SmsManager;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
        Log.d(TAG, "New SMS created with id of: " + this.id);
    }

    public void send(Context context) {
        SmsManager manager = SmsManager.getDefault();
        manager.se
    }
}
