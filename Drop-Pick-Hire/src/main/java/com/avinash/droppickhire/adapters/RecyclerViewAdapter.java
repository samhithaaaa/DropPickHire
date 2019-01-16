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
import com.avinash.droppickhire.helper.Helper;
import com.avinash.droppickhire.helper.Preferences;
import com.avinash.droppickhire.pojo.Event;
import com.avinash.droppickhire.pojo.JobSeeker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private final List<Event> events;

    private final boolean isRecruiter;

    private final Context context;

    private final String userId;

    public RecyclerViewAdapter(List<Event> events, boolean isRecruiter, Context context, String userId) {
        this.events = events;
        this.isRecruiter = isRecruiter;
        this.context = context;
        this.userId = userId;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recycler_view, viewGroup, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.MyViewHolder viewHolder, final int i) {
        final Event event = events.get(i);
        viewHolder.title.setText("Event Name: " + event.getTitle());
        viewHolder.location.setText("Location: " + event.getLocation());
        viewHolder.date.setText("Date: " + event.getDate());
        viewHolder.time.setText("Time: " + event.getTime());
        viewHolder.event_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecruiter) {
                    navigateToEventsSubmissionsActivity(event);
                } else {
                    checkProfileStatusAndSubmit(event.getId());
                }
            }
        });
    }

    private void navigateToEventsSubmissionsActivity(Event event) {
        Intent intent = new Intent(context, SubmissionDetailsActivity.class);
        intent.putExtra(Constants.EVENT, event);
        context.startActivity(intent);
    }

    private void checkProfileStatusAndSubmit(String eventId) {
        if (Preferences.getIns().getBooleanValueForKey(Constants.IS_JOB_SEEKER_PROFILE_COMPLETE)) {
            showSubmitForEventDialog(eventId);
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.profile_incomplete), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(context, JobSeekerProfileActivity.class);
            context.startActivity(intent);
        }
    }

    private void showSubmitForEventDialog(final String eventId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Please confirm");
        builder.setMessage("Are you sure to submit your profile to this Event?");

        builder.setPositiveButton(R.string.submit_application, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String jobSeeker = Preferences.getIns().getStringValueForKey(Constants.JOB_SEEKER_PROFILE);
                JobSeeker submission = new Gson().fromJson(jobSeeker, JobSeeker.class);
                submission.setEventID(eventId);
                new FirebaseUtils().getSubmissionsDB().child(Helper.uniqueIdGenerator()).setValue(submission);
                Toast.makeText(context, context.getResources().getString(R.string.submitted), Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void clear() {
        final int size = events.size();
        events.clear();
        notifyItemRangeRemoved(0, size);
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
