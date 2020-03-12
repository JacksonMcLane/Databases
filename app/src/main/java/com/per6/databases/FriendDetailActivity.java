package com.per6.databases;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

public class FriendDetailActivity extends AppCompatActivity {

    private EditText editTextName;
    private SeekBar seekBarClumsiness;
    private Switch switchAwesome;
    private SeekBar seekBarGymFrequency;
    private RatingBar ratingBarTrustworthiness;
    private EditText editTextMoneyOwed;
    private Button buttonUpdate;
    private Friend foundFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_detail);
        wireWidgets();
        Intent foundIntent = getIntent();
        foundFriend = foundIntent.getParcelableExtra(FriendListActivity.EXTRA_FRIEND);
        if (foundFriend == null) {
            buttonUpdate.setText(R.string.create);
            newFriend();
        }
        else {
            editTextName.setText(foundFriend.getName());
            seekBarClumsiness.setProgress(foundFriend.getClumsiness());
            switchAwesome.setChecked(foundFriend.getAwesome());
            seekBarGymFrequency.setProgress((int)(foundFriend.getGymFrequency() * 2));
            ratingBarTrustworthiness.setProgress(foundFriend.getTrustworthiness());
            editTextMoneyOwed.setText(String.valueOf(foundFriend.getMoneyOwed()));
            setButtonListener();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options_menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu_item_detail_options_log_out){
            logoutUser();
            return true;
        }
        else{
            return super.onOptionsItemSelected(item);
        }
    }

    private void logoutUser() {
        Intent logoutIntent = new Intent(FriendDetailActivity.this, LoginActivity.class);
        Backendless.UserService.logout(new AsyncCallback<Void>() {
            @Override
            public void handleResponse(Void response) {

            }

            @Override
            public void handleFault(BackendlessFault fault) {

            }
        });
        startActivity(logoutIntent);
    }

    private void newFriend() {
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                foundFriend = new Friend();
                foundFriend.setOwnerId(Backendless.UserService.CurrentUser().getUserId());
                foundFriend.setName(String.valueOf(editTextName.getText()));
                foundFriend.setClumsiness(seekBarClumsiness.getProgress());
                foundFriend.setAwesome(switchAwesome.isChecked());
                foundFriend.setGymFrequency((seekBarGymFrequency.getProgress()/2.0));
                foundFriend.setTrustworthiness(ratingBarTrustworthiness.getProgress());
                foundFriend.setMoneyOwed(Double.valueOf(String.valueOf(editTextMoneyOwed.getText())));
                Backendless.Persistence.save(foundFriend, new AsyncCallback<Friend>() {
                    @Override
                    public void handleResponse(Friend response) {
                        Toast.makeText(FriendDetailActivity.this, "Successfully Made Friend", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    @Override
                    public void handleFault(BackendlessFault fault) {
                        fault.getMessage();
                    }
                });
            }
        });
    }


    private void setButtonListener() {
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                foundFriend.setName(String.valueOf(editTextName.getText()));
                foundFriend.setClumsiness(seekBarClumsiness.getProgress());
                foundFriend.setAwesome(switchAwesome.isChecked());
                foundFriend.setGymFrequency((seekBarGymFrequency.getProgress()/2.0));
                foundFriend.setTrustworthiness(ratingBarTrustworthiness.getProgress());
                foundFriend.setMoneyOwed(Double.valueOf(String.valueOf(editTextMoneyOwed.getText())));
                Backendless.Persistence.save(foundFriend, new AsyncCallback<Friend>() {
                    @Override
                    public void handleResponse(Friend response) {
                        Toast.makeText(FriendDetailActivity.this, "Successfully Updated Friend", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void handleFault(BackendlessFault fault) {
                    }
                });
            }
        });
    }

    private void wireWidgets() {
        editTextName = findViewById(R.id.editText_detail_name);
        seekBarClumsiness = findViewById(R.id.seekBar_detail_clumsiness);
        switchAwesome = findViewById(R.id.switch_detail_awesome);
        seekBarGymFrequency = findViewById(R.id.seekBar_detail_gym_frequency);
        ratingBarTrustworthiness = findViewById(R.id.ratingBar_detail_trustworthiness);
        editTextMoneyOwed = findViewById(R.id.editText_detail_money_owed);
        buttonUpdate = findViewById(R.id.button_detail_update);
    }
}
