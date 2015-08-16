package com.apesinspace.blip;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
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
    protected View contentView;

    protected TextView infoTitle;
    HashMap<Marker, MarkerInfo>  markerMap;

    public enum MarkerType {
        SCENIC_POINT,
        CAUTION_POINT,
        POINT_OF_INTEREST
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_route_fragment);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        markerMap = new HashMap<Marker, MarkerInfo>();

        final ImageButton checkMarkButton = (ImageButton)findViewById(R.id.checkMarkButton);
        final ImageButton cancelButton = (ImageButton)findViewById(R.id.cancelTripButton);

        checkMarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RouteFragmentActivity.this, PostAdventureFragmentActivity.class);
                startActivity(i);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public void onMapReady(GoogleMap map){
        googleMap = map;
        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);


        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            getDirectionBetweenTwoPlaces("Cupertino", "Yosemite");
        }

        UiSettings settings = map.getUiSettings();
        //Let's enable the zoom control and compass control on the app.
        settings.setZoomControlsEnabled(true);
        settings.setCompassEnabled(true);
        settings.setScrollGesturesEnabled(true);
        settings.setZoomGesturesEnabled(true);

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker marker) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {
                        View contentView = getLayoutInflater().inflate(R.layout.info_contents, null);
                        MarkerInfo info = markerMap.get(marker);
                        TextView infoTitle = (TextView)contentView.findViewById(R.id.info_title);
                        infoTitle.setText(info.getInfoTitle());
                        TextView infoText = (TextView)contentView.findViewById(R.id.info_text);
                        infoText.setText(info.getInfoText());

                        return contentView;
                    }
                });
                return false;
            }
        });


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

    public void addMarker(final LatLng point, final MarkerType markerType, final String infoTitle, final String infoText){


        if (MarkerType.SCENIC_POINT == markerType){
            //Make Marker purple
            Marker newMarker = googleMap.addMarker(new MarkerOptions()
                    .position(point)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));

            MarkerInfo markerInfo = new MarkerInfo();
            markerInfo.setInfoTitle(infoTitle);
            markerInfo.setInfoText(infoText);
            markerMap.put(newMarker, markerInfo);

        }
        else if(MarkerType.CAUTION_POINT == markerType){
            //Make marker red
            Marker newMarker = googleMap.addMarker(new MarkerOptions()
                    .position(point)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

            MarkerInfo markerInfo = new MarkerInfo();
            markerInfo.setInfoTitle(infoTitle);
            markerInfo.setInfoText(infoText);
            markerMap.put(newMarker, markerInfo);

        }
        else if(MarkerType.POINT_OF_INTEREST == markerType){
            //Make marker yellow
            Marker newMarker = googleMap.addMarker(new MarkerOptions()
                    .position(point)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));

            MarkerInfo markerInfo = new MarkerInfo();
            markerInfo.setInfoTitle(infoTitle);
            markerInfo.setInfoText(infoText);
            markerMap.put(newMarker, markerInfo);

        }
    }

    public class DirectionsJSONParser {

        /**
         * Receives a JSONObject and returns a list of lists containing latitude and longitude
         */
        public List<List<HashMap<String, String>>> parse(JSONObject jObject) {

            List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
            JSONArray jRoutes = null;
            JSONArray jLegs = null;
            JSONArray jSteps = null;

            try {

                jRoutes = jObject.getJSONArray("routes");

                /** Traversing all routes */
                for (int i = 0; i < jRoutes.length(); i++) {
                    jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                    List path = new ArrayList<HashMap<String, String>>();

                    /** Traversing all legs */
                    for (int j = 0; j < jLegs.length(); j++) {
                        jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                        /** Traversing all steps */
                        for (int k = 0; k < jSteps.length(); k++) {
                            String polyline = "";
                            polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                            List<LatLng> list = decodePoly(polyline);

                            /** Traversing all points */
                            for (int l = 0; l < list.size(); l++) {
                                HashMap<String, String> hm = new HashMap<String, String>();
                                hm.put("lat", Double.toString(((LatLng) list.get(l)).latitude));
                                hm.put("lng", Double.toString(((LatLng) list.get(l)).longitude));
                                path.add(hm);
                            }
                        }
                        routes.add(path);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
            }

            return routes;
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

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

            try {
                JSONObject json = new JSONObject(result);
                JSONArray routesArray = json.getJSONArray("routes");
                JSONObject route = routesArray.getJSONObject(0);
                JSONArray legsArray = route.getJSONArray("legs");


                for(int i = 0; i < legsArray.length(); i++){
                    JSONObject leg = legsArray.getJSONObject(i);
                    JSONObject routeStartLocation = leg.getJSONObject("start_location");
                    LatLng routeStartPoint = new LatLng(routeStartLocation.getDouble("lat"), routeStartLocation.getDouble("lng"));
                    JSONObject routeEndLocation = leg.getJSONObject("end_location");
                    LatLng routeEndPoint = new LatLng(routeEndLocation.getDouble("lat"), routeEndLocation.getDouble("lng"));

                    LatLngBounds bounds = new LatLngBounds(routeStartPoint, routeEndPoint);
                    int padding = 100; // offset from edges of the map in pixels
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    googleMap.animateCamera(cu);

                }
            }
            catch(Exception e){
                System.out.println("Exception thrown when creating json object." + e.getMessage());
            }

        }

    }
    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    if(j == 36){
                        addMarker(position, MarkerType.SCENIC_POINT,"Scenic Viewpoint", "Wow, such a nice view!");
                    }
                    if(j == 500){
                        addMarker(position, MarkerType.CAUTION_POINT, "Take Caution!", "Be careful! The roads get really slippery here.");
                    }
                    if(j == 1000){
                        addMarker(position, MarkerType.POINT_OF_INTEREST, "Point of Interest", "Nice little bike shop nearby.");
                    }

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(4);
                lineOptions.color(Color.BLUE);
            }

            // Drawing polyline in the Google Map for the i-th route
            googleMap.addPolyline(lineOptions);
        }
    }
}
