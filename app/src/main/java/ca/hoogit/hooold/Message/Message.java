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

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.android.ex.chips.RecipientEntry;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.orm.dsl.Table;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import ca.hoogit.hooold.Scheduling.Sms;
import ca.hoogit.hooold.Utils.Consts;

/**
 * @author jordon
 *         <p/>
 *         Date    11/08/15
 *         Description
 */
@Table
public class Message extends SugarRecord implements Parcelable, Comparable<Message> {

    // TODO cache the title and the photo url, so i don't need to query database for each
    private Date created;
    private Date scheduleDate; // TODO auto add to recants if date is past
    private boolean repeat;
    private int category; // TODO rename to category add type (facebook,twitter,etc)
    private String message;
    private int status;
    private int errorCode;

    @Ignore
    private boolean selected;
    @Ignore
    private boolean wasSelected;
    @Ignore
    private List<Recipient> recipients;

    public Message() {
    }

    public Message(Date scheduleDate) {
        this.created = new Date();
        this.category = Consts.MESSAGE_CATEGORY_SCHEDULED;
        this.scheduleDate = scheduleDate;
    }

    public Message(Date scheduleDate, List<Recipient> recipients) {
        this.created = new Date();
        this.category = Consts.MESSAGE_CATEGORY_SCHEDULED;
        this.scheduleDate = scheduleDate;
        this.recipients = recipients;
    }

    public Sms toSms() {
        if (getRecipients().size() == 1) {
            return new Sms(getId(), recipients.get(0).getPhone(), message, scheduleDate);
        } else {
            List<String> phoneNumbs = new ArrayList<>();
            for (Recipient recipient : this.recipients) {
                phoneNumbs.add(recipient.getPhone());
            }
            return new Sms(getId(), phoneNumbs, message, scheduleDate);
        }
    }

    /**
     * DB Methods
     */

    public static MessageList all() {
        List<Message> list = Message.listAll(Message.class);
        MessageList messages = new MessageList();
        for (Message message : list) {
            messages.add(message, false);
        }
        return messages;
    }

    public static MessageList all(int type) {
        if (type == Consts.MESSAGE_CATEGORY_ALL) {
            return all();
        }
        List<Message> list = Message.find(Message.class, "category = ?", String.valueOf(type));
        MessageList messages = new MessageList();
        for (Message message : list) {
            message.getRecipients();
            messages.add(message, false);
        }
        messages.sort();
        return messages;
    }

    @Override
    public long save() {
        this.selected = false;
        this.wasSelected = false;
        long id = super.save();
        if (recipients == null) {
            recipients = Recipient.allRecipients(id);
        }
        for (Recipient entry : recipients) {
            if (entry.getMessageId() != this.getId()) {
                entry.setMessage(id);
                entry.save();
            }
        }
        return id; // TODO FIX UPDATING WHEN GOING FROM RECENT TO SCHEDULED
    }

    public long save(boolean saveRecipients) {
        if (saveRecipients) {
            return save();
        }
        return super.save();
    }

    @Override
    public boolean delete() {
        if (this.recipients == null || this.recipients.isEmpty()) {
            this.recipients = Recipient.allRecipients(getId());
        }
        for (Recipient recipient : this.recipients) {
            recipient.delete();
        }
        this.selected = false;
        this.wasSelected = false;
        return super.delete();
    }

    /**
     * Helpers
     */

    public List<RecipientEntry> getRecipientEntries() {
        List<RecipientEntry> list = new ArrayList<>();
        for (Recipient recipient : this.getRecipients()) {
            list.add(recipient.toRecipientEntry());
        }
        return list;
    }

    public static List<Message> getSelected(List<Message> messages) {
        List<Message> list = new ArrayList<>();
        for (Message message : messages) {
            if (message.isSelected()) {
                list.add(message);
            }
        }
        return list;
    }


    public String getTitle() {
        String title = "";
        if (this.getRecipients().size() > 0) {
            Recipient first = this.getRecipients().get(0);
            title = first.getName();
            if (getRecipients().size() > 1) {
                title += " +" + this.getRecipientCount();
            }
        }
        return title;
    }

    public int getRecipientCount() {
        return this.getRecipients().size() - 1;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(Date scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public List<Recipient> getRecipients() {
        if (this.recipients == null) {
            return this.recipients = Recipient.allRecipients(this.getId());
        } else if (this.recipients.isEmpty()) {
            return this.recipients = Recipient.allRecipients(this.getId());
        }
        return recipients;
    }

    public void setRecipients(List<Recipient> recipients) {
        if (getId() != null) {
            for (Recipient recipient : this.getRecipients()) {
                recipient.delete();
            }
            recipients = deDupe(recipients);
            this.recipients.clear();
        }
        this.recipients = recipients;
    }

    public List<Recipient> deDupe(List<Recipient> recipients) {
        HashMap<String, Recipient> r = new HashMap<>();
        for (Recipient recipient : recipients) {
            if (!r.containsKey(recipient.getPhone())) {
                r.put(recipient.getPhone(), recipient);
            }
        }
        List<Recipient> deDuped = new ArrayList<>();
        for (Recipient recipient : r.values()) {
            deDuped.add(recipient);
        }
        return deDuped;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean wasSelected() {
        return wasSelected;
    }

    public void setWasSelected(boolean wasSelected) {
        this.wasSelected = wasSelected;
    }

    @Override
    public int compareTo(@NonNull Message another) {
        return getScheduleDate().compareTo(another.getScheduleDate());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        final Message other = (Message) o;
        return !((this.getId() == null) ? (other.getId() != null) :
                !this.getId().equals(other.getId()));
    }

    /**
     * Parcelable - created with http://www.parcelabler.com/
     */

    protected Message(Parcel in) {
        long tmpCreated = in.readLong();
        created = tmpCreated != -1 ? new Date(tmpCreated) : null;
        long tmpScheduleDate = in.readLong();
        scheduleDate = tmpScheduleDate != -1 ? new Date(tmpScheduleDate) : null;
        repeat = in.readByte() != 0x00;
        category = in.readInt();
        message = in.readString();
        status = in.readInt();
        errorCode = in.readInt();
        selected = in.readByte() != 0x00;
        wasSelected = in.readByte() != 0x00;
        if (in.readByte() == 0x01) {
            recipients = new ArrayList<Recipient>();
            in.readList(recipients, Recipient.class.getClassLoader());
        } else {
            recipients = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(created != null ? created.getTime() : -1L);
        dest.writeLong(scheduleDate != null ? scheduleDate.getTime() : -1L);
        dest.writeByte((byte) (repeat ? 0x01 : 0x00));
        dest.writeInt(category);
        dest.writeString(message);
        dest.writeInt(status);
        dest.writeInt(errorCode);
        dest.writeByte((byte) (selected ? 0x01 : 0x00));
        dest.writeByte((byte) (wasSelected ? 0x01 : 0x00));
        if (recipients == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(recipients);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };
}