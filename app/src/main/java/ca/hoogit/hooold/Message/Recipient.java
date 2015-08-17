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
package ca.hoogit.hooold.Message;

import android.net.Uri;

import com.android.ex.chips.RecipientEntry;
import com.android.ex.chips.recipientchip.DrawableRecipientChip;
import com.orm.SugarRecord;
import com.orm.dsl.Table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jordon
 *
 * Date    13/08/15
 * Description
 *
 */

@Table
public class Recipient extends SugarRecord implements Serializable {

    private String name;
    private String phone;
    private String pictureUrl;

    // Needed for reconstruction
    private long contactId;
    private long dataId;
    private int destinationType;
    private String lookupKey;

    private long messageId;

    public Recipient() {
    }

    public Recipient(String name) {
        this.name = name;
    }

    public Recipient(long messageId, String name, String phone) {
        this.messageId = messageId;
        this.name = name;
        this.phone = phone;
    }

    public Recipient(long messageId, String name, String phone, String pictureUrl) {
        this.messageId = messageId;
        this.name = name;
        this.phone = phone;
        this.pictureUrl = pictureUrl;
    }

    public static List<Recipient> chipsToRecipients(DrawableRecipientChip[] chips) {
        List<Recipient> recipients = new ArrayList<>();
        for (DrawableRecipientChip chip : chips) {
            RecipientEntry re = chip.getEntry();
            Recipient r = new Recipient(re.getDisplayName());
            r.setPhone(re.getDestination());
            if (re.getPhotoThumbnailUri() != null) {
                r.setPictureUrl(re.getPhotoThumbnailUri().toString());
            }
            r.setContactId(re.getContactId());
            r.setDataId(re.getDataId());
            r.setDestinationType(re.getDestinationType());
            r.setLookupKey(re.getLookupKey());
            recipients.add(r);
        }
        return recipients;
    }

    public RecipientEntry toRecipientEntry() {
        Uri photo = this.pictureUrl == null ? null : Uri.parse(this.pictureUrl);
        return RecipientEntry.rebuildEntry(
                name, phone, photo, contactId,dataId, destinationType, lookupKey);
    }

    public static List<Recipient> allRecipients(long id) {
        List<Recipient> recipients;
        recipients = Recipient.find(Recipient.class, "message_Id = ?", String.valueOf(id));

        return recipients;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessage(long messageId) {
        this.messageId = messageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public long getContactId() {

        return contactId;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
    }

    public long getDataId() {
        return dataId;
    }

    public void setDataId(long dataId) {
        this.dataId = dataId;
    }

    public int getDestinationType() {
        return destinationType;
    }

    public void setDestinationType(int destinationType) {
        this.destinationType = destinationType;
    }

    public String getLookupKey() {
        return lookupKey;
    }

    public void setLookupKey(String lookupKey) {
        this.lookupKey = lookupKey;
    }
}
