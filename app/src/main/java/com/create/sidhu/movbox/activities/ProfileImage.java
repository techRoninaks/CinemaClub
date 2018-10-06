package com.create.sidhu.movbox.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.create.sidhu.movbox.Interfaces.SqlDelegate;
import com.create.sidhu.movbox.R;
import com.create.sidhu.movbox.helpers.PermissionsHelper;
import com.create.sidhu.movbox.helpers.SqlHelper;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ProfileImage extends Activity implements SqlDelegate{

    ImageView imageView;
    Button btnGallery, btnCamera, btnConfirm, btnDiscard;
    LinearLayout llMainButton, llConfirmButton;
    Uri imageUri;
    String imagePath, image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_image);
        imageView = findViewById(R.id.imageView);
        btnCamera = findViewById(R.id.btn_Camera);
        btnGallery = findViewById(R.id.btn_Gallery);
        btnConfirm = findViewById(R.id.btn_Confirm);
        btnDiscard = findViewById(R.id.btn_Discard);
        Button buttonExit = findViewById(R.id.btn_exit);
        llMainButton = findViewById(R.id.containerMainButtons);
        llConfirmButton = findViewById(R.id.containerConfirmButtons);
        final Intent intent = getIntent();
        final Bundle bundle = intent.getBundleExtra("bundle");
        image = bundle.getString("image");
        Glide.with(this)
                .asBitmap()
                .load(image)
                .into(imageView);
        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(ProfileImage.this, MainActivity.class);
                bundle.putString("return_path", "ProfileFragment");
                intent1.putExtra("bundle", bundle);
                startActivity(intent1);
                finish();
            }
        });
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchGetPictureIntent();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                SqlHelper sqlHelper = new SqlHelper(ProfileImage.this, ProfileImage.this);
                                sqlHelper.setActionString("upload_profile_image");
                                sqlHelper.setExecutePath("upload.php");
                                sqlHelper.setMethod("POST");
                                sqlHelper.setUploadFilePath(imagePath);
                                ArrayList<NameValuePair> params = new ArrayList<>();
                                params.add(new BasicNameValuePair("u_id", MainActivity.currentUserModel.getUserId()));
                                sqlHelper.setParams(params);
                                sqlHelper.uploadFile(true);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileImage.this);
                builder.setTitle("Upload Image");
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });
        btnDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                Glide.with(ProfileImage.this)
                                        .asBitmap()
                                        .load(image)
                                        .into(imageView);
                                llConfirmButton.setVisibility(View.GONE);
                                llMainButton.setVisibility(View.VISIBLE);
                                imagePath = "";
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileImage.this);
                builder.setTitle("Discard Image");
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });
        performChecks();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermissionsHelper.REQUEST_IMAGE_CAPTURE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                } else {
                    Toast.makeText(ProfileImage.this, getString(R.string.permission_camera_denied), Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case PermissionsHelper.REQUEST_READ_EXTERNAL_STORAGE:{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                }
                return;
            }
            case PermissionsHelper.REQUEST_WRITE_EXTERNAL_STORAGE:{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PermissionsHelper.REQUEST_IMAGE_CAPTURE) {
            if(resultCode == RESULT_OK) {
                //Bundle extras = data.getExtras();
                Glide.with(this)
                        .asBitmap()
                        .load(getRealPathFromURI(imageUri))
                        .into(imageView);
                llMainButton.setVisibility(View.GONE);
                llConfirmButton.setVisibility(View.VISIBLE);
                imagePath = getRealPathFromURI(imageUri);
                //String encodedImage = encodeImage(getRealPathFromURI(imageUri));
            }else{
                Toast.makeText(ProfileImage.this, getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
            }
        }else if (requestCode == PermissionsHelper.REQUEST_READ_EXTERNAL_STORAGE || requestCode == PermissionsHelper.REQUEST_WRITE_EXTERNAL_STORAGE&& resultCode == RESULT_OK) {
            if(resultCode == RESULT_OK) {
                Uri fullPhotoUri = data.getData();
                Glide.with(this)
                        .asBitmap()
                        .load(fullPhotoUri)
                        .into(imageView);
                llMainButton.setVisibility(View.GONE);
                llConfirmButton.setVisibility(View.VISIBLE);
                imagePath = getRealPathFromURI(fullPhotoUri);
                String encodedImage = encodeImage(fullPhotoUri);
            }else{
                Toast.makeText(ProfileImage.this, getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /***
     * Performs necessary hardware checks
     */
    private void performChecks(){
        PackageManager packageManager = getPackageManager();
        if(packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)){
            btnCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(new PermissionsHelper(ProfileImage.this).requestPermissions(PermissionsHelper.REQUEST_IMAGE_CAPTURE))
                        dispatchTakePictureIntent();
                }
            });
            btnCamera.setVisibility(View.VISIBLE);
        }
    }


    /***
     * Pass intent to start Camera app
     */
    private void dispatchTakePictureIntent() {
        try {
            if(new PermissionsHelper(ProfileImage.this).requestPermissions(PermissionsHelper.REQUEST_WRITE_EXTERNAL_STORAGE)){
                ContentValues values = new ContentValues();
                imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, PermissionsHelper.REQUEST_IMAGE_CAPTURE);
                }
            }

        }catch (Exception e){
            Log.e("ProfileImage:dispatch", e.getMessage());
        }
    }

    /***
     * Pass intent to start Gallery app
     */
    private void dispatchGetPictureIntent() {
        try {
            if(new PermissionsHelper(ProfileImage.this).requestPermissions(PermissionsHelper.REQUEST_WRITE_EXTERNAL_STORAGE)){
                Intent getPictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getPictureIntent.setType("image/*");
                getPictureIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                getPictureIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                if (getPictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(Intent.createChooser(getPictureIntent, "Select Picture"), PermissionsHelper.REQUEST_WRITE_EXTERNAL_STORAGE);
                }
            }

        }catch (Exception e){
            Log.e("ProfileImage:dispatch", e.getMessage());
        }
    }

    private String encodeImage(Uri imageUri){
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
            byte[] imageBytes = baos.toByteArray();
            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            return encodedImage;
        } catch (Exception e) {
            Log.e("encodeImage", e.getMessage());
        }
        return "";
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    public void onResponse(SqlHelper sqlHelper) {

    }
}
