package com.apesinspace.blip;


import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class RouteFragmentActivity extends FragmentActivity implements OnMapReadyCallback{

    GoogleMap googleMap;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            getDirectionBetweenTwoPlaces("Toronto", "Montreal");
        }

        setContentView(R.layout.activity_route_fragment);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap map){
        googleMap = map;
        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        UiSettings settings = map.getUiSettings();
        //Let's enable the zoom control and compass control on the app.
        settings.setZoomControlsEnabled(true);
        settings.setCompassEnabled(true);
        settings.setScrollGesturesEnabled(true);
        settings.setZoomGesturesEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_route, menu);
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


    public void getDirectionBetweenTwoPlaces(String startingPlace, String endingPlace) {
        String urlString = "https://maps.googleapis.com/maps/api/directions/json?origin=" + startingPlace +
                "&destination=" + endingPlace + "&key=AIzaSyDjE7vD3WVTTZ1aabGmZBLj9XB5hlqHbZw";

        DownloadHandler downloadHandler = new DownloadHandler();
        downloadHandler.execute(urlString);
    }

    public class DownloadHandler extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urlString){
            try {
                //Assume just one link here.
                URL url = new URL(urlString[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                try {
                    int ch;
                    StringBuffer sb = new StringBuffer();
                    while ((ch = in.read()) != -1) {
                        sb.append((char) ch);
                    }
                    System.out.println(sb.toString());
                    return sb.toString();
                } catch (IOException e) {
                    throw e;
                } finally {
                    if (in != null) {
                        in.close();
                    }
                }
            }
            catch (Exception exception){
                System.out.println("Exception Caught." + exception.getMessage());

            }

            return null;
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);

            try {
                JSONObject json = new JSONObject(result);
                String status = json.getString("status");
                JSONArray jsonArray = json.getJSONArray("routes");
                JSONArray legsArray = jsonArray.getJSONArray(0);
                ArrayList<LatLng> points = new ArrayList<LatLng>();
                for(int i = 0; i < legsArray.length(); i++){
                    JSONObject latLngJson = legsArray.getJSONObject(i);
                    LatLng newPoint = new LatLng(latLngJson.getDouble("lat"), latLngJson.getDouble("long"));
                    points.add(newPoint);
                }
                System.out.println(status);

                PolylineOptions polyLineOptions = new PolylineOptions();
                polyLineOptions.addAll(points);
                polyLineOptions.width(2);
                polyLineOptions.color(Color.BLUE);

                googleMap.addPolyline(polyLineOptions);

            }
            catch(Exception e){
                System.out.println("Exception thrown when creating json object." + e.getMessage());
            }



        }

    }
}
