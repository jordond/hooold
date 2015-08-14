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

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.hoogit.hooold.R;
import ca.hoogit.hooold.Utils.Consts;
import ca.hoogit.hooold.Utils.HoooldUtils;

/**
 * @author jordon
 *         <p/>
 *         Date    11/08/15
 *         Description
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private ArrayList<Message> mMessages;
    private Context mContext;
    private View mView;
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

    public int add(Message message) {
        int position = 0;
        if (message != null) {
            message.save();
            mMessages.add(message);
            Collections.sort(mMessages);
            Collections.reverse(mMessages);
            position = mMessages.indexOf(message);
            notifyItemInserted(position);
        }
        return position;
    }

    public void delete(int position) {
        Message message = mMessages.get(position);
        if (message != null) {
            message.delete();
            mMessages.remove(position);
            notifyItemRemoved(position);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        int layoutId = R.layout.item_message_scheduled;
        if (mType == Consts.MESSAGE_TYPE_RECENT) {
            layoutId = R.layout.item_message_recent;
        }
        mView = inflater.inflate(layoutId, parent, false);
        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message message = mMessages.get(position);

        List<Recipient> recipients = message.getRecipients();
        if (recipients != null && !recipients.isEmpty()) {
            String title = recipients.get(0).getName();
            if (recipients.size() > 1) {
                title = title + " +" + (recipients.size() - 1);
            }
            holder.recipient.setText(title);
        } else {
            recipients = new ArrayList<>();
        }
        holder.date.setText(HoooldUtils.toListDate(message.getScheduleDate()));
        holder.message.setText(message.getMessage());

        int color = mUnusedColors.get(0);
        mUsedColors.add(color);
        mUnusedColors.remove(0);

        if (mUnusedColors.isEmpty()) {
            mUnusedColors.addAll(mUsedColors);
            mUsedColors.clear();
        }

        GradientDrawable backgroundGradient = (GradientDrawable) holder.icon.getBackground();
        backgroundGradient.setColor(color);

        String url = "";
        for (Recipient recipient : recipients) {
            if (recipient.getPictureUrl() !=  null) {
                url = recipient.getPictureUrl();
                break;
            }
        }
        if (url.isEmpty()) {
            holder.icon.setImageResource(R.drawable.ic_action_sms);
        } else {
            holder.icon.setImageURI(Uri.parse(url));
        }

    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView icon;
        TextView recipient, date, message;
        Button edit, delete;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            recipient = (TextView) itemView.findViewById(R.id.contact);
            date = (TextView) itemView.findViewById(R.id.date);
            message = (TextView) itemView.findViewById(R.id.message);
            if (mType == Consts.MESSAGE_TYPE_SCHEDULED) {
                edit = (Button) itemView.findViewById(R.id.edit);
                delete = (Button) itemView.findViewById(R.id.delete);

                edit.setOnClickListener(this);
                delete.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
            final int position = getAdapterPosition();
            switch (v.getId()) {
                case R.id.edit:
                    break;
                case R.id.delete:
                    new MaterialDialog.Builder(mContext)
                            .title(R.string.message_delete_title)
                            .content(R.string.message_delete_content)
                            .positiveText(R.string.message_delete_positive)
                            .negativeText(R.string.message_delete_negative)
                            .autoDismiss(true)
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    super.onPositive(dialog);
                                    final Message message = mMessages.get(position);
                                    delete(position);
                                    Snackbar.make(mView, mContext.getString(R.string.message_delete_snackbar_text), Snackbar.LENGTH_LONG)
                                            .setAction(R.string.snackbar_undo,
                                                    new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            add(message);
                                                        }
                                                    }).show();
                                }

                                @Override
                                public void onNegative(MaterialDialog dialog) {
                                    super.onNegative(dialog);
                                    dialog.dismiss();
                                }
                            }).show();
                    break;
            }
        }
    }
}