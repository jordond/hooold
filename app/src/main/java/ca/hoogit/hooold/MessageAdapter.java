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

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.primitives.Ints;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author jordon
 *
 * Date    11/08/15
 * Description
 *
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private ArrayList<Message> mMessages;
    private Context mContext;
    private int mType;

    private ArrayList<Integer> mUnusedColors;
    private ArrayList<Integer> mUsedColors;

    public MessageAdapter(Context context, int type) {
        mContext = context;
        mType = type;
        init();
    }

    private void init() {
        mMessages = new ArrayList<>();
        mUnusedColors = new ArrayList<>();
        mUsedColors = new ArrayList<>();

        int[] colors = mContext.getResources().getIntArray(R.array.colors);
        for (int color : colors) {
            mUnusedColors.add(color);
        }
    }

    public ArrayList<Message> getList() {
        if (mMessages != null) {
            if (mMessages.isEmpty()) {
                mMessages = new ArrayList<>();
            }
        }
        return mMessages;
    }

    public boolean update(ArrayList<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            mMessages = new ArrayList<>();
            mMessages = Message.all(mType);
        } else {
            mMessages = messages;
        }
        if (getItemCount() != 0) {
            notifyDataSetChanged();
            return true;
        }
        return false;
    }

    public boolean update() {
        mMessages = Message.all(mType);
        if (getItemCount() != 0) {
            notifyDataSetChanged();
            return true;
        }
        return false;
    }

    public void swap(ArrayList<Message> list) {
        mMessages.clear();
        mMessages.addAll(list);
        notifyDataSetChanged();
    }

    public void add(Message message) {
        if (message != null) {
            message.save();
            mMessages.add(message);
            notifyItemInserted(mMessages.size());
        }
    }

    public void remove(Message message) {
        if (message != null) {
            int position = mMessages.indexOf(message);
            if (position != -1) {
                message.delete();
                mMessages.remove(message);
                notifyItemRemoved(mMessages.indexOf(message));
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message message = mMessages.get(position);

        holder.contact.setText(message.getContact());
        holder.date.setText(HoooldUtils.toListDate(message.getScheduleDate()));
        holder.message.setText(message.getMessage());

        int color = mUnusedColors.get(0);
        mUsedColors.add(color);
        mUnusedColors.remove(0);

        if (mUnusedColors.isEmpty()) {
            mUnusedColors.addAll(mUsedColors);
            mUsedColors.clear();
        }

        GradientDrawable backgroundGradient = (GradientDrawable)holder.icon.getBackground();
        backgroundGradient.setColor(color);
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView contact, date, message;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            contact = (TextView) itemView.findViewById(R.id.contact);
            date = (TextView) itemView.findViewById(R.id.date);
            message = (TextView) itemView.findViewById(R.id.message);
        }
    }
}