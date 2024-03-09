package com.example.finalproject.Domains;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class Utilities {
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
        return validateEditText(etEmail, "Email is required");
    }

}
