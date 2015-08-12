package ca.hoogit.hooold;

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

import butterknife.Bind;
import butterknife.ButterKnife;

public class MessageFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_TYPE = "type";
    private static final String ARG_PARAM2 = "param2";

    private static final String KEY_MESSAGES = "MESSAGES";

    // TODO: Rename and change types of parameters
    private int mType;
    private String mParam2;

    @Bind(R.id.recycler) RecyclerView mRecyclerView;
    @Bind(R.id.empty_list) TextView mEmptyListView;

    private MessageAdapter mAdapter;
    private ArrayList<Message> mMessages; // Saved and restored, so db call not needed on config change

    // TODO rename
    public static MessageFragment newInstance(int type, String param2) {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, type);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MessageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mType = getArguments().getInt(ARG_TYPE);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mAdapter != null) {
            if (mMessages != null && mMessages.isEmpty())
            mMessages = mAdapter.getList();
        }
        outState.putParcelableArrayList(KEY_MESSAGES, mMessages);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            mMessages = savedInstanceState.getParcelableArrayList(KEY_MESSAGES); // TODO Make sure its working may have to do work in mainActivity
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
        animator.setAddDuration(1000);
        animator.setRemoveDuration(1000);
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
            mAdapter.add(message);
            toggleViews(true);
        }
    }
}
