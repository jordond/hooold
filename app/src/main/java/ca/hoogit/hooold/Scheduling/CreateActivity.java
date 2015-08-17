package ca.hoogit.hooold.Scheduling;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.android.ex.chips.BaseRecipientAdapter;
import com.android.ex.chips.RecipientEditTextView;
import com.android.ex.chips.RecipientEntry;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import ca.hoogit.hooold.BaseActivity;
import ca.hoogit.hooold.Message.Message;
import ca.hoogit.hooold.Message.Recipient;
import ca.hoogit.hooold.R;
import ca.hoogit.hooold.Utils.Consts;
import ca.hoogit.hooold.Utils.HoooldUtils;

public class CreateActivity extends BaseActivity
        implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private static final String TAG = CreateActivity.class.getSimpleName();

    @Bind(R.id.contact) RecipientEditTextView mContact;
    @Bind(R.id.date) TextView mDate;
    @Bind(R.id.message) EditText mMessageText;

    private boolean mIsEdit;
    private long mMessageId;
    private Message mMessage;

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
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.action_create).setVisible(!mIsEdit);
        menu.findItem(R.id.action_edit).setVisible(mIsEdit);
        return result;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_create:
            case R.id.action_edit:
                List<Recipient> list = Recipient.chipsToRecipients(mContact.getRecipients());

                if (isValid(list)) {
                    if (mMessage == null) {
                        mMessage = new Message(mScheduledDate.getTime());
                    } else {
                        mMessage.setScheduleDate(mScheduledDate.getTime());
                    }
                    mMessage.setRecipients(list);
                    // TODO implement repeat message
                    mMessage.setMessage(mMessageText.getText().toString());

                    Intent result = new Intent();
                    result.putExtra(Consts.KEY_MESSAGE, mMessage);
                    result.putExtra(Consts.KEY_MESSAGE_ID, mMessageId);

                    setResult(RESULT_OK, result);
                    finish();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isValid(List<Recipient> recipients) {
        String message = "";
        int count = 0;
        if (recipients == null || recipients.isEmpty()) {
            message = "No recipients were chosen";
            count++;
        }

        Calendar now = Calendar.getInstance();
        if (mScheduledDate == null) {
            message = "No scheduled date was chosen";
            count++;
        } else {
            if (now.compareTo(mScheduledDate) != -1) {
                message = "Scheduled date is not in the future";
                count ++;
            }
        }

        String messageContent = mMessageText.getText().toString();
        if (messageContent.isEmpty()) {
            message = "Please add a message to send";
            count ++;
        }

        if (count > 0) {
            makeSnackbar(message);
            return false;
        }
        return true;
    }

    public void makeSnackbar(String validationMessage) {
        View view = getWindow().getDecorView().findViewById(R.id.fields);
        Snackbar.make(view, validationMessage, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String mAction = getIntent().getAction();
        getToolbar().setTitle(mAction + getString(R.string.activity_create_title_suffix));

        mIsEdit = mAction.equals(Consts.MESSAGE_EDIT);

        mContact.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        BaseRecipientAdapter adapter = new BaseRecipientAdapter(
                BaseRecipientAdapter.QUERY_TYPE_PHONE, this);
        adapter.setShowMobileOnly(true);
        mContact.setAdapter(adapter);
        mContact.dismissDropDownOnItemSelected(true);

        if (mIsEdit) {
            mMessageId = getIntent().getExtras().getLong(Consts.KEY_MESSAGE_ID);
            Message message = Message.findById(Message.class, mMessageId);
            populate(message);
        } else {
            setDate(new Date());
        }
    }

    public void populate(Message message) {
        if (message != null) {
            List<RecipientEntry> recipients = message.getRecipientEntries();
            mContact.setRecipientEntries(recipients);
            setDate(message.getScheduleDate());
            mDate.setText(HoooldUtils.toFancyDate(message.getScheduleDate()));
            mMessageText.setText(message.getMessage());
            mMessage = message;
        }
    }

    public void setDate(Date date) {
        mScheduledDate = Calendar.getInstance();
        mScheduledDate.setTime(date);
    }

    @OnClick(R.id.date)
    public void dateOnClick(View v) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

        DatePickerDialog dpd = DatePickerDialog.newInstance(
                CreateActivity.this,
                mScheduledDate.get(Calendar.YEAR),
                mScheduledDate.get(Calendar.MONTH),
                mScheduledDate.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setMinDate(Calendar.getInstance());
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
                now.get(Calendar.MINUTE) + 1,
                false
        );
        tpd.show(getFragmentManager(), Consts.FRAGMENT_TAG_DATETIME_PICKER);
    }

    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int hour, int minute) {
        mScheduledDate.set(Calendar.HOUR_OF_DAY, hour);
        mScheduledDate.set(Calendar.MINUTE, minute);

        mDate.setText(HoooldUtils.toFancyDate(mScheduledDate.getTime()));
    }
}
