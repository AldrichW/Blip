package com.apesinspace.blip;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by samnwosu on 8/16/15.
 */
public class Utils extends Activity {
    public static final String TAG = Utils.class.getSimpleName();
    public static final int TAKE_PHOTO_REQUEST = 0;
    public static final int MEDIA_TYPE_IMAGE = 1;
    protected Uri mMediaUri;
    protected Activity mActivity;


    public void takePhoto(Context context, Activity act) {
        Intent takePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        if(mMediaUri == null){
            Toast.makeText(context, "Error",
                    Toast.LENGTH_LONG).show();
        }else {
            takePhoto.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
            mActivity = act;
            act.startActivityForResult(takePhoto, TAKE_PHOTO_REQUEST);
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
            RequestBody requestBody = new MultipartBuilder().type(MultipartBuilder.FORM).addFormDataPart("image", fileName, RequestBody.create(MediaType.parse("image/jpeg"), new File(mMediaUri.getPath()))).build();
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
