package com.avinash.droppickhire.helper;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.avinash.droppickhire.activities.App;


public class Preferences {

    private static Preferences pref;
    private static SharedPreferences prefObj;
    private static SharedPreferences.Editor prefsEditor;

    public static Preferences getIns() {
        if(pref == null) {
            pref = new Preferences();
            prefObj = PreferenceManager.getDefaultSharedPreferences(App.get());
            prefsEditor = prefObj.edit();
        }
        return pref;
    }

    public SharedPreferences getSharedPref() {
        if(prefObj == null) {
            prefObj = PreferenceManager.getDefaultSharedPreferences(App.get());
        }
        return prefObj;
    }

    public SharedPreferences.Editor getEditor() {
        if(prefsEditor == null) {
            prefsEditor = getSharedPref().edit();
        }
        return prefsEditor;
    }

    public void storeStringKeyValue(String key, String value) {
        getEditor().putString(key, value).commit();
    }

    public String getStringValueForKey(String key) {
        return getSharedPref().getString(key, "");
    }

    public void storeBooleanKeyValue(String key, boolean value) {
        getEditor().putBoolean(key, value).commit();
    }

    public boolean getBooleanValueForKey(String key) {
        return getSharedPref().getBoolean(key, false);
    }
}
