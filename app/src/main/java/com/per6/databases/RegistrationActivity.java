package com.mistershorr.databases;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

public class RegistrationActivity extends AppCompatActivity {

    public static final String TAG = RegistrationActivity.class.getSimpleName();
    public static final String EXTRA_USERNAME = "username";
    public static final String EXTRA_PASSWORD = "password";

    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextUsername;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private Button buttonCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // show the back button

        wireWidgets();
        setListeners();

        // preload the username (get the username from the intent)
        String username = getIntent().getStringExtra(LoginActivity.EXTRA_USERNAME);
        editTextUsername.setText(username);

    }

    private void setListeners() {
        buttonCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createBackendlessAccount();
            }
        });
    }

    private void createBackendlessAccount() {
        // TODO update to make this work with startActivityForResult

        // create account on backendless

        // only do this if all the fields aren't empty AND the passwords match
        if(isDataValid()) {
            BackendlessUser user = new BackendlessUser();
            user.setProperty("email", editTextEmail.getText().toString());
            user.setPassword(editTextPassword.getText().toString());
            user.setProperty("name", editTextName.getText().toString());
            user.setProperty("username", editTextUsername.getText().toString());

            Backendless.UserService.register(user, new AsyncCallback<BackendlessUser>() {
                public void handleResponse(BackendlessUser registeredUser) {
                    Toast.makeText(RegistrationActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                    String username = editTextUsername.getText().toString();
                    String password = editTextPassword.getText().toString();
                    Intent registrationCompleteIntent = new Intent();
                    registrationCompleteIntent.putExtra(EXTRA_USERNAME, username);
                    registrationCompleteIntent.putExtra(EXTRA_PASSWORD, password);
                    setResult(RESULT_OK, registrationCompleteIntent);
                    finish();
                }

                public void handleFault(BackendlessFault fault) {
                    Toast.makeText(RegistrationActivity.this, fault.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Make sure you fill in all fields and passwords match.", Toast.LENGTH_SHORT).show();
        }






    }

    private boolean isDataValid() {
       return !editTextName.getText().toString().isEmpty() &&
              !editTextPassword.getText().toString().isEmpty() &&
              !editTextConfirmPassword.getText().toString().isEmpty() &&
              !editTextEmail.getText().toString().isEmpty() &&
              !editTextUsername.getText().toString().isEmpty() &&
              editTextPassword.getText().toString().equals(editTextConfirmPassword.getText().toString());
    }

    private void wireWidgets() {
        editTextUsername = findViewById(R.id.edit_text_create_account_username);
        editTextPassword = findViewById(R.id.edit_text_create_account_password);
        editTextName = findViewById(R.id.edit_text_create_account_name);
        editTextEmail = findViewById(R.id.edit_text_create_account_email);
        editTextConfirmPassword = findViewById(R.id.edit_text_create_account_confirm_password);
        buttonCreateAccount = findViewById(R.id.button_create_account);
    }
}
