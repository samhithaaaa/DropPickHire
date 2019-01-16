package com.avinash.droppickhire.helper;

import android.provider.ContactsContract;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseUtils {

    private FirebaseDatabase mFirebaseDatabase;

    private DatabaseReference mDatabaseReference;

    private DatabaseReference userDB;

    private DatabaseReference eventsDB;

    private DatabaseReference jobSeekersDB;

    private DatabaseReference submissionsDB;

    public FirebaseUtils() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        userDB = mDatabaseReference.child("users");
        eventsDB = mDatabaseReference.child("events");
        jobSeekersDB = mDatabaseReference.child("jobSeeker");
        submissionsDB = mDatabaseReference.child("submissions");
    }

    public DatabaseReference getUserDB() {
        return userDB;
    }

    public DatabaseReference getEventsDB() {
        return eventsDB;
    }

    public DatabaseReference getJobSeekersDB() {
        return jobSeekersDB;
    }


    public DatabaseReference getSubmissionsDB() {
        return submissionsDB;
    }
}
