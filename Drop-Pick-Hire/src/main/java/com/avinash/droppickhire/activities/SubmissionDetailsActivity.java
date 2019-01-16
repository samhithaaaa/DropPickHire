package com.avinash.droppickhire.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.avinash.droppickhire.R;
import com.avinash.droppickhire.adapters.RecyclerViewAdapter;
import com.avinash.droppickhire.adapters.SubmissionsAdapter;
import com.avinash.droppickhire.helper.Constants;
import com.avinash.droppickhire.helper.FirebaseUtils;
import com.avinash.droppickhire.pojo.Event;
import com.avinash.droppickhire.pojo.JobSeeker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class SubmissionDetailsActivity extends BaseActivity {

    @BindView(R.id.profiles_lst)
    RecyclerView profilesLst;

    @Override
    protected int setLayout() {
        return R.layout.activity_submission_details;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Event event = (Event) getIntent().getSerializableExtra(Constants.EVENT);
        final String eventId = event.getId();

        new FirebaseUtils().getSubmissionsDB().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<JobSeeker> jobSeekers = new ArrayList<>();
                    for (DataSnapshot item : dataSnapshot.getChildren()) {

                        JobSeeker jobSeeker = item.getValue(JobSeeker.class);
                        if (jobSeeker.getEventID().equals(eventId)) {
                            int match = 0;

                            for (String skill : event.getSkills()) {
                                if (jobSeeker.getResume().indexOf(skill) != -1) {
                                    match++;
                                }
                            }

                            String matchPercent = String.valueOf((float) match / event.getSkills().size() * 100);
                            matchPercent = matchPercent.length() > 4 ? matchPercent.substring(0, 5) : matchPercent;
                            jobSeeker.setMatch(matchPercent);
                            jobSeekers.add(jobSeeker);
                        }
                    }
                    if (jobSeekers.size() > 0) {
                        SubmissionsAdapter adapter = new SubmissionsAdapter(jobSeekers, SubmissionDetailsActivity.this);

                        LinearLayoutManager mLayoutManager = new LinearLayoutManager(SubmissionDetailsActivity.this);
                        profilesLst.setLayoutManager(mLayoutManager);

                        profilesLst.setAdapter(adapter);
                    } else {
                        Toast.makeText(SubmissionDetailsActivity.this, getResources().getString(R.string.no_submissions), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
