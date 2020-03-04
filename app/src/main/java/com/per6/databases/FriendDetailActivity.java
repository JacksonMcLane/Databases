package com.per6.databases;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Switch;

import com.backendless.Backendless;
import com.backendless.Persistence;
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
            switchAwesome.setChecked(foundFriend.getIsAwesome());
            seekBarGymFrequency.setProgress((int)(foundFriend.getGymFrequency() * 2));
            ratingBarTrustworthiness.setProgress(foundFriend.getTrustworthiness());
            editTextMoneyOwed.setText(String.valueOf(foundFriend.getMoneyOwed()));

        }
        setButtonListener();

    }

    private void newFriend() {
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Backendless.Persistence.save(foundFriend, new AsyncCallback<Friend>() {
                    @Override
                    public void handleResponse(Friend response) {
                        foundFriend = new Friend();
                        foundFriend.setOwnerId(Backendless.UserService.CurrentUser().getUserId());
                        foundFriend.setName(String.valueOf(editTextName.getText()));
                        foundFriend.setClumsiness(seekBarClumsiness.getProgress());
                        foundFriend.setIsAwesome(switchAwesome.isChecked());
                        foundFriend.setGymFrequency((seekBarGymFrequency.getProgress()/2.0));
                        foundFriend.setTrustworthiness(ratingBarTrustworthiness.getProgress());
                        foundFriend.setMoneyOwed(Double.valueOf(String.valueOf(editTextMoneyOwed.getText())));
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
                Backendless.Persistence.save(foundFriend, new AsyncCallback<Friend>() {
                    @Override
                    public void handleResponse(Friend response) {
                        foundFriend.setName(String.valueOf(editTextName.getText()));
                        foundFriend.setClumsiness(seekBarClumsiness.getProgress());
                        foundFriend.setIsAwesome(switchAwesome.isChecked());
                        foundFriend.setGymFrequency((seekBarGymFrequency.getProgress()/2.0));
                        foundFriend.setTrustworthiness(ratingBarTrustworthiness.getProgress());
                        foundFriend.setMoneyOwed(Double.valueOf(String.valueOf(editTextMoneyOwed.getText())));

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
