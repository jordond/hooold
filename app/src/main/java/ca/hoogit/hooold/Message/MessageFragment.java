package ca.hoogit.hooold.Message;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import ca.hoogit.hooold.BaseActivity;
import ca.hoogit.hooold.R;
import ca.hoogit.hooold.Utils.Consts;

public class MessageFragment extends Fragment implements MessageAdapter.OnCardAction {

    private static final String TAG = MessageFragment.class.getSimpleName();
    @Bind(R.id.recycler)
    RecyclerView mRecyclerView;
    @Bind(R.id.empty_list)
    TextView mEmptyListView;

    private View mRootView;
    private int mType;

    private MessageAdapter mAdapter;
    private ArrayList<Message> mMessages = new ArrayList<>();
    private ArrayList<Message> mSelectedMessages = new ArrayList<>();
    private ArrayList<Message> mDeletedMessages = new ArrayList<>();
    private ArrayList<MessageAdapter.ViewHolder> mHolders = new ArrayList<>();

    public static MessageFragment newInstance(int type) {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        args.putInt(Consts.ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    public MessageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mType = getArguments().getInt(Consts.ARG_TYPE);
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
            mMessages = savedInstanceState.getParcelableArrayList(Consts.KEY_MESSAGES);
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
    public void onResume() {
        super.onResume();

        if (mRecyclerView.getAdapter() == null) {
            mAdapter = new MessageAdapter(getActivity(), mType);
            mAdapter.setListener(this);
            mRecyclerView.setAdapter(mAdapter);
            mEmptyListView.setVisibility(View.GONE);
        }

        mAdapter.update(mMessages);
        toggleViews();
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        int menuId = R.menu.menu_scheduled_selected_multiple;
        switch (mSelectedMessages.size()) {
            case 0:
                menuId = R.menu.menu_main;
                break;
            case 1:
                menuId = R.menu.menu_scheduled_selected_single;
                break;
        }

        try {
            BaseActivity activity = (BaseActivity) getActivity();
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(mSelectedMessages.size() > 0);
        } catch (NullPointerException e) {
            Log.e(TAG, "Failed to set displayHomeAsUpEnabled");
        }
        inflater.inflate(menuId, menu);
    }

    private void toggleViews() {
        if (mRecyclerView.getAdapter().getItemCount() != 0) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyListView.setVisibility(View.GONE);
        } else {
            mRecyclerView.setVisibility(View.GONE);
            mEmptyListView.setVisibility(View.VISIBLE);
        }
    }

    public void add(Message message) {
        if (mAdapter != null) {
            int position = mAdapter.add(message);
            mRecyclerView.scrollToPosition(position);
            toggleViews();

        }
    }

    @Override
    public void cardSelected(View view, Message message) {
        if (message != null) {
            if (!mSelectedMessages.contains(message)) {
                mSelectedMessages.add(message);
                mHolders.add((MessageAdapter.ViewHolder) view.getTag());
            } else {
                mSelectedMessages.remove(message);
                mHolders.remove((MessageAdapter.ViewHolder) view.getTag());

            }
            getActivity().invalidateOptionsMenu();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                resetHolderIcons();
                return true;
            case R.id.action_delete:
                delete();
                return true;
            case R.id.action_edit:
                return true;
            case R.id.action_send_now:
                resetHolderIcons();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void resetHolderIcons() {
        for (MessageAdapter.ViewHolder holder : mHolders) {
            holder.reset();
        }
        mSelectedMessages.clear();
        mHolders.clear();
        getActivity().invalidateOptionsMenu();
    }

    private void delete() {
        new MaterialDialog.Builder(getActivity())
                .title(getResources().getString(R.string.message_delete_title))
                .content(getResources().getString(R.string.message_delete_content)
                        + " " + mSelectedMessages.size() + " messages will be deleted.")
                .positiveText(getResources().getString(R.string.message_delete_positive))
                .negativeText(getResources().getString(R.string.message_delete_negative))
                .autoDismiss(true)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        mAdapter.delete(mSelectedMessages);
                        mDeletedMessages.addAll(mSelectedMessages);
                        showDeleteSnackbar();
                        mSelectedMessages.clear();
                        toggleViews();
                        resetHolderIcons();
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
                        mAdapter.add(mDeletedMessages);
                        mDeletedMessages.clear();
                    }
                }).show();
    }
}
