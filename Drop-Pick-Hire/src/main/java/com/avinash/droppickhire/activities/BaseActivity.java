package com.avinash.droppickhire.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.avinash.droppickhire.R;
import com.avinash.droppickhire.helper.Constants;
import com.avinash.droppickhire.helper.Preferences;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setLayout());
        ButterKnife.bind(this);
    }

    protected abstract int setLayout();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        Intent i = new Intent(BaseActivity.this, SignInActivity.class);
        Preferences.getIns().storeStringKeyValue(Constants.JOB_SEEKER_PROFILE, "");
        Preferences.getIns().storeStringKeyValue(Constants.USER, "");
        Preferences.getIns().storeBooleanKeyValue(Constants.IS_JOB_SEEKER_PROFILE_COMPLETE, false);
        Preferences.getIns().storeBooleanKeyValue(Constants.IS_LOGGED_IN, false);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Preferences.getIns().storeStringKeyValue(Constants.USER, "");
        Preferences.getIns().storeBooleanKeyValue(Constants.IS_LOGGED_IN, false);
        startActivity(i);
    }
}
