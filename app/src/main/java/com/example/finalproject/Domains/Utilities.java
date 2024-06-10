package com.example.finalproject.Domains;


import android.content.Context;
import android.location.LocationManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class containing various methods for common tasks.
 */
public class Utilities {

    private Context context;

    /**
     * Constructs a new Utilities object with the given context.
     *
     * @param context The context to be used by the utility methods.
     */
    public Utilities(Context context) {
        this.context = context;
    }

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    /**
     * Hides the soft keyboard.
     *
     * @param context The context from which the keyboard should be hidden.
     * @param view    The view currently focused, whose window token will be used to hide the keyboard.
     */
    public static void hideKeyboard(Context context, View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * Validates an EditText field.
     *
     * @param editText     The EditText field to be validated.
     * @param errorMessage The error message to be displayed if validation fails.
     * @return True if the EditText field is not empty, false otherwise.
     */
    public static Boolean validateEditText(EditText editText, String errorMessage) {
        String text = editText.getText().toString().trim();
        if (text.isEmpty()) {
            editText.setError(errorMessage);
            return false;
        } else {
            editText.setError(null);
            return true;
        }
    }

    /**
     * Validates a password EditText field.
     *
     * @param etPassword The EditText field for password input.
     * @return True if the password field is not empty, false otherwise.
     */
    public static Boolean validatePassword(EditText etPassword) {
        return validateEditText(etPassword, "Password is required");
    }

    /**
     * Validates a username EditText field.
     *
     * @param etUsername The EditText field for username input.
     * @return True if the username field is not empty and does not contain restricted characters, false otherwise.
     */
    public static Boolean validateUsername(EditText etUsername) {
        String text = String.valueOf(etUsername.getText());
        if (text.contains(".") || text.contains("#") || text.contains("$")
                || text.contains("[") || text.contains("]") || text.contains("/")) {
            etUsername.setError("Cannot include '.','#','$','[',']' or '/'");
            return false;
        }
        return validateEditText(etUsername, "User name is required");
    }

    /**
     * Validates an email EditText field.
     *
     * @param etEmail The EditText field for email input.
     * @return True if the email field is not empty and contains a valid email address format, false otherwise.
     */
    public static Boolean validateEmail(EditText etEmail) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(etEmail.getText());

        if (!matcher.matches()) {
            etEmail.setError("Invalid email address");
            return false;
        }
        return validateEditText(etEmail, "Email is required");
    }

    /**
     * Checks if location services are enabled on the device.
     *
     * @return True if either GPS provider or network provider is enabled, false otherwise.
     */
    public boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }
        return false;
    }
}
