package com.avinash.droppickhire.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.avinash.droppickhire.R;
import com.avinash.droppickhire.helper.Constants;
import com.avinash.droppickhire.helper.FirebaseUtils;
import com.avinash.droppickhire.helper.Helper;
import com.avinash.droppickhire.helper.Preferences;
import com.avinash.droppickhire.pojo.User;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.rengwuxian.materialedittext.MaterialEditText;

import butterknife.BindView;
import butterknife.OnClick;

public class SignInActivity extends BaseActivity {

    @BindView(R.id.edt_username)
    MaterialEditText usernameEdt;

    @BindView(R.id.edt_password)
    MaterialEditText passwordEdt;

    @BindView(R.id.tab_signin)
    TextView signInTab;

    @BindView(R.id.tab_signup)
    TextView signUpTab;

    @BindView(R.id.tab_recruiter)
    TextView recruiterTab;

    @BindView(R.id.tab_job_seeker)
    TextView jobSeekerTab;

    private boolean isSignIn = true;

    private boolean isRecruiter = true;

    private DatabaseReference usersDB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        if (Preferences.getIns().getBooleanValueForKey(Constants.IS_LOGGED_IN)) {
            User user = new Gson().fromJson(Preferences.getIns().getStringValueForKey(Constants.USER), User.class);
            Intent intent = new Intent(SignInActivity.this, EventsListActivity.class);
            intent.putExtra(Constants.USER, user);
            startActivity(intent);
            SignInActivity.this.finish();
        } else {
            usersDB = new FirebaseUtils().getUserDB();
            passwordEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if ((actionId == EditorInfo.IME_ACTION_DONE)) {
                        signInOrUp();
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    @OnClick(R.id.txt_signin)
    public void signIn() {
        isSignIn = true;
        signInTab.setVisibility(View.VISIBLE);
        signUpTab.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.txt_signup)
    public void signUp() {
        isSignIn = false;
        signUpTab.setVisibility(View.VISIBLE);
        signInTab.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.txt_recruiter)
    public void recruiter() {
        isRecruiter = true;
        recruiterTab.setVisibility(View.VISIBLE);
        jobSeekerTab.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.txt_job_seeker)
    public void jobSeeker() {
        isRecruiter = false;
        jobSeekerTab.setVisibility(View.VISIBLE);
        recruiterTab.setVisibility(View.INVISIBLE);
    }


    @OnClick(R.id.btn_send)
    public void signInOrUp() {
        String username = usernameEdt.getText().toString();
        String password = passwordEdt.getText().toString();

        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            Log.i("ID, PWD:", username + "--" + password);
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setIsRecruiter(isRecruiter);
            String userId = Helper.uniqueIdGenerator();
            user.setId(userId);
            if (isSignIn) {
                doSignIn(user);
            } else {
                doSignUp(user);
            }
        } else {
            Toast.makeText(SignInActivity.this, getString(R.string.network_problem), Toast.LENGTH_LONG).show();
        }

    }

    private void doSignUp(final User user) {
        usersDB.orderByChild("username").equalTo(user.getUsername()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(SignInActivity.this, getString(R.string.user_exists), Toast.LENGTH_LONG).show();
                } else {
                    usersDB.child(user.getId()).setValue(user);
                    Intent intent = new Intent(SignInActivity.this, EventsListActivity.class);
                    intent.putExtra(Constants.USER, user);
                    Toast.makeText(SignInActivity.this, getString(R.string.sign_up_success), Toast.LENGTH_LONG).show();
                    Preferences.getIns().storeBooleanKeyValue(Constants.IS_LOGGED_IN, true);
                    Preferences.getIns().storeStringKeyValue(Constants.USER, new Gson().toJson(user));
                    startActivity(intent);
                    SignInActivity.this.finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void doSignIn(final User user) {
        usersDB.orderByChild("username").equalTo(user.getUsername()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        User remoteUserObj = data.getValue(User.class);
                        if (remoteUserObj.getPassword().equals(user.getPassword())) {
                            Intent intent = new Intent(SignInActivity.this, EventsListActivity.class);
                            intent.putExtra(Constants.USER, remoteUserObj);
                            Toast.makeText(SignInActivity.this, getString(R.string.sign_in_success), Toast.LENGTH_LONG).show();
                            Preferences.getIns().storeBooleanKeyValue(Constants.IS_LOGGED_IN, true);
                            Preferences.getIns().storeStringKeyValue(Constants.USER, new Gson().toJson(remoteUserObj));
                            startActivity(intent);
                            SignInActivity.this.finish();
                        } else {
                            Toast.makeText(SignInActivity.this, getResources().getString(R.string.no_account_found), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected int setLayout() {
        return R.layout.layout_sign_in;
    }

}
