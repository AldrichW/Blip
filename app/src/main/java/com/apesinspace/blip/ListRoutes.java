package com.apesinspace.blip;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListRoutes extends AppCompatActivity {


    private static final String EXTRA_ROUTE_ID = "com.example.EXTRA_ROUTE_ID";
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    public static final String TAG = ListRoutes.class.getSimpleName();
    protected List<Routes> mRoutes;
    protected ListView mRouteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_routes);
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
        mRoutes.add(new Routes("this"));
        mRoutes.add(new Routes("is"));
        mRoutes.add(new Routes("sparta"));
        mRouteList = (ListView)findViewById(R.id.listView);
        mRouteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ListRoutes.this,MainActivity.class);
                //get id of the route that was clicked on
                //String id = mRoutes.get(position).getId();
                //intent.putExtra(EXTRA_ROUTE_ID,id);
                startActivity(intent);
            }
        });
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON,"{\n" +
                "   \"data\": \"Test\",\n" +
                "   \"bool\": true\n" +
                "}");
        Request request = new Request.Builder()
                .url("http://node.jrdbnntt.com/resources/test")
                .post(body)
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
                                //getRoutes(jsonResponse);
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
                JSONArray jsonRoutes = jsonResponse.getJSONArray("routes");
                for(int i = 0; i < jsonRoutes.length(); i++){
                    mRoutes.add(new Routes(jsonRoutes.getJSONObject(i).getString("route")));
                }
            }catch (JSONException e){
                Log.e(TAG,e.getMessage());
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        //do a http request to grab Routes
        //getRoutes();
        RouteAdapter adapter = new RouteAdapter(ListRoutes.this,mRoutes);
        mRouteList.setAdapter(adapter);
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

}
