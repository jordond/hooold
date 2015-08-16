package ca.hoogit.hooold.Message;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
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
import android.widget.Toolbar;

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
    @Bind(R.id.recycler) RecyclerView mRecyclerView;
    @Bind(R.id.empty_list) TextView mEmptyListView;

    private View mRootView;
    private int mType;
    private IMessageInteraction mListener;

    private MessageAdapter mAdapter;
    private ArrayList<Message> mMessages = new ArrayList<>();
    private ArrayList<Message> mDeletedMessages = new ArrayList<>();

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
        List<Message> selected = Message.getSelected(mAdapter.getList());
        switch (selected.size()) {
            case 0:
                menuId = R.menu.menu_scheduled;
                break;
            case 1:
                menuId = R.menu.menu_scheduled_selected_single;
                break;
        }
        mListener.itemSelected(selected.size() > 0);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                delete();
                return true;
            case R.id.action_edit:
                reset();
                return true;
            case R.id.action_send_now:
                // TODO Implement sending message immediately
                Log.d(TAG, "TODO implement sending message immediately");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void reset() {
        List<Message> list = Message.getSelected(mAdapter.getList());
        mAdapter.unSelect(list);
        getActivity().invalidateOptionsMenu();
        mListener.itemSelected(false);
    }

    private void delete() {
        final List<Message> selected = Message.getSelected(mAdapter.getList());
        mDeletedMessages.clear();
        new MaterialDialog.Builder(getActivity())
                .title(getResources().getString(R.string.message_delete_title))
                .content(getResources().getString(R.string.message_delete_content)
                        + " " + selected.size() + " messages will be deleted.")
                .positiveText(getResources().getString(R.string.message_delete_positive))
                .negativeText(getResources().getString(R.string.message_delete_negative))
                .autoDismiss(true)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        mAdapter.delete(selected);
                        mDeletedMessages.addAll(selected);
                        showDeleteSnackbar();
                        toggleViews();
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
