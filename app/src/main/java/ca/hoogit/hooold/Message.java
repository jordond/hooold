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
package ca.hoogit.hooold;

import android.os.Parcel;
import android.os.Parcelable;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.orm.dsl.Table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author jordon
 *
 * Date    11/08/15
 * Description
 *
 */
@Table
public class Message extends SugarRecord implements Parcelable, Comparable<Message> {

    private Date created;
    private Date scheduleDate;
    private boolean repeat;
    private int type;
    private String contact;
    private String message;

    public Message() {

    }

    public Message(Date created, Date scheduleDate, String contact) {
        this.created = created;
        this.scheduleDate = scheduleDate;
        this.contact = contact;
    }

    /**
     * DB Methods
     */

    public static ArrayList<Message> all() {
        List<Message> list = Message.listAll(Message.class);
        return new ArrayList<>(list);
    }

    public static ArrayList<Message> all(int type) {
        if (type == Consts.MESSAGE_TYPE_ALL) {
            return all();
        }
        ArrayList<Message> sorted = new ArrayList<>();
        for (Message message : Message.listAll(Message.class)) {
            if (message.getType() == type) {
                sorted.add(message);
            }
        }
        Collections.sort(sorted);
        Collections.reverse(sorted);
        return sorted;
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

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
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

    /**
     * Parcelable - created with http://www.parcelabler.com/
     */

    protected Message(Parcel in) {
        long tmpCreated = in.readLong();
        created = tmpCreated != -1 ? new Date(tmpCreated) : null;
        long tmpScheduleDate = in.readLong();
        scheduleDate = tmpScheduleDate != -1 ? new Date(tmpScheduleDate) : null;
        repeat = in.readByte() != 0x00;
        contact = in.readString();
        message = in.readString();
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
        dest.writeString(contact);
        dest.writeString(message);
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

    @Override
    public int compareTo(Message another) {
        return getScheduleDate().compareTo(another.getScheduleDate());
    }
}
