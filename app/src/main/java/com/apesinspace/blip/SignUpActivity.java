package com.apesinspace.blip;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

public class SignUpActivity extends AppCompatActivity {

    protected EditText mUsername;
    protected EditText mPassword;
    protected EditText mEmail;
    protected Button mSignUpButton;
    protected Button mCancelButton;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    public static final String TAG = SignUpActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        android.support.v7.app.ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.hide();
        }

        mUsername = (EditText) findViewById(R.id.usernameField);
        mPassword = (EditText) findViewById(R.id.passwordField);
        mEmail = (EditText) findViewById(R.id.emailField);
        mCancelButton = (Button) findViewById(R.id.cancelButton);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mSignUpButton = (Button) findViewById(R.id.signupButton);
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();
                String email = mEmail.getText().toString();

                username = username.trim();
                password = password.trim();
                email = email.trim();

                if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                    builder.setMessage("Please enter in a username, password, and Email");
                    builder.setTitle("Opps");
                    builder.setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    // create new user
                    try {
                        OkHttpClient client = new OkHttpClient();
                        JSONObject newUser = new JSONObject();
                        newUser.put("username", username);
                        newUser.put("password",password);
                        newUser.put("email",email);
                        RequestBody body = RequestBody.create(JSON,newUser.toString());
                        Log.d(TAG,newUser.toString());
                        Request request = new Request.Builder()
                                .url("http://node.jrdbnntt.com/account/signup")
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
            AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
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
            User user = new User();
            try {
                user.setId(jsonResponse.getString("id"));
                user.setName(jsonResponse.getString("name"));
                user.setImageUrl(jsonResponse.getString("profile_pic"));
            }catch (JSONException e){
            }
            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE).edit();
            editor.putString("USER",user.getName());
            editor.putString("ID", user.getId());
            editor.putString("PIC", user.getImageUrl());
            editor.apply();
            BlipApplication.setCurrentUser(user);
            BlipApplication.setIsLoggedIn(true);
            Intent intent = new Intent(SignUpActivity.this,ListRoutes.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

    }
}