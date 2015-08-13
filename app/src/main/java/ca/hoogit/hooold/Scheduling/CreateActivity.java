package ca.hoogit.hooold.Scheduling;

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.android.ex.chips.BaseRecipientAdapter;
import com.android.ex.chips.RecipientEditTextView;
import com.android.ex.chips.recipientchip.DrawableRecipientChip;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.OnClick;
import ca.hoogit.hooold.BaseActivity;
import ca.hoogit.hooold.R;
import ca.hoogit.hooold.Utils.Consts;
import ca.hoogit.hooold.Utils.HoooldUtils;

public class CreateActivity extends BaseActivity
        implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private static final String TAG = CreateActivity.class.getSimpleName();

    @Bind(R.id.contact) RecipientEditTextView mContact;
    @Bind(R.id.date) TextView mDate;
    @Bind(R.id.message) EditText mMessageText;

    private String mAction;

    private Calendar mScheduledDate;


    @Override
    protected int getToolbarColor() {
        return getResources().getColor(R.color.primary);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_create;
    }

    @Override
    protected int getMenuResource() {
        return R.menu.menu_create;
    }

    @Override
    protected boolean getDisplayHomeAsUpEnabled() {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_create:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAction = getIntent().getAction();
        getToolbar().setTitle(mAction + getString(R.string.activity_create_title_suffix));

        mContact.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        BaseRecipientAdapter adapter = new BaseRecipientAdapter(BaseRecipientAdapter.QUERY_TYPE_PHONE, this);
        adapter.setShowMobileOnly(true);
        mContact.setAdapter(adapter);
        mContact.dismissDropDownOnItemSelected(true);
    }

    @OnClick(R.id.date)
    public void dateOnClick(View v) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                CreateActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setMinDate(now);
        dpd.show(getFragmentManager(), Consts.FRAGMENT_TAG_DATETIME_PICKER);
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        mScheduledDate = Calendar.getInstance();
        mScheduledDate.set(Calendar.YEAR, year);
        mScheduledDate.set(Calendar.MONTH, month);
        mScheduledDate.set(Calendar.DAY_OF_MONTH, day);

        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                CreateActivity.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
        );
        tpd.show(getFragmentManager(), Consts.FRAGMENT_TAG_DATETIME_PICKER);
    }

    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int hour, int minute) {
        mScheduledDate.set(Calendar.HOUR_OF_DAY, hour);
        mScheduledDate.set(Calendar.MINUTE, minute);

        mDate.setText(HoooldUtils.toListDate(mScheduledDate.getTime()));
    }
}
