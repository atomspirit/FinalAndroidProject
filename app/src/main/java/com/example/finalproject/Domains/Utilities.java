package com.example.finalproject.Domains;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilities {
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
        String text = editText.getText().toString();
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
