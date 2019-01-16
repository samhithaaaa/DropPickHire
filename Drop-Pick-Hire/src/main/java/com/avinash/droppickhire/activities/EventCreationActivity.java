package com.avinash.droppickhire.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.DecimalFormat;

import com.avinash.droppickhire.R;
import com.avinash.droppickhire.adapters.SpinnerAdapter;
import com.avinash.droppickhire.helper.Constants;
import com.avinash.droppickhire.helper.FirebaseUtils;
import com.avinash.droppickhire.helper.Preferences;
import com.avinash.droppickhire.pojo.Event;
import com.avinash.droppickhire.pojo.User;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class EventCreationActivity extends BaseActivity implements com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener, com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener {

    private LatLng dstLatLng;

    @BindView(R.id.edt_location)
    MaterialEditText locationEdt;

    @BindView(R.id.edt_name)
    MaterialEditText nameEdt;

    @BindView(R.id.edt_date)
    MaterialEditText dateEdt;

    @BindView(R.id.edit_time)
    MaterialEditText timeEdt;

    private String time = "";

    private String date = "";

    private DatabaseReference eventsDB;

    private String creatorId;

    @BindView(R.id.degree_spinner)
    Spinner degreeSpinner;

    @BindView(R.id.majors_spinner)
    Spinner majorsSpinner;

    @BindView(R.id.skills_spinner)
    Spinner skillsSpinner;

    @BindView(R.id.experience_spinner)
    Spinner experienceSpinner;

    @OnClick(R.id.edt_date)
    public void setDate() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                EventCreationActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setVersion(DatePickerDialog.Version.VERSION_2);
        dpd.setAccentColor(getResources().getColor((R.color.bg)));
        dpd.show(getFragmentManager(), Constants.DATE_PICKER_DIALOG);
    }

    @OnClick(R.id.edit_time)
    public void setTime() {
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                EventCreationActivity.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                Constants.IS_24_HOURS
        );
        tpd.setVersion(TimePickerDialog.Version.VERSION_2);
        tpd.setAccentColor(getResources().getColor((R.color.bg)));
        tpd.show(getFragmentManager(), Constants.TIME_PICKER_DIALOG);
    }


    @OnClick(R.id.edt_location)
    public void setDstLocation() {
        Intent targetLocationIntent = new Intent(EventCreationActivity.this, MapsActivity.class);
        targetLocationIntent.putExtra(Constants.IS_RECRUITER, Preferences.getIns().getBooleanValueForKey(Constants.IS_LOGGED_IN));
        startActivityForResult(targetLocationIntent, Constants.RECRUITER_TARGET_LOCATION_REQUEST);
    }

    @OnClick(R.id.btn_submit)
    public void createEvent() {
        Event event = new Event();
        if (!TextUtils.isEmpty(nameEdt.getText().toString())) {
            if (!TextUtils.isEmpty(locationEdt.getText().toString()) && dstLatLng != null) {
                DecimalFormat df = new DecimalFormat("0.00");
                df.setMaximumFractionDigits(7);
                String lat = String.valueOf(df.format(dstLatLng.latitude)).replace(".", "");
                String lng = String.valueOf(df.format(dstLatLng.longitude)).replace(".", "");
                String id = lat + lng;
                if (!TextUtils.isEmpty(date) && !TextUtils.isEmpty(time)) {
                    List<String> selectedItems = null;
                    SpinnerAdapter adapter = (SpinnerAdapter) degreeSpinner.getAdapter();
                    selectedItems = adapter.getSelectedItems();
                    if (selectedItems != null && selectedItems.size() > 0) {
                        event.setDegrees(selectedItems);
                        selectedItems = null;
                        adapter = (SpinnerAdapter) majorsSpinner.getAdapter();
                        selectedItems = adapter.getSelectedItems();
                        if (selectedItems != null && selectedItems.size() > 0) {
                            event.setMajors(selectedItems);
                            selectedItems = null;
                            adapter = (SpinnerAdapter) skillsSpinner.getAdapter();
                            selectedItems = adapter.getSelectedItems();
                            if (selectedItems != null && selectedItems.size() > 0) {
                                event.setSkills(selectedItems);
                                selectedItems = null;
                                adapter = (SpinnerAdapter) experienceSpinner.getAdapter();
                                selectedItems = adapter.getSelectedItems();
                                if (selectedItems != null && selectedItems.size() > 0) {
                                    // we have all the details
                                    event.setExperience(selectedItems);
                                    event.setId(id);
                                    event.setTitle(nameEdt.getText().toString());
                                    event.setLocation(locationEdt.getText().toString());
                                    event.setLatitude(dstLatLng.latitude);
                                    event.setLongitude(dstLatLng.longitude);
                                    event.setDate(dateEdt.getText().toString());
                                    event.setTime(timeEdt.getText().toString());
                                    event.setCreatorID(creatorId);
                                    event.setNoOfSubmissions("0");

                                    eventsDB.child(event.getId()).setValue(event);
                                    EventCreationActivity.this.finish();
                                } else {
                                    Toast.makeText(EventCreationActivity.this,
                                            getResources().getString(R.string.select_experience), Toast.LENGTH_LONG).show();
                                }

                            } else {
                                Toast.makeText(EventCreationActivity.this,
                                        getResources().getString(R.string.select_skills), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(EventCreationActivity.this,
                                    getResources().getString(R.string.select_major), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(EventCreationActivity.this,
                                getResources().getString(R.string.select_degree), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(EventCreationActivity.this,
                            getResources().getString(R.string.enter_date_time), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(EventCreationActivity.this,
                        getResources().getString(R.string.select_location), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(EventCreationActivity.this,
                    getResources().getString(R.string.enter_title), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RECRUITER_TARGET_LOCATION_REQUEST) {
            if (resultCode == RESULT_OK) {
                dstLatLng = data.getParcelableExtra(Constants.DST_LAT_LON);
                String locationName = data.getStringExtra(Constants.NAME);
                locationEdt.setText(locationName);
                Log.e("latlng", dstLatLng.latitude + "-" + dstLatLng.longitude);
            } else {
                Toast.makeText(EventCreationActivity.this, getResources().getString(R.string.failed_to_get_location), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected int setLayout() {
        return R.layout.activity_event_creation;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        User user = (User) intent.getSerializableExtra(Constants.USER);
        creatorId = user.getId();

        eventsDB = new FirebaseUtils().getEventsDB();

        loadSpinnerWithAdapter(degreeSpinner, Arrays.asList("Select Degree", "BS", "MS"));
        loadSpinnerWithAdapter(majorsSpinner, Arrays.asList("Select Majors", "Computer Engineering"
                , "Computer Science", "Software Engineering", "Electrical Engineering", "Information Science"));
        loadSpinnerWithAdapter(skillsSpinner, Arrays.asList("Select Skills", "Java", "C", "C++"
                , "Python", "javascript", "jQuery", "Android", "iOS", "AWS", "Jenkins", "React JS", "Node JS"
                , "Angualr JS", "PHP", "SQL", "Mongo DB"));
        loadSpinnerWithAdapter(experienceSpinner, Arrays.asList("Experience Required", "0+", "1+", "2+"
                , "3+", "4+", "5+", "6+", "7+", "8+", "9+", "10+"));
    }

    private void loadSpinnerWithAdapter(Spinner spinner, List<String> list) {
        SpinnerAdapter adapter = new SpinnerAdapter(EventCreationActivity.this, R.layout.item_spinner, list);
        spinner.setAdapter(adapter);
    }


    @Override
    public void onDateSet(com.wdullaer.materialdatetimepicker.date.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        dateEdt.setText(date);

    }

    @Override
    public void onTimeSet(com.wdullaer.materialdatetimepicker.time.TimePickerDialog view, int hourOfDay, int minute, int second) {
        time = hourOfDay + ":" + minute;
        timeEdt.setText(time);
    }
}