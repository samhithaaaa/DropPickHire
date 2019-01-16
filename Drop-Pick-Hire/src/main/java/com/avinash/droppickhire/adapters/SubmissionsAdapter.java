package com.avinash.droppickhire.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.avinash.droppickhire.R;
import com.avinash.droppickhire.activities.JobSeekerProfileActivity;
import com.avinash.droppickhire.activities.SubmissionDetailsActivity;
import com.avinash.droppickhire.helper.Constants;
import com.avinash.droppickhire.helper.FirebaseUtils;
import com.avinash.droppickhire.helper.Preferences;
import com.avinash.droppickhire.pojo.Event;
import com.avinash.droppickhire.pojo.JobSeeker;
import com.google.gson.Gson;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SubmissionsAdapter extends RecyclerView.Adapter<SubmissionsAdapter.MyViewHolder> {

    private final List<JobSeeker> jobSeekers;

    private final Context context;

    public SubmissionsAdapter(List<JobSeeker> jobSeekers, Context context) {
        this.jobSeekers = jobSeekers;
        this.context = context;
    }

    @NonNull
    @Override
    public SubmissionsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recycler_view, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubmissionsAdapter.MyViewHolder viewHolder, final int i) {
        final JobSeeker jobSeeker = jobSeekers.get(i);
        viewHolder.title.setText("Name: " + jobSeeker.getFirstName() + " " + jobSeeker.getLastName());
        viewHolder.location.setText("Experience: " + jobSeeker.getExperience());
        viewHolder.date.setText("Education: " + jobSeeker.getDegree() + " in " + jobSeeker.getMajor());
        viewHolder.time.setText("Match: " + jobSeeker.getMatch());
    }

    @Override
    public int getItemCount() {
        return jobSeekers.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title_txt)
        TextView title;

        @BindView(R.id.location_txt)
        TextView location;

        @BindView(R.id.date_txt)
        TextView date;

        @BindView(R.id.time_txt)
        TextView time;

        @BindView(R.id.card_view)
        CardView event_item;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
