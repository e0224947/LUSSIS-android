package com.sa45team7.lussis.helpers;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.sa45team7.lussis.rest.model.Employee;

import org.json.JSONException;

import static com.sa45team7.lussis.LUSSISApplication.getAppContext;

/**
 * Created by Ton That Minh Nhat on 1/17/18.
 * Helper class to get current logged-in user information
 */

public class UserManager {

    private Employee currentEmployee;

    private static final UserManager ourInstance = new UserManager();

    public static UserManager getInstance() {
        return ourInstance;
    }

    private UserManager() {
    }

    public void setCurrentEmployee(Employee employee) {
        currentEmployee = employee;
    }

    public Employee getCurrentEmployee() {
        return currentEmployee;
    }

    public void clear() {
        currentEmployee = null;
    }

    public void cacheUser() {
        synchronized (this) {
            if (currentEmployee != null) {
                Gson gson = new Gson();
                String json = gson.toJson(currentEmployee);
                Log.e("TestJson", json);

                SharedPreferences.Editor editor = PreferenceManager.
                        getDefaultSharedPreferences(getAppContext()).edit();
                editor.putString("employee_data", json);
                editor.apply();
            }

        }
    }

    public void loadUserFromLocal() throws JSONException {
        synchronized (this) {
            // read string json from local disk
            String userData = PreferenceManager.
                    getDefaultSharedPreferences(getAppContext()).getString("user_data", null);

            // convert string to JSON Object
            Gson gson = new Gson();

            // Get user with JSON Object
            if (userData != null)
                currentEmployee = gson.fromJson(userData, Employee.class);
        }
    }

    public boolean hasCurrentEmployee(){
        try {
            loadUserFromLocal();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return currentEmployee != null;
    }
}
