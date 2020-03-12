package com.per6.databases;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FriendListActivity extends AppCompatActivity {

    public static final String EXTRA_FRIEND = "Friend";

    private ListView listView;
    private FriendAdapter friendAdapter;
    private TextView textViewName;
    private TextView textViewClumsiness;
    private TextView textViewMoneyOwed;
    private FloatingActionButton floatingActionButtonNewFriend;
    private Comparator<Friend> comparator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        wireWidgets();
        comparator = new Comparator<Friend>() {
            @Override
            public int compare(Friend friend, Friend t1) {
                return friend.getName().compareTo(t1.getName());
            }
        };
        loadDataFromBackendless();
        registerForContextMenu(listView);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if(item.getItemId() == R.id.menu_item_context_delete) {
            deleteFriend(info.position);
            return true;
        }
        else{
            return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_options_sort_by_name:
                sortAlphabetically();
                friendAdapter.notifyDataSetChanged();
                return true;
            case R.id.menu_item_options_sort_by_money_owed:
                sortByName();
                friendAdapter.notifyDataSetChanged();
                return true;
            case R.id.menu_item_options_log_out:
                logoutUser();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void sortByName() {
        Collections.sort(friendAdapter.getFriendsList());
        friendAdapter.notifyDataSetChanged();
    }

    private void sortAlphabetically() {
        Collections.sort(friendAdapter.getFriendsList(), comparator);
        friendAdapter.notifyDataSetChanged();
    }

    private void deleteFriend(int position) {
        Backendless.Persistence.of(Friend.class).remove((Friend)listView.getAdapter().getItem(position), new AsyncCallback<Long>() {
            @Override
            public void handleResponse(Long response) {
                Toast.makeText(FriendListActivity.this, "Deleted Friend", Toast.LENGTH_SHORT).show();
                friendAdapter.notifyDataSetChanged();
                loadDataFromBackendless();
            }

            @Override
            public void handleFault(BackendlessFault fault) {

            }
        });
    }

    private void logoutUser() {
        Intent logoutIntent = new Intent(FriendListActivity.this, LoginActivity.class);
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

    public void loadDataFromBackendless(){
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

                floatingActionButtonNewFriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent newFriendIntent = new Intent(FriendListActivity.this, FriendDetailActivity.class);
                        startActivity(newFriendIntent);
                    }
                });


            }
            @Override
            public void handleFault( BackendlessFault fault )
            {
                Toast.makeText(FriendListActivity.this, fault.getDetail(), Toast.LENGTH_SHORT).show();
                // an error has occurred, the error code can be retrieved with fault.getCode()
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(friendAdapter != null) {
            loadDataFromBackendless();
        }
    }

    private void wireWidgets() {
        listView = findViewById(R.id.listView_list_friends);
        textViewName = findViewById(R.id.textView_item_friend_name);
        textViewClumsiness = findViewById(R.id.textView_item_friend_clumsiness);
        textViewMoneyOwed = findViewById(R.id.textView_item_friend_money_owed);
        floatingActionButtonNewFriend = findViewById(R.id.floatingActionButton_list_add);
    }



    private class FriendAdapter extends ArrayAdapter{
        private List<Friend> friendsList;
        private int position;

        public FriendAdapter(List<Friend> friendsList) {
            super(FriendListActivity.this, -1, friendsList);
            this.friendsList = friendsList;
        }

        public List<Friend> getFriendsList() {
            return friendsList;
        }

        public void setFriendsList(List<Friend> friendsList) {
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
