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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
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

    private static final String TAG = MessageAdapter.class.getSimpleName();
    private Context mContext;
    private int mCategory;
    private OnCardAction mListener;

    private MessageList mMessages;

    private ArrayList<Integer> mUnusedColors;
    private ArrayList<Integer> mUsedColors;

    public MessageAdapter(Context context, int category) {
        mContext = context;
        mCategory = category;
        init();
    }

    private void init() {
        mMessages = new MessageList();
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

    public MessageList getList() {
        if (mMessages != null) {
            if (mMessages.isEmpty()) {
                mMessages = new MessageList();
            }
        }
        return mMessages;
    }

    public boolean set(MessageList messages) {
        if (messages == null || messages.isEmpty()) {
            mMessages = Message.all(mCategory);
        } else {
            mMessages = messages;
        }
        if (getItemCount() != 0) {
            notifyDataSetChanged();
            return true;
        }
        return false;
    }

    public void unSelect() {
        for (Message message : getSelected()) {
            if (message.isSelected()) {
                int position = mMessages.indexOf(message);
                Message m = mMessages.get(position);
                m.setSelected(false);
                m.setWasSelected(true);
                notifyItemChanged(position);
            }
        }
    }

    public ArrayList<Message> getSelected() {
        ArrayList<Message> messages = new ArrayList<>();
        for (Message message : mMessages) {
            if (message.isSelected()) {
                messages.add(message);
            }
        }
        return messages;
    }

    public void swap(MessageList list) {
        mMessages.clear();
        mMessages.addAll(list);
        notifyDataSetChanged();
    }

    public int add(Message message) {
        int position = 0;
        if (message != null && message.getCategory() == mCategory) {
            message.save();
            mMessages.add(message);
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

    public int update(Message message) {
        int position = mMessages.find(message);
        if (position != -1) {
            message.save();
            if (mCategory == Consts.MESSAGE_CATEGORY_RECENT) {
                mMessages.remove(position);
                notifyItemRemoved(position);
            } else {
                message = mMessages.set(position, message, true);
                notifyItemChanged(mMessages.indexOf(message));
            }
        } else {
            add(message);
        }
        return position;
    }

    public void delete(Message message) {
        int position = mMessages.indexOf(message);
        if (position !=  -1) {
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

    public boolean move(long messageId) {
        int position = mMessages.find(messageId);
        if (position != -1) {
            Log.d(TAG, "move: Removing at position: " + position);
            mMessages.remove(position);
            notifyItemRemoved(position);
            return true;
        } else {
            Message message = Message.findById(Message.class, messageId);
            if (message != null && message.getCategory() == mCategory) {
                Log.d(TAG, "move: Adding message to list");
                mMessages.add(message);
                notifyItemInserted(mMessages.indexOf(message));
                return true;
            }
        }
        return false;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        int layoutId = R.layout.item_message_scheduled;
        if (mCategory == Consts.MESSAGE_CATEGORY_RECENT) {
            layoutId = R.layout.item_message_recent;
        }
        View view = inflater.inflate(layoutId, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message message = mMessages.get(position);

        holder.recipient.setText(message.getTitle());
        holder.date.setText(HoooldUtils.toListDate(message.getScheduleDate()));
        holder.message.setText(message.getMessage());
        if (message.isRepeat()) {
            holder.repeat.setVisibility(View.VISIBLE);
        }

        if (mCategory == Consts.MESSAGE_CATEGORY_SCHEDULED) {
            setupScheduled(holder, message);
        } else if (mCategory == Consts.MESSAGE_CATEGORY_RECENT) {
            setupRecent(holder, message);
        }

        if (message.isSelected()) {
            holder.iconReverse.setVisibility(View.VISIBLE);
            holder.selected = true;
        } else if (message.wasSelected()) {
            holder.iconReverse.setVisibility(View.VISIBLE);
            holder.selected = false;
            holder.animator.reset(true);
            message.setWasSelected(false);
            mMessages.set(position, message);
        } else {
            holder.animator.reset(holder.selected);
            holder.selected = false;
        }

        GradientDrawable gradient = (GradientDrawable) holder.iconReverse.getBackground();
        gradient.setColor(mContext.getResources().getColor(Consts.SELECTED_ITEM_COLOR));
    }

    public void setupScheduled(ViewHolder holder, Message message) {
        int recipCount = message.getRecipientCount();
        if (recipCount >= 1) {
            holder.recipientNum.setText("+" + recipCount);
            holder.recipientNum.setVisibility(View.VISIBLE);
            holder.hasExtraRecipient = true;
        } else {
            holder.recipientNum.setVisibility(View.INVISIBLE);
            holder.hasExtraRecipient = false;
        }

        GradientDrawable backgroundGradient = (GradientDrawable) holder.icon.getBackground();
        backgroundGradient.setColor(getIconColor());

        String url = "";
        for (Recipient recipient : message.getRecipients()) {
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

    public void setupRecent(ViewHolder holder, Message message) {
        GradientDrawable gradient = (GradientDrawable) holder.icon.getBackground();
        int colorId = R.color.md_green_700;
        int iconId = R.drawable.ic_action_thumb_up;
        if (message.getStatus() == Consts.MESSAGE_STATUS_FAILED) {
            holder.status.setText("FAILED " + message.getErrorCode());
            colorId = R.color.md_red_700;
            iconId = R.drawable.ic_alert_error;
        } else {
            holder.status.setText("Success");
        }
        gradient.setColor(mContext.getResources().getColor(colorId));
        holder.icon.setImageResource(iconId);

    }

    public int getIconColor() {
        int color = mUnusedColors.get(0);
        mUsedColors.add(color);
        mUnusedColors.remove(0);

        if (mUnusedColors.isEmpty()) {
            mUnusedColors.addAll(mUsedColors);
            mUsedColors.clear();
        }
        return color;
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, IconAnimator.OnAnimationComplete {

        CardView layout;
        ImageView icon, iconReverse, repeat;
        TextView recipient, date, message, recipientNum, status;

        IconAnimator animator;

        boolean hasExtraRecipient;
        boolean selected;
        boolean isScheduled;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            iconReverse = (ImageView) itemView.findViewById(R.id.icon_reverse);
            repeat = (ImageView) itemView.findViewById(R.id.repeat);
            recipient = (TextView) itemView.findViewById(R.id.contact);
            date = (TextView) itemView.findViewById(R.id.date);
            message = (TextView) itemView.findViewById(R.id.message);
            recipientNum = (TextView) itemView.findViewById(R.id.recipient_num);
            status = (TextView) itemView.findViewById(R.id.status);

            isScheduled = Consts.MESSAGE_CATEGORY_SCHEDULED == mCategory;

            animator = new IconAnimator(mContext, icon, iconReverse);
            if (isScheduled) {
                layout = (CardView) itemView.findViewById(R.id.card);
                layout.setOnClickListener(this);
                animator.setListener(this);
            }

            icon.setOnClickListener(this);
            iconReverse.setOnClickListener(this);
        }

        @Override
        public void onClick(final View v) {
            switch (v.getId()) {
                case R.id.card:
                case R.id.icon:
                case R.id.icon_reverse:
                    if (isScheduled) {
                        recipientNum.setVisibility(View.INVISIBLE);
                    }
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