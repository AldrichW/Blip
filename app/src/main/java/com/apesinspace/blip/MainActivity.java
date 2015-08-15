package com.apesinspace.blip;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.internal.view.SupportMenuItem;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    protected ListView mUserListView;
    protected List<User> mUsers;
    protected ImageView mRouteImageView;
    private ShareActionProvider mShareActionProvider;
    protected Intent mShareIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUserListView = (ListView) findViewById(R.id.listView);
        mRouteImageView = (ImageView) findViewById(R.id.imageView);
        mUsers = new ArrayList<>();
        mShareIntent = new Intent();
        mShareIntent.setAction(Intent.ACTION_SEND);
        mShareIntent.setType("text/plain");
        mShareIntent.putExtra(Intent.EXTRA_TEXT, "From me to you, this text is new.");

        mUsers.add(new User("Sam","This is a review blalb"));
        mUsers.add(new User("Sam", "This is a review blalb"));
        mUsers.add(new User("Sam", "This is a review blalb"));
        mUsers.add(new User("Sam", "This is a review blalb"));
        mUsers.add(new User("Sam", "This is a review blalb"));
        mUsers.add(new User("Sam", "This is a review blalb"));

        mRouteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RouteFragmentActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //if(mUserListView.getAdapter() == null) {
            UserAdapter adapter = new UserAdapter(MainActivity.this, mUsers);
            mUserListView.setAdapter(adapter);
        //}else{
            //refill
          //  ((UserAdapter)mUserListView.getAdapter()).refill(mUsers);
        //}

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.menu_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(mShareIntent);
        }


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
