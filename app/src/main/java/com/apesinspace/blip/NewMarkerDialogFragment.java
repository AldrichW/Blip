package com.apesinspace.blip;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by aldrichW on 15-08-15.
 */
public class NewMarkerDialogFragment extends DialogFragment {

    Marker marker;
    GoogleMap googleMap;
    Spinner markerTypeSpinner;
    Button uploadPhotoButton;
    EditText description;
    String imagePathString;
    View view;

    HashMap<Marker, MarkerInfo> markerMap;
    public static final int RESULT_LOAD_IMAGE = 3;
    public static final int RESULT_OK = -1;
    public static final int GET_FROM_GALLERY = 3;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();

        markerMap = new HashMap<Marker, MarkerInfo>();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        view = inflater.inflate(R.layout.new_marker_view, null);
        markerTypeSpinner = (Spinner)view.findViewById(R.id.marker_type_spinner);
        uploadPhotoButton = (Button)view.findViewById(R.id.upload_button);
        description = (EditText)view.findViewById(R.id.marker_description);

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
                        View contentView = inflater.inflate(R.layout.info_contents, null);
                        MarkerInfo info = markerMap.get(marker);
                        TextView infoTitle = (TextView)contentView.findViewById(R.id.info_title);
                        infoTitle.setText(info.getInfoTitle());
                        ImageView image = (ImageView)contentView.findViewById(R.id.info_image);
                        image.setImageBitmap(BitmapFactory.decodeFile(info.getImage()));
                        TextView infoText = (TextView)contentView.findViewById(R.id.info_text);
                        infoText.setText(info.getInfoText());

                        return contentView;
                    }
                });
                return false;
            }
        });

        uploadPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }

        });


        builder.setView(view);
        builder.setTitle("New Marker");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Marker newMarker = marker;
                MarkerInfo info = new MarkerInfo();
                if(markerTypeSpinner.getSelectedItemPosition() == 0){
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                    info.setInfoTitle("Scenic Viewpoint");
                }
                else if(markerTypeSpinner.getSelectedItemPosition() == 1){
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    info.setInfoTitle("Caution Point");
                }
                else{
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                    info.setInfoTitle("Points of Interest");
                }

                info.setInfoText(description.getText().toString());
                info.setImagePath(imagePathString);

                markerMap.put(newMarker, info);
            }
        });
        // Create the AlertDialog object and return it
        return builder.create();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = view.getContext().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            ImageView imageView = (ImageView) view.findViewById(R.id.imgView);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            imagePathString = picturePath;

            uploadPhotoButton.setVisibility(View.GONE);
        }


    }

    public void setMarker(Marker _marker){
        marker = _marker;
    }

    public void setGoogleMap(GoogleMap _googleMap){
        googleMap = _googleMap;
    }
}
