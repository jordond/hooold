package ca.hoogit.hooold.Main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.Date;

import butterknife.Bind;
import butterknife.OnClick;
import ca.hoogit.hooold.About.AboutActivity;
import ca.hoogit.hooold.BaseActivity;
import ca.hoogit.hooold.Message.Message;
import ca.hoogit.hooold.Message.MessageFragment;
import ca.hoogit.hooold.R;
import ca.hoogit.hooold.Utils.Consts;

public class MainActivity extends BaseActivity {

    @Bind(R.id.viewpager) ViewPager mPager;
    @Bind(R.id.tabs) TabLayout mTabs;
    @Bind(R.id.fab) FloatingActionButton mNewMessage;

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
        Message test = new Message(new Date(), new Date(), "Jordon DaHoooog");
        test.setType(Consts.MESSAGE_TYPE_SCHEDULED);
        test.setMessage("Hello you forgot to pay child support this month, also happy birthday");
        test.setRepeat(false);
        Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" +
                R.id.viewpager + ":" + mPager.getCurrentItem());
        if (mPager.getCurrentItem() == 0 && page != null) {
            ((MessageFragment)page).add(test);
        }
    }
}
