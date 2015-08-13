package ca.hoogit.hooold.Scheduling;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import ca.hoogit.hooold.BaseActivity;
import ca.hoogit.hooold.R;

public class CreateActivity extends BaseActivity {

    @Override
    protected int getToolbarColor() {
        return R.color.primary;
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

    }

//        Intent picker = new Intent(Intent.ACTION_PICK,
//                      ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
//        startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);
}
