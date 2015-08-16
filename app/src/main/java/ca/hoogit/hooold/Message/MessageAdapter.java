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
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.hoogit.hooold.R;
import ca.hoogit.hooold.Utils.Consts;
import ca.hoogit.hooold.Utils.HoooldUtils;
import ca.hoogit.hooold.Utils.IconAnimator;

/**
 * @author jordon
 *         <p/>
 *         Date    11/08/15
 *         Description
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private ArrayList<Message> mMessages;
    private Context mContext;
    private int mType;
    private OnCardAction mListener;

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

    public void setListener(OnCardAction listener) {
        this.mListener = listener;
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

    public void unSelect(List<Message> messages) {
        for (Message message : messages) {
            if (message.isSelected()) {
                int position = mMessages.indexOf(message);
                Message m = mMessages.get(position);
                m.setSelected(false);
                m.setWasSelected(true);
                notifyItemChanged(position);
            }
        }
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

    public void add(List<Message> messages) {
        if (messages != null) {
            for (Message message : messages) {
                add(message);
            }
        }
    }

    public void delete(Message message) {
        int position = mMessages.indexOf(message);
        if (position !=  -1) {
            message.setSelected(false);
            message.setWasSelected(false);
            message.delete();
            mMessages.remove(message);
            notifyItemRemoved(position);
        }
    }

    public void delete(List<Message> messages) {
        if (messages != null) {
            if (!messages.isEmpty()) {
                for (Message message : messages) {
                    delete(message);
                }
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        int layoutId = R.layout.item_message_scheduled;
        if (mType == Consts.MESSAGE_TYPE_RECENT) {
            layoutId = R.layout.item_message_recent;
        }
        View view = inflater.inflate(layoutId, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message message = mMessages.get(position);

        List<Recipient> recipients = message.getRecipients();
        if (recipients != null && !recipients.isEmpty()) {
            String title = recipients.get(0).getName();
            if (recipients.size() > 1) {
                String extra = "+" + (recipients.size() - 1);
                title = title + " " + extra;
                holder.recipientNum.setText(extra);
                holder.recipientNum.setVisibility(View.VISIBLE);
                holder.hasExtraRecipient = true;
            } else {
                holder.recipientNum.setVisibility(View.INVISIBLE);
                holder.hasExtraRecipient = false;
            }
            holder.recipient.setText(title);
        } else {
            recipients = new ArrayList<>();
        }
        if (message.isRepeat()) {
            holder.repeat.setVisibility(View.VISIBLE);
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

        //holder.selected = message.isSelected();
        if (message.isSelected()) {
            holder.iconReverse.setVisibility(View.VISIBLE);
            holder.selected = true;
        } else if (message.wasSelected()) {
            holder.iconReverse.setVisibility(View.VISIBLE);
            holder.selected = false;
            holder.animator.reset(true);
            message.setWasSelected(false);
            mMessages.set(position, message);
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
            Bitmap icon = HoooldUtils.uriToBitmap(mContext, Uri.parse(url));
            if (icon != null) {
                holder.icon.setImageBitmap(icon);
            }
        }

    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, IconAnimator.OnAnimationComplete {

        CardView layout;
        ImageView icon, iconReverse, repeat;
        TextView recipient, date, message, recipientNum;

        IconAnimator animator;

        boolean hasExtraRecipient;
        boolean selected;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            iconReverse = (ImageView) itemView.findViewById(R.id.icon_reverse);
            repeat = (ImageView) itemView.findViewById(R.id.repeat);
            recipient = (TextView) itemView.findViewById(R.id.contact);
            date = (TextView) itemView.findViewById(R.id.date);
            message = (TextView) itemView.findViewById(R.id.message);
            recipientNum = (TextView) itemView.findViewById(R.id.recipient_num);

            if (mType == Consts.MESSAGE_TYPE_SCHEDULED) {
                layout = (CardView) itemView.findViewById(R.id.card);
                layout.setOnClickListener(this);
            }

            icon.setOnClickListener(this);
            iconReverse.setOnClickListener(this);

            animator = new IconAnimator(mContext, icon, iconReverse);
            animator.setListener(this);

            icon.setTag(this);
            iconReverse.setTag(this);
        }

        @Override
        public void onClick(final View v) {
            switch (v.getId()) {
                case R.id.card:
                case R.id.icon:
                case R.id.icon_reverse:
                    recipientNum.setVisibility(View.INVISIBLE);
                    animator.start(selected);
                    mMessages.get(getAdapterPosition()).setSelected(selected = !selected);
                    if (mListener != null) {
                        mListener.cardSelected(v, mMessages.get(getAdapterPosition()));
                    }
                    break;
            }
        }

        @Override
        public void animationCompleted(boolean isReversed) {
            if (hasExtraRecipient) {
                int visibility = isReversed ? View.INVISIBLE : View.VISIBLE;
                recipientNum.setVisibility(visibility);
            } else {
                recipientNum.setVisibility(View.INVISIBLE);
            }
        }
    }

    public interface OnCardAction {
        void cardSelected(View view, Message message);
    }
}