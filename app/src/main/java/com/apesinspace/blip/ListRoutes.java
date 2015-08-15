package com.apesinspace.blip;

import android.content.Intent;
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
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListRoutes extends AppCompatActivity {


    private static final String EXTRA_ROUTE_ID = "com.example.EXTRA_ROUTE_ID";
    protected List<Routes> mRoutes;
    protected ListView mRouteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_routes);
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

        RequestBody formBody = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart("username", "tet")
                .addFormDataPart("pass", "test")
                .addFormDataPart("Test", "test")//Will leave for now, but should either make a constant or some kind of user input
                .build();
        Request request = new Request.Builder()
                .url("http://node.jrdbnntt.com/api/save_route")
                .post(formBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e("TEST", e.getMessage());
                //Todo:Create alert dialog that notifies user what happend
            }
            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    //Todo: check to see if authenticated if so start next activity else show error
                    if (response.isSuccessful()) {
                        //process response
                        final JSONObject jsonResponse = new JSONObject(response.body().string());
                        //runOnUiThread(new Runnable() {
                           // @Override
                          //  public void run() {
                            //    Authenticate(jsonResponse);
                        //    }
                       // });
                    }else{
                        // TODO: Show response error to user
                    }
                }catch (Exception e){
                    //TODO: Show error to user
                    Log.e("TST", e.getMessage());
                }
            }
        });
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
}
