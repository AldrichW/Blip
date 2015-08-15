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

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RouteFragmentActivity extends FragmentActivity implements OnMapReadyCallback{

    GoogleMap googleMap;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_route_fragment);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            getDirectionBetweenTwoPlaces("Toronto", "Montreal");
        }

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
                "&destination=" + endingPlace + "&key=\n" +
                "AIzaSyC8w2HRhc3uLwG7b2gIQroRHUYtkyljgSI";

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
                List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>() ;
                JSONObject json = new JSONObject(result);
                String status = json.getString("status");
                JSONArray routesArray = json.getJSONArray("routes");
                JSONObject route = routesArray.getJSONObject(0);
                JSONArray legsArray = route.getJSONArray("legs");

                List path = new ArrayList<HashMap<String, String>>();

                for(int i = 0; i < legsArray.length(); i++){
                    JSONObject leg = legsArray.getJSONObject(i);
                    JSONArray stepsArray = leg.getJSONArray("steps");
                    JSONObject routeStartLocation = leg.getJSONObject("start_location");
                    LatLng routeStartPoint = new LatLng(routeStartLocation.getDouble("lat"), routeStartLocation.getDouble("lng"));
                    JSONObject routeEndLocation = leg.getJSONObject("end_location");
                    LatLng routeEndPoint = new LatLng(routeEndLocation.getDouble("lat"), routeEndLocation.getDouble("lng"));

                    LatLngBounds bounds = new LatLngBounds(routeStartPoint, routeEndPoint);
                    int padding = 100; // offset from edges of the map in pixels
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    googleMap.animateCamera(cu);
                    ArrayList<LatLng> points = new ArrayList<LatLng>();
                    for (int j = 0; j < stepsArray.length(); j++){
                        String polyline = "";
                        polyline = (String)((JSONObject)((JSONObject)stepsArray.get(j)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);

                        /** Traversing all points */
                        for(int l=0;l<list.size();l++){
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                            hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                            path.add(hm);
                        }
                    }
                    Polyline line = googleMap.addPolyline(new PolylineOptions()
                            .addAll(points)
                            .width(5)
                            .color(Color.BLUE));

                    routes.add(path);

                    ArrayList<LatLng> pointsTwo = null;
                    PolylineOptions lineOptions = null;
                    MarkerOptions markerOptions = new MarkerOptions();

                    // Traversing through all the routes
                    for(int k=0;k<routes.size();k++){
                        points = new ArrayList<LatLng>();
                        lineOptions = new PolylineOptions();

                        // Fetching i-th route
                        List<HashMap<String, String>> paths = routes.get(k);

                        // Fetching all the points in i-th route
                        for(int l=0;l<path.size();l++){
                            HashMap<String,String> point = paths.get(l);

                            double lat = Double.parseDouble(point.get("lat"));
                            double lng = Double.parseDouble(point.get("lng"));
                            LatLng position = new LatLng(lat, lng);

                            points.add(position);
                        }

                        // Adding all the points in the route to LineOptions
                        lineOptions.addAll(points);
                        lineOptions.width(5);
                        lineOptions.color(Color.BLUE);
                    }

                    // Drawing polyline in the Google Map for the i-th route
                    googleMap.addPolyline(lineOptions);
                }
            }
            catch(Exception e){
                System.out.println("Exception thrown when creating json object." + e.getMessage());
            }

        }

        private List<LatLng> decodePoly(String encoded) {

            List<LatLng> poly = new ArrayList<LatLng>();
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng((((double) lat / 1E5)),
                        (((double) lng / 1E5)));
                poly.add(p);
            }

            return poly;
        }

    }
}
