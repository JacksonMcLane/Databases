package com.per6.databases;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.UserService;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;

import java.util.List;

public class FriendListActivity extends AppCompatActivity {

    public static final String EXTRA_FRIEND = "Friend";

    private ListView listView;
    private FriendAdapter friendAdapter;
    private TextView textViewName;
    private TextView textViewClumsiness;
    private TextView textViewMoneyOwed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        wireWidgets();
        Backendless.initApp(this, com.per6.databases.Credentials.APP_ID, com.per6.databases.Credentials.API_KEY);
        //search only for Friends with ownerIds that match the user's objectId
        String userId = Backendless.UserService.CurrentUser().getObjectId();
        String whereClause = "ownerId = " + "'" + userId + "'";
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);

        Backendless.Data.of(Friend.class).find(queryBuilder, new AsyncCallback<List<Friend>>(){
            @Override
            public void handleResponse(final List<Friend> foundFriends)
            {
                friendAdapter = new FriendAdapter(foundFriends);
                listView.setAdapter(friendAdapter);

                // we're sure that the list of friends exists at this point in the code
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent detailIntent = new Intent(FriendListActivity.this, FriendDetailActivity.class);
                        detailIntent.putExtra(EXTRA_FRIEND, foundFriends.get(i));
                        startActivity(detailIntent);
                    }
                });
                
                //TODO make Friend parcelable + when a friend is clicked, opens detail activity and loads the info
            }
            @Override
            public void handleFault( BackendlessFault fault )
            {
                Toast.makeText(FriendListActivity.this, fault.getDetail(), Toast.LENGTH_SHORT).show();
                // an error has occurred, the error code can be retrieved with fault.getCode()
            }
        });

    }

    private void wireWidgets() {
        listView = findViewById(R.id.listView_list_friends);
        textViewName = findViewById(R.id.textView_item_friend_name);
        textViewClumsiness = findViewById(R.id.textView_item_friend_clumsiness);
        textViewMoneyOwed = findViewById(R.id.textView_item_friend_money_owed);

    }



    private class FriendAdapter extends ArrayAdapter{
        private List<Friend> friendsList;
        private int position;

        public FriendAdapter(List<Friend> friendsList) {
            super(FriendListActivity.this, -1, friendsList);
            this.friendsList = friendsList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            this.position = position;
            LayoutInflater inflater = getLayoutInflater();
            if(convertView == null){
                convertView = inflater.inflate(R.layout.item_friend, parent, false);
            }

            textViewName = convertView.findViewById(R.id.textView_item_friend_name);
            textViewClumsiness = convertView.findViewById(R.id.textView_item_friend_clumsiness);
            textViewMoneyOwed = convertView.findViewById(R.id.textView_item_friend_money_owed);

            textViewName.setText(friendsList.get(position).getName());
            textViewClumsiness.setText(String.valueOf(friendsList.get(position).getClumsiness()));
            textViewMoneyOwed.setText(String.valueOf(friendsList.get(position).getMoneyOwed()));

            return convertView;
        }
    }

}
