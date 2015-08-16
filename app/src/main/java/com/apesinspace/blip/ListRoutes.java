package com.apesinspace.blip;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationServices;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.Route;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ListRoutes extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private static final String EXTRA_ROUTE_ID = "com.example.EXTRA_ROUTE_ID";
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    public static final String TAG = ListRoutes.class.getSimpleName();
    protected List<Routes> mRoutes;
    protected ListView mRouteList;
    public static final int TAKE_PHOTO_REQUEST = 0;
    public static final int MEDIA_TYPE_IMAGE = 1;
    protected Uri mMediaUri;
    protected EditText mEditText;
    protected RouteAdapter mAdapter;

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    double latitude;
    double longitude;

    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_routes);

        buildGoogleApiClient();

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if(!BlipApplication.getLoggedIn()){
            SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            String restoredText = prefs.getString("ID", null);
            if (restoredText != null) {
                User user = new User();
                user.setName(prefs.getString("NAME", ""));//"No name defined" is the default value.
                user.setId(prefs.getString("ID", "")); //0 is the default value.
                user.setImageUrl(prefs.getString("PIC", ""));
                BlipApplication.setCurrentUser(user);
                Log.d(TAG,user.getId());
            }else {
                navigateToLogin();
            }
        }

        mRoutes = new ArrayList<>();
        mRouteList = (ListView)findViewById(R.id.listView);
        mRouteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ListRoutes.this, MainActivity.class);
                //get id of the route that was clicked on
                String routeId = mRoutes.get(position).getId();
                intent.putExtra(EXTRA_ROUTE_ID,routeId);
                startActivity(intent);
            }
        });
        mAdapter = new RouteAdapter(ListRoutes.this,mRoutes);
        mEditText = (EditText)findViewById(R.id.filter);
        mEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println("Text [" + s + "]");

                mAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mRouteList.setAdapter(mAdapter);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://node.jrdbnntt.com/routes/find_routes/-1.444031/54.950512/10000")
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
                        Log.d(TAG,jsonResponse.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getRoutes(jsonResponse);
                            }
                        });
                    }else{
                        // TODO: Show response error to user
                    }
                }catch (Exception e){
                    //TODO: Show error to user
                    Log.e("TAG", e.getMessage());
                }
            }
        });
    }

    private void getRoutes(JSONObject jsonResponse) {
        if(jsonResponse == null){
            //todo:show error
        }else {
            try{
                JSONArray jsonRoutes = jsonResponse.getJSONArray("found");
                for(int i = 0; i < jsonRoutes.length(); i++){
                    mRoutes.add(new Routes(jsonRoutes.getJSONObject(i)));
                }
                mAdapter = new RouteAdapter(ListRoutes.this,mRoutes);
                mRouteList.setAdapter(mAdapter);
            }catch (JSONException e){
                Log.e(TAG,e.getMessage());
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        //do a http request to grab Routes

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_routes, menu);
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
            takePhoto();
            return true;
        }

        if (id == R.id.action_logout){
            ListRoutes.this.getSharedPreferences(MY_PREFS_NAME, 0).edit().clear().commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void takePhoto() {
        Intent takePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        if(mMediaUri == null){
            Toast.makeText(ListRoutes.this, "Error",
                    Toast.LENGTH_LONG).show();
        }else {
            takePhoto.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
            startActivityForResult(takePhoto, TAKE_PHOTO_REQUEST);
        }
    }

    private Uri getOutputMediaFileUri(int mediaTypeImage) {
        if(isExternalStorageAvailable()) {
            return Uri.fromFile(getOutputMediaFile(mediaTypeImage));
        }
        return null;
    }

    private boolean isExternalStorageAvailable(){
        String state = Environment.getExternalStorageState();
        if(state.equals(Environment.MEDIA_MOUNTED)){
            return true;
        }
        return false;
    }

    /** Create a File for saving an image or video */
    private File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        String appName = "Test";
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES),appName);
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d(TAG, "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        String path = mediaStorageDir.getPath() + File.separator;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(path + "IMG_" + timeStamp + ".jpg");
        }else {
            return null;
        }

        Log.d(TAG,"the file path is " + Uri.fromFile(mediaFile));

        return mediaFile;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            String fileName = FileHelper.getFileName(this,mMediaUri,"image");
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new MultipartBuilder()
                    .type(MultipartBuilder.FORM)
                    .addFormDataPart("image", fileName, RequestBody.create(MediaType.parse("image/jpeg"), new File(mMediaUri.getPath())))
                    .addFormDataPart("user",BlipApplication.getCurrentUser().getId())
                    .build();
            Request request = new Request.Builder()
                    .url("http://node.jrdbnntt.com/resources/save_image")
                    .post(requestBody)
                    .build();
            Log.d(TAG,requestBody.toString());
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
                                    //handleResponse(jsonResponse);
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

        }else if(resultCode != RESULT_CANCELED){
            Toast.makeText(this,"Error", Toast.LENGTH_LONG).show();
        }
    }
}

