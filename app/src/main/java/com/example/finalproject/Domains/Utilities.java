package com.example.finalproject.Domains;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilities {

    private Context context;
    private final SharedPreferences spPermissions;
    SharedPreferences.Editor editorPermissions;
    private final static int REQUEST_GROUP_PERMISSION = 500;
    private final static String PERMISSIONS_TAG = "PERMISSIONS";
    private Map<String,String> availablePermissions;


    public Utilities(Context context)
    {
        this.context = context;
        spPermissions = context.getSharedPreferences("permissions", Context.MODE_PRIVATE);
        editorPermissions = spPermissions.edit();
        setAvailablePermissions();
    }

    private void setAvailablePermissions()
    {
        availablePermissions = new HashMap<>();
        availablePermissions.put(Manifest.permission.BLUETOOTH_SCAN, "bluetooth_scan");
        availablePermissions.put(Manifest.permission.BLUETOOTH_ADVERTISE, "bluetooth_advertise");
        availablePermissions.put(Manifest.permission.ACCESS_COARSE_LOCATION, "access_coarse_location");
        availablePermissions.put(Manifest.permission.ACCESS_FINE_LOCATION, "access_fine_location");
        availablePermissions.put(Manifest.permission.NEARBY_WIFI_DEVICES, "nearby_wifi_devices");
        availablePermissions.put(Manifest.permission.ACCESS_WIFI_STATE, "access_wifi_state");
        availablePermissions.put(Manifest.permission.CHANGE_WIFI_STATE, "change_wifi_state");

    }


    // Run-Time Permissions -----------------------------------------------------------------------
    public void updatePermissionPreference(String permission) {

        if(availablePermissions.containsKey(permission))
        {
            editorPermissions.putBoolean(availablePermissions.get(permission), true);
            editorPermissions.commit();
        }
    }

    public boolean checkPermissionPreference(String permission){
        if(availablePermissions.containsKey(permission))
        {
            return spPermissions.getBoolean(availablePermissions.get(permission), false);
        }
        Log.d(PERMISSIONS_TAG,  permission + ", granted? " + (ContextCompat.checkSelfPermission(context,permission) == PackageManager.PERMISSION_GRANTED));
        return false;
    }
    public void requestAllPermissions(ArrayList<String> permissions, Activity activity)
    {
        if(permissions != null && permissions.size() > 0)
        {
            Log.d(PERMISSIONS_TAG,  "requesting all perms");

            ArrayList<String> permsToRequest = new ArrayList<>();

            for(String perm : permissions)
            {
                //if(!checkPermissionPreference(perm)) permsToRequest.add(perm);
            }
            // TODO: Potential Bug! the user disallow a perm but i still updated the perm.

            if(permsToRequest.size() == 0) {
                return;
            }
            String[] permissionGroups = new String[permsToRequest.size()];
            permsToRequest.toArray(permissionGroups);
            ActivityCompat.requestPermissions(activity,permissionGroups,REQUEST_GROUP_PERMISSION);

            for(String permission : permsToRequest)
            {
                Log.d(PERMISSIONS_TAG,permission);
                updatePermissionPreference(permission);
            }
        }
    }
    public void requestAllPermissionsAvailable(Activity activity)
    {
        ArrayList<String> perms = new ArrayList<>(availablePermissions.keySet());
        requestAllPermissions(perms,activity);
    }

    public boolean gotAllPermissions()
    {
        Log.d(PERMISSIONS_TAG,"checking got all perms");

        boolean gotAllPerms = true;
        for(String perm : availablePermissions.keySet())
        {
            if(!checkPermissionPreference(perm))
                gotAllPerms = false;
            Log.d(PERMISSIONS_TAG,perm + ", got it? " + checkPermissionPreference(perm));
        }
        return gotAllPerms;
    }


    // EditText -----------------------------------------------------------------------------------

    // Regular expression pattern for validating email addresses
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";


    public static void hideKeyboard(Context context, View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    public static Boolean validateEditText(EditText editText, String errorMessage){
        String text = editText.getText().toString().trim();
        if(text.isEmpty()){
            editText.setError(errorMessage);
            return false;
        } else {
            editText.setError(null);
            return true;
        }
    }

    public static Boolean validatePassword(EditText etPassword){
        return validateEditText(etPassword, "Password is required");
    }
    public static Boolean validateUsername(EditText etUsername){
        return validateEditText(etUsername, "User name is required");
    }
    public static Boolean validateEmail(EditText etEmail){
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(etEmail.getText());

        if(!matcher.matches()) {
            etEmail.setError("Invalid email address");
            return false;
        }
        return validateEditText(etEmail, "Email is required");
    }



}
