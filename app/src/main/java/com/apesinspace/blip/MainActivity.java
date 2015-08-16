package com.apesinspace.blip;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.internal.view.SupportMenuItem;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;


import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    private static final String EXTRA_ROUTE_ID = "com.example.EXTRA_ROUTE_ID";


    protected ListView mUserListView;
    protected List<User> mUsers;
    protected ImageView mRouteImageView;
    protected ImageView mAuthorImageView;
    protected TextView mAuthorName;
    protected TextView mDiscription;
    protected RatingBar mAvrageRating;
    protected String mRouteId;

    private ShareActionProvider mShareActionProvider;
    protected Intent mShareIntent;
    protected Routes mRoutes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRouteId = getIntent().getStringExtra(EXTRA_ROUTE_ID);
        mUserListView = (ListView) findViewById(R.id.listView);
        mRouteImageView = (ImageView) findViewById(R.id.imageView);
        mAuthorImageView = (ImageView)findViewById(R.id.authorIcon);
        mAuthorName = (TextView)findViewById(R.id.authorName);
        mDiscription = (TextView)findViewById(R.id.discription);
        mAvrageRating = (RatingBar)findViewById(R.id.averageRating);

        mUsers = new ArrayList<>();
        mShareIntent = new Intent();
        mShareIntent.setAction(Intent.ACTION_SEND);
        mShareIntent.setType("text/plain");
        mShareIntent.putExtra(Intent.EXTRA_TEXT, "From me to you, this text is new.");

        getRouterDetails();
        getUsersReviews();
        mRouteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RouteFragmentActivity.class);
                startActivity(intent);
            }
        });

    }

    private void getRouterDetails() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://node.jrdbnntt.com/routes/getRouteById/"+mRouteId)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e(TAG, e.getMessage());
                //Todo:Create alert dialog that notifies user what happend
            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    //Todo: check to see if authenticated if so start next activity else show error
                    if (response.isSuccessful()) {
                        //process response
                        final JSONObject jsonResponse = new JSONObject(response.body().string());
                        Log.d(TAG, jsonResponse.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                handleResponse(jsonResponse);
                            }
                        });
                    } else {
                        // TODO: Show response error to user
                        Log.e("TAG", "the response was unsuccessful");
                    }
                } catch (Exception e) {
                    //TODO: Show error to user
                    Log.e("TAG", e.getMessage());
                }
            }
        });
    }

    private void handleResponse(JSONObject jsonResponse) {
        mRoutes = new Routes();

        try{
            JSONObject object = jsonResponse.getJSONObject("routeData");
            mRoutes.setId(object.getString("id"));
            mRoutes.setName(object.getString("name"));
            mRoutes.setAvgRating(object.getInt("star_rating"));
            mRoutes.setDiscription(object.getString("description"));
            JSONObject creator = object.getJSONObject("original_driver");
            mRoutes.setAuthor(creator.getString("username"));
            mRoutes.setAuthorImage(creator.getString("profile_pic"));


        }catch (JSONException e){

        }
        mAvrageRating.setNumStars(mRoutes.getAvgRating());
        mAuthorName.setText(mRoutes.getAuthor());
        mDiscription.setText(mRoutes.getDiscription());
        Picasso.with(MainActivity.this).load(mRoutes.getAuthorImage()).into(mAuthorImageView);

    }

    private void getUsersReviews() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://node.jrdbnntt.com/routes/getRouteReviewsById/"+mRouteId)
                .build();
        Call call = client.newCall(request);
        Log.d(TAG,"http://node.jrdbnntt.com/routes/getRouteReviewsById/"+mRouteId);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e(TAG, e.getMessage());
                //Todo:Create alert dialog that notifies user what happend
            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    //Todo: check to see if authenticated if so start next activity else show error
                    if (response.isSuccessful()) {
                        //process response
                        final JSONObject jsonResponse = new JSONObject(response.body().string());
                        Log.d(TAG, jsonResponse.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                handleUserResponse(jsonResponse);
                            }
                        });
                    } else {
                        // TODO: Show response error to user
                        Log.e("TAG", "the response was unsuccessful");
                    }
                } catch (Exception e) {
                    //TODO: Show error to user
                    Log.e("TAG", e.getMessage());
                }
            }
        });
    }

    private void handleUserResponse(JSONObject jsonResponse) {
        try{
        JSONArray array = jsonResponse.getJSONArray("reviewData");
        for(int i = 0; i < array.length(); i++) {
            JSONObject review = array.getJSONObject(i);
            User user = new User();
            user.setReview(review.getString("review"));
            user.setRating(review.getInt("star_rating"));
            JSONObject obj = review.getJSONObject("user");
            user.setName(obj.getString("username"));
            user.setImageUrl(obj.getString("profile_pic"));
            mUsers.add(user);
        }
            UserAdapter adapter = new UserAdapter(MainActivity.this, mUsers);
            mUserListView.setAdapter(adapter);

        }catch (JSONException e){

        }

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
