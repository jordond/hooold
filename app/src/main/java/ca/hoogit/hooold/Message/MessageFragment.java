package ca.hoogit.hooold.Message;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.hoogit.hooold.R;
import ca.hoogit.hooold.Utils.Consts;

public class MessageFragment extends Fragment {

    @Bind(R.id.recycler) RecyclerView mRecyclerView;
    @Bind(R.id.empty_list) TextView mEmptyListView;

    private int mType;

    private MessageAdapter mAdapter;
    private ArrayList<Message> mMessages;

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
    public void onResume() {
        super.onResume();
        setupList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        ButterKnife.bind(this, view);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new MessageAdapter(getActivity(), mType);

        mRecyclerView.setLayoutManager(layoutManager);
        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setAddDuration(Consts.ANIMATION_LIST_ITEM_DELAY);
        animator.setRemoveDuration(Consts.ANIMATION_LIST_ITEM_DELAY);
        mRecyclerView.setItemAnimator(animator);
        mRecyclerView.setAdapter(mAdapter);

        mMessages = new ArrayList<>();

        return view;
    }

    private void setupList() {
        boolean toggle = mAdapter.update(mMessages);
        toggleViews(toggle);
    }

    private void toggleViews(boolean toggle) {
        if (toggle) {
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
            toggleViews(true);

        }
    }
}
