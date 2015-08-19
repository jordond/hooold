package ca.hoogit.hooold.Main;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import butterknife.Bind;
import butterknife.OnClick;
import ca.hoogit.hooold.About.AboutActivity;
import ca.hoogit.hooold.BaseActivity;
import ca.hoogit.hooold.Message.Message;
import ca.hoogit.hooold.Message.MessageFragment;
import ca.hoogit.hooold.R;
import ca.hoogit.hooold.Scheduling.CreateActivity;
import ca.hoogit.hooold.Utils.Consts;

public class MainActivity extends BaseActivity implements MessageFragment.IMessageInteraction {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Bind(R.id.viewpager) ViewPager mPager;
    @Bind(R.id.tabs) TabLayout mTabs;
    @Bind(R.id.fab) FloatingActionButton mButton;

    @Override
    protected int getToolbarColor() {
        return getResources().getColor(R.color.primary);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    protected int getMenuResource() {
        return R.menu.menu_main;
    }

    @Override
    protected boolean getDisplayHomeAsUpEnabled() {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            case android.R.id.home:
                MessageFragment frag = currentPage();
                if (frag != null) {
                    frag.reset();
                }
                return true;
            case R.id.test:
                Intent refresh = new Intent(Consts.INTENT_MESSAGE_REFRESH);
                LocalBroadcastManager.getInstance(this).sendBroadcast(refresh);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpViews();
    }

    private void setUpViews() {
        if (mPager.getAdapter() == null) {
            FragmentAdapter adapter = new FragmentAdapter(this, getSupportFragmentManager());
            mPager.setAdapter(adapter);
        }
        mTabs.setupWithViewPager(mPager);
    }

    @OnClick(R.id.fab)
    public void onClick(View view) {
        Intent createIntent = new Intent(this, CreateActivity.class);
        createIntent.setAction(Consts.MESSAGE_CREATE);
        startActivityForResult(createIntent, Consts.RESULT_MESSAGE_CREATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Consts.RESULT_MESSAGE_CREATE:
                case Consts.RESULT_MESSAGE_EDIT:
                    Log.d(TAG, "Create activity has finished");
                    Message message = data.getParcelableExtra(Consts.KEY_MESSAGE);
                    MessageFragment frag = getPage(Consts.MESSAGE_CATEGORY_SCHEDULED);
                    if (frag != null) {
                        boolean isEdit = Consts.RESULT_MESSAGE_EDIT == requestCode;
                        if (isEdit) {
                            long id = data.getLongExtra(Consts.KEY_MESSAGE_ID, -1);
                            Intent refresh = new Intent(Consts.INTENT_MESSAGE_REFRESH);
                            refresh.putExtra(Consts.KEY_MESSAGE_ID, id);
                            LocalBroadcastManager.getInstance(this).sendBroadcast(refresh);
                        } else {
                            frag.add(message);
                        }
                    }
                    break;
            }
        } else {
            Log.i(TAG, "Activity was canceled or failed");
        }
    }

    private MessageFragment currentPage() {
        return getPage(mPager.getCurrentItem());
    }

    private MessageFragment getPage(int position) {
        Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" +
                R.id.viewpager + ":" + position);
        if (page != null) {
            return (MessageFragment) page;
        }
        return null;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void itemSelected(boolean isSelected) {
        int color = getResources().getColor(R.color.primary);
        if (isSelected) {
            color = getResources().getColor(Consts.SELECTED_ITEM_COLOR);
        }
        getToolbar().setBackgroundColor(color);
        mTabs.setBackgroundColor(color);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(isSelected);
            getSupportActionBar().setHomeButtonEnabled(isSelected);
        } catch (NullPointerException e) {
            Log.e(TAG, "Failed to set displayHomeAsUpEnabled");
        }
        if (isSelected) {
            mButton.setVisibility(View.INVISIBLE);
        } else {
            mButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void editItem(Message message) {
        Intent editIntent = new Intent(this, CreateActivity.class);
        editIntent.setAction(Consts.MESSAGE_EDIT);
        editIntent.putExtra(Consts.KEY_MESSAGE_ID, message.getId());
        startActivityForResult(editIntent, Consts.RESULT_MESSAGE_EDIT);
    }
}
