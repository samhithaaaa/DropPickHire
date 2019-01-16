package com.avinash.droppickhire.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.avinash.droppickhire.R;
import com.avinash.droppickhire.adapters.RecyclerViewAdapter;
import com.avinash.droppickhire.helper.Constants;
import com.avinash.droppickhire.helper.FirebaseUtils;
import com.avinash.droppickhire.pojo.Event;
import com.avinash.droppickhire.pojo.User;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class EventsListActivity extends BaseActivity {

    private static final String TAG = EventsListActivity.class.getSimpleName();

    private User user;

    @BindView(R.id.events_lst)
    RecyclerView eventsLst;

    @BindView(R.id.create_event_btn)
    FloatingActionButton createEventBtn;

    @BindView(R.id.create_profile_btn)
    FloatingActionButton createProfileBtn;

    private RecyclerViewAdapter adapter;

    private DatabaseReference eventsDB;

    private List<Event> events = new ArrayList<>();

    private LatLng srcLatLng;

    @OnClick(R.id.create_event_btn)
    public void createEvent() {
        Intent intent = new Intent(EventsListActivity.this, EventCreationActivity.class);
        intent.putExtra(Constants.USER, user);
        startActivity(intent);
    }

    @OnClick(R.id.create_profile_btn)
    public void createJobSeekerProfile() {
        Intent intent = new Intent(EventsListActivity.this, JobSeekerProfileActivity.class);
        intent.putExtra(Constants.USER, user);
        startActivity(intent);
    }

    @Override
    protected int setLayout() {
        return R.layout.activity_events_list;
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = (User) getIntent().getSerializableExtra(Constants.USER);

        eventsDB = new FirebaseUtils().getEventsDB();

        if (!user.getIsRecruiter()) {
            @SuppressLint("MissingPermission") Task location = LocationServices.getFusedLocationProviderClient(EventsListActivity.this).getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    Location currentLocation = (Location) task.getResult();
                    srcLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    showEventsNearbyToJobSeeker(srcLatLng);
                }
            });
        }

        if (user.getIsRecruiter()) {
            createEventBtn.setVisibility(View.VISIBLE);
            createProfileBtn.setVisibility(View.INVISIBLE);
            showEventsCreatedByRecruiter();
        } else {
            createEventBtn.setVisibility(View.INVISIBLE);
            createProfileBtn.setVisibility(View.VISIBLE);
        }

    }

    private void showEventsCreatedByRecruiter() {
        eventsDB.orderByChild("creatorID").equalTo(user.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    events = new ArrayList<>();
                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        Event event = item.getValue(Event.class);
                        if (event != null) {
                            Log.e(TAG, event.toString());
                            events.add(event);
                        }
                    }
                    eventsLst.setHasFixedSize(true);

                    // use a linear layout manager
                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(EventsListActivity.this);
                    eventsLst.setLayoutManager(mLayoutManager);
                    if (events.size() > 0) {
                        adapter = new RecyclerViewAdapter(events, user.getIsRecruiter(), EventsListActivity.this, user.getId());
                        eventsLst.setAdapter(adapter);
                    } else {
                        String alert = "No Events found, try creating an event";
                        Toast.makeText(EventsListActivity.this, alert, Toast.LENGTH_LONG).show();
                    }
                } else {
                    String alert = "No Events found, try creating an event";
                    Toast.makeText(EventsListActivity.this, alert, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showEventsNearbyToJobSeeker(final LatLng srcLatLng) {
        eventsDB.orderByChild("creatorID").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        Event event = item.getValue(Event.class);
                        if (event != null) {
                            Log.e(TAG, event.toString());
                            if (getDistance(event.getLatitude(), event.getLongitude(), srcLatLng.latitude, srcLatLng.longitude) < 1610) {
                                events.add(event);
                            }
                        }
                    }
                    eventsLst.setHasFixedSize(true);

                    // use a linear layout manager
                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(EventsListActivity.this);
                    eventsLst.setLayoutManager(mLayoutManager);
                    if (events.size() > 0) {
                        adapter = new RecyclerViewAdapter(events, user.getIsRecruiter(), EventsListActivity.this, user.getId());
                        eventsLst.setAdapter(adapter);
                    } else {
                        String alert = "No Events found, try going closer to an event location";
                        Toast.makeText(EventsListActivity.this, alert, Toast.LENGTH_LONG).show();
                    }
                } else {
                    String alert = "No Events found, try going closer to an event location";
                    Toast.makeText(EventsListActivity.this, alert, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private int getDistance(Double fromLat, Double fromLng, double toLat, double toLng) {
        Location from = new Location("");
        from.setLatitude(fromLat);
        from.setLongitude(fromLng);

        Location to = new Location("");
        to.setLatitude(toLat);
        to.setLongitude(toLng);

        int distance = (int) from.distanceTo(to);
        Log.e("Calculating: ", "Distance between: " + fromLat + "," + fromLng + " and " + toLat + "," + toLng + " is: " + distance);
        return distance;
    }
}
