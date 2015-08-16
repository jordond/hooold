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
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.android.ex.chips.RecipientEntry;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.orm.dsl.Table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
    private Date scheduleDate;
    private boolean repeat;
    private int type; // TODO rename to status add type (facebook,twitter,etc)
    private String message;

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
        this.type = Consts.MESSAGE_TYPE_SCHEDULED;
        this.scheduleDate = scheduleDate;
    }

    public Message(Date scheduleDate, List<Recipient> recipients) {
        this.created = new Date();
        this.type = Consts.MESSAGE_TYPE_SCHEDULED;
        this.scheduleDate = scheduleDate;
        this.recipients = recipients;
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
        if (type == Consts.MESSAGE_TYPE_ALL) {
            return all();
        }
        List<Message> list = Message.find(Message.class, "type = ?", String.valueOf(type));
        MessageList messages = new MessageList();
        for (Message message : list) {
            message.getRecipients();
            messages.add(message, false);
        }
        messages.sort();
        return messages;
    }

    public List<RecipientEntry> getRecipientEntries() {
        List<RecipientEntry> list = new ArrayList<>();
        for (Recipient recipient : this.getRecipients()) {
            RecipientEntry entry;
            if (recipient.getPictureUrl() != null) {
                entry = RecipientEntry.constructGeneratedEntry(
                            recipient.getName(),
                            recipient.getPhone(),
                            Uri.parse(recipient.getPictureUrl()),
                            true);
            } else {
                entry = RecipientEntry.constructGeneratedEntry(
                        recipient.getName(), recipient.getPhone(), true);
            }
            list.add(entry);
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

    @Override
    public long save() {
        this.selected = false;
        this.wasSelected = false;
        long id = super.save();
        if (recipients == null) {
            recipients = Recipient.allRecipients(id);
        }
        for (Recipient entry : recipients) {
            entry.setMessage(id);
            entry.save();
        }
        return id;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<Recipient> getRecipients() {
        if (this.recipients == null) {
            return this.recipients = Recipient.allRecipients(this.getId());
        }
        return recipients;
    }

    public void setRecipients(List<Recipient> recipients) {
        if (this.recipients != null) {
            for (Recipient recipient : this.recipients) {
                if (!recipients.contains(recipient)) {
                    recipient.delete();
                }
            }
        }
        this.recipients = recipients;
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
        type = in.readInt();
        message = in.readString();
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
        dest.writeInt(type);
        dest.writeString(message);
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