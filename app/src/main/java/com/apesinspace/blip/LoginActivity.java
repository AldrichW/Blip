package com.apesinspace.blip;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {
    public static String TAG = LoginActivity.class.getSimpleName();
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    protected TextView mSignUpTextView;
    protected EditText mEmail;
    protected EditText mPassword;
    protected Button mLoginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); Deprecated
        setContentView(R.layout.activity_login);

        android.support.v7.app.ActionBar actionbar = getSupportActionBar();
        if(actionbar != null) {
            actionbar.hide();
        }


        mSignUpTextView = (TextView)findViewById(R.id.signupText);
        mSignUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
        mEmail = (EditText)findViewById(R.id.emailField);
        mPassword = (EditText)findViewById(R.id.passwordField);
        mLoginButton = (Button)findViewById(R.id.loginButton);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                email = email.trim();
                password = password.trim();

                if (email.isEmpty() || password.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage("Please enter Both a username and password");
                    builder.setTitle("Oops");
                    builder.setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    // create new user
                    try {
                        OkHttpClient client = new OkHttpClient();
                        JSONObject newUser = new JSONObject();
                        newUser.put("email", email);
                        newUser.put("password",password);
                        RequestBody body = RequestBody.create(JSON,newUser.toString());
                        Log.d(TAG, newUser.toString());
                        Request request = new Request.Builder()
                                .url("http://node.jrdbnntt.com/account/signin")
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
                    }catch (JSONException e){
                        Log.e(TAG,e.getMessage());
                    }

                }
            }
        });
    }

    private void handleResponse(JSONObject jsonResponse){
        if(jsonResponse == null || !jsonResponse.isNull("error")){
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            String error = "Unknwon error";
            try {
                if (!jsonResponse.isNull("error"))
                    error = jsonResponse.getString("error");
            }catch (JSONException e){
            }
            builder.setMessage(error);
            builder.setTitle("Oops!");
            builder.setPositiveButton(android.R.string.ok,null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }else{
            BlipApplication.setIsLoggedIn(true);
            Intent intent = new Intent(LoginActivity.this,ListRoutes.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
