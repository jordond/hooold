package ca.hoogit.hooold.Message;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.hoogit.hooold.R;
import ca.hoogit.hooold.Scheduling.SchedulingService;
import ca.hoogit.hooold.Scheduling.Sms;
import ca.hoogit.hooold.Utils.Consts;
import ca.hoogit.hooold.Views.EmptyRecyclerView;

public class MessageFragment extends Fragment implements MessageAdapter.OnCardAction {

    private String TAG = MessageFragment.class.getSimpleName();
    @Bind(R.id.recycler) EmptyRecyclerView mRecyclerView;
    @Bind(R.id.empty_list) TextView mEmptyListView;

    private View mRootView;
    private int mCategory;
    private IMessageInteraction mListener;

    private MessageAdapter mAdapter;
    private MessageList mMessages = new MessageList();
    private ArrayList<Message> mDeletedMessages = new ArrayList<>();

    private boolean isRecents;

    private BroadcastReceiver mMessageRefresh = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mAdapter != null) {
                long id = intent.getLongExtra(Consts.KEY_MESSAGE_ID, -1L);
                if (id != -1L) {
                    Log.i(TAG, "onReceive: Has ID attempting a move");
                    if (mCategory == Consts.MESSAGE_CATEGORY_SCHEDULED) {
                        Snackbar.make(mRecyclerView, "Message was sent",
                                Snackbar.LENGTH_SHORT).show();
                    }
                    mAdapter.move(id);
                } else {
                    Log.i(TAG, "onReceive: Is a general refresh");
                    refresh();
                }
            }
        }
    };

    public void refresh() {
        mAdapter.set(null);
    }

    public static MessageFragment newInstance(int category) {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        args.putInt(Consts.ARG_CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    public MessageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCategory = getArguments().getInt(Consts.ARG_CATEGORY);
            if (mCategory == Consts.MESSAGE_CATEGORY_SCHEDULED) {
                TAG += " S";
            } else {
                TAG += " R";
                isRecents = true;
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mAdapter != null) {
            if (mMessages != null && mMessages.isEmpty()) {
                mMessages = mAdapter.getList();
            }
        }
        outState.putParcelableArrayList(Consts.KEY_MESSAGES, mMessages);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            ArrayList<Message> test = savedInstanceState.getParcelableArrayList(Consts.KEY_MESSAGES);
            mMessages = (MessageList) test;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        ButterKnife.bind(this, view);

        setHasOptionsMenu(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setAddDuration(Consts.ANIMATION_LIST_ITEM_DELAY);
        animator.setRemoveDuration(Consts.ANIMATION_LIST_ITEM_DELAY);
        mRecyclerView.setItemAnimator(animator);

        return mRootView = view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (IMessageInteraction) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement IMessageInteraction");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mRecyclerView.getAdapter() == null) {
            mAdapter = new MessageAdapter(getActivity(), mCategory);
            mAdapter.setListener(this);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setEmptyView(mEmptyListView);
        }

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageRefresh,
                new IntentFilter(Consts.INTENT_MESSAGE_REFRESH));

        mAdapter.set(mMessages);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageRefresh);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        int menuId = R.menu.menu_blank;
        List<Message> selected = new ArrayList<>();
        if (mAdapter != null) {
            selected = mAdapter.getSelected();
        }
        if (mCategory == Consts.MESSAGE_CATEGORY_SCHEDULED) {
            if (selected.size() == 1) {
                menuId = R.menu.menu_scheduled_selected_single;
            } else if (selected.size() > 1) {
                menuId = R.menu.menu_scheduled_selected_multiple;
            }
        } else if (mCategory == Consts.MESSAGE_CATEGORY_RECENT) {
            if (selected.size() == 1) {
                menuId = R.menu.menu_recent_selected_single;
            } else if (selected.size() > 1) {
                menuId = R.menu.menu_recent_selected_multiple;
            } else {
                menuId = R.menu.menu_recent_none;
            }
        }
        mListener.itemSelected(selected.size() > 0);
        inflater.inflate(menuId, menu);
    }

    public void add(Message message) {
        if (mAdapter != null) {
            int position = mAdapter.add(message);
            mRecyclerView.scrollToPosition(position);
        }
    }

    public void update(Message message) {
        if (mAdapter != null) {
            int position = mAdapter.update(message);
            mRecyclerView.scrollToPosition(position);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                delete(mAdapter.getSelected());
                return true;
            case R.id.action_edit:
                List<Message> selected = mAdapter.getSelected(); //TODO put at top?
                if (selected != null && selected.size() == 1) {
                    reset();
                    mListener.editItem(selected.get(0));
                }
                return true;
            case R.id.action_send_now:
                List<Message> test = mAdapter.getSelected(); //TODO put at top?
                for (Message t : test) {
                    Sms sms = t.toSms();
                    SchedulingService.startDeleteMessage(getContext(), sms);
                    sms.send(getActivity());
                }
                reset();
                return true;
            case R.id.action_clear:
                delete(mAdapter.getList());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void reset() {
        mAdapter.unSelect();
        getActivity().invalidateOptionsMenu();
        mListener.itemSelected(false);
    }

    private void delete(final List<Message> messages) {
        String content = getResources().getString(R.string.message_delete_content);
        if (isRecents) {
            content = getResources().getString(R.string.message_delete_content_recent);
        }
        mDeletedMessages.clear();
        new MaterialDialog.Builder(getActivity())
                .title(getResources().getString(R.string.message_delete_title))
                .content(content + " " + messages.size() + " messages will be deleted.")
                .positiveText(getResources().getString(R.string.message_delete_positive))
                .negativeText(getResources().getString(R.string.message_delete_negative))
                .autoDismiss(true)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        for (Message message : messages) {
                            SchedulingService.startDeleteMessage(getContext(), message.toSms());
                        }
                        mAdapter.delete(messages);
                        mDeletedMessages.addAll(messages);
                        showDeleteSnackbar();
                        reset();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        dialog.dismiss();
                    }
                }).show();
    }

    public void showDeleteSnackbar() {
        Snackbar.make(mRootView, mDeletedMessages.size() + " " + getString(
                R.string.message_delete_snackbar_text), Snackbar.LENGTH_LONG).setAction(
                getResources().getString(R.string.snackbar_undo),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (Message message : mDeletedMessages) {
                            SchedulingService.startAddMessage(getContext(), message.toSms());
                        }
                        mAdapter.add(mDeletedMessages);
                    }
                }).show();
    }

    @Override
    public void cardSelected(View view, Message message) {
        getActivity().invalidateOptionsMenu();
    }

    public interface IMessageInteraction {
        void itemSelected(boolean isSelected);
        void editItem(Message message);
    }
}
