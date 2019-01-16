package com.avinash.droppickhire.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Spinner;
import android.widget.Toast;

import com.avinash.droppickhire.R;
import com.avinash.droppickhire.adapters.SpinnerAdapter;
import com.avinash.droppickhire.helper.Constants;
import com.avinash.droppickhire.helper.FirebaseUtils;
import com.avinash.droppickhire.helper.Helper;
import com.avinash.droppickhire.helper.Preferences;
import com.avinash.droppickhire.pojo.JobSeeker;
import com.avinash.droppickhire.pojo.User;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class JobSeekerProfileActivity extends BaseActivity {

    @BindView(R.id.edt_first_name)
    MaterialEditText firstNameEdt;

    @BindView(R.id.edt_last_name)
    MaterialEditText lastNameEdt;

    @BindView(R.id.majors_spinner)
    Spinner majorsSpinner;

    @BindView(R.id.experience_spinner)
    Spinner experienceSpinner;

    @BindView(R.id.degree_spinner)
    Spinner degreeSpinner;

    private ProgressDialog progress;

    private JobSeeker jobSeeker;

    private DatabaseReference jobSeekersDB;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            // obtained read external storage permission
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            }
        }
    }

    @Override
    protected int setLayout() {
        return R.layout.activity_job_seeker_profile;
    }

    private void loadSpinnerWithAdapter(Spinner spinner, List<String> list) {
        SpinnerAdapter adapter = new SpinnerAdapter(JobSeekerProfileActivity.this, R.layout.item_spinner, list);
        spinner.setAdapter(adapter);
    }

    @OnClick(R.id.upload_resume_btn)
    public void uploadResumeBtn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            } else {
                // we have permission to read storage
                new MaterialFilePicker()
                        .withActivity(JobSeekerProfileActivity.this)
                        .withRequestCode(10)
                        .start();
            }
        }
    }

    @OnClick(R.id.submit_btn)
    public void submitJobSeekerProfile() {
        if (!TextUtils.isEmpty(firstNameEdt.getText().toString()) && !TextUtils.isEmpty(lastNameEdt.getText().toString())) {
            jobSeeker.setFirstName(firstNameEdt.getText().toString());
            jobSeeker.setLastName(lastNameEdt.getText().toString());

            List<String> selectedItems = null;
            SpinnerAdapter adapter = (SpinnerAdapter) degreeSpinner.getAdapter();
            selectedItems = adapter.getSelectedItems();
            if (selectedItems != null && selectedItems.size() == 1) {
                jobSeeker.setDegree(selectedItems.get(0));
                selectedItems = null;
                adapter = (SpinnerAdapter) majorsSpinner.getAdapter();
                selectedItems = adapter.getSelectedItems();
                if (selectedItems != null && selectedItems.size() == 1) {
                    jobSeeker.setMajor(selectedItems.get(0));
                    adapter = (SpinnerAdapter) experienceSpinner.getAdapter();
                    selectedItems = adapter.getSelectedItems();
                    if (selectedItems != null && selectedItems.size() == 1) {
                        // we have all the details
                        jobSeeker.setExperience(selectedItems.get(0));
                        jobSeeker.setId(new Gson().fromJson(Preferences.getIns().getStringValueForKey(Constants.USER), User.class).getId());

                        if (jobSeeker.getResume() != null && jobSeeker.getResume().size() > 0) {

                            jobSeekersDB = new FirebaseUtils().getJobSeekersDB();
                            Preferences.getIns().storeBooleanKeyValue(Constants.IS_JOB_SEEKER_PROFILE_COMPLETE, true);
                            Preferences.getIns().storeStringKeyValue(Constants.JOB_SEEKER_PROFILE, new Gson().toJson(jobSeeker));
                            jobSeekersDB.child(jobSeeker.getId()).setValue(jobSeeker);

                            Toast.makeText(JobSeekerProfileActivity.this,
                                    getResources().getString(R.string.profile_updated), Toast.LENGTH_LONG).show();
                            JobSeekerProfileActivity.this.finish();
                        } else {
                            Toast.makeText(JobSeekerProfileActivity.this,
                                    getResources().getString(R.string.upload_resume), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(JobSeekerProfileActivity.this,
                                getResources().getString(R.string.select_experience_one), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(JobSeekerProfileActivity.this,
                            getResources().getString(R.string.select_major_one), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(JobSeekerProfileActivity.this,
                        getResources().getString(R.string.select_degree_one), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(JobSeekerProfileActivity.this,
                    getResources().getString(R.string.enter_fn_ln), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        jobSeeker = new JobSeeker();

        loadSpinnerWithAdapter(degreeSpinner, Arrays.asList("Select Degree", "BS", "MS"));
        loadSpinnerWithAdapter(majorsSpinner, Arrays.asList("Select Majors", "Computer Engineering"
                , "Computer Science", "Software Engineering", "Electrical Engineering", "Information Science"));
        loadSpinnerWithAdapter(experienceSpinner, Arrays.asList("Experience Required", "0+", "1+", "2+"
                , "3+", "4+", "5+", "6+", "7+", "8+", "9+", "10+"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == 10 && resultCode == RESULT_OK) {

            progress = new ProgressDialog(JobSeekerProfileActivity.this);
            progress.setTitle("Uploading");
            progress.setMessage("Please wait...");
            progress.show();

            File file = new File(data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH));

            try {
                InputStream inputStream = new FileInputStream(file);
                String parsedText = "";
                PdfReader reader = new PdfReader(inputStream);
                int n = reader.getNumberOfPages();

                for (int i = 0; i < n; i++)
                    parsedText = parsedText + PdfTextExtractor.getTextFromPage(reader, i + 1).trim() + "\n";

                String[] parsedData = parsedText.split("\\s+");
                if (parsedData.length > 0) {
                    jobSeeker.setResume(new ArrayList<>(Arrays.asList(parsedData)));
                } else {
                    Toast.makeText(JobSeekerProfileActivity.this,
                            getResources().getString(R.string.invalid_file), Toast.LENGTH_LONG).show();
                }

                reader.close();
                progress.dismiss();

            } catch (FileNotFoundException e) {
                Toast.makeText(this, "File Not Found", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(this, "IO Error reading input stream", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
