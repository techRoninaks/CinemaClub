package com.create.sidhu.movbox.activities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.create.sidhu.movbox.GlideApp;
import com.create.sidhu.movbox.Interfaces.SqlDelegate;
import com.create.sidhu.movbox.R;
import com.create.sidhu.movbox.helpers.EmailHelper;
import com.create.sidhu.movbox.helpers.ModelHelper;
import com.create.sidhu.movbox.helpers.PermissionsHelper;
import com.create.sidhu.movbox.helpers.SqlHelper;
import com.create.sidhu.movbox.helpers.StringHelper;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ProfileImage extends Activity implements SqlDelegate{

    ImageView imageView;
    Button btnGallery, btnCamera, btnConfirm, btnDiscard;
    LinearLayout llMainButton, llConfirmButton;
    Uri imageUri;
    String imagePath, image, uploadUrl;
    Bitmap bitmap;

    RequestOptions requestOptions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_profile_image);
            requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.ic_placeholder);
            requestOptions.error(R.drawable.ic_placeholder);
            uploadUrl = ProfileImage.this.getString(R.string.master_url) + "upload.php";
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
            GlideApp.with(this)
                    .asBitmap()
                    .load(image)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(imageView);
            buttonExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE: {
//                                SqlHelper sqlHelper = new SqlHelper(ProfileImage.this, ProfileImage.this);
//                                sqlHelper.setActionString("upload_profile_image");
//                                sqlHelper.setExecutePath("upload.php");
//                                sqlHelper.setMethod("POST");
//                                sqlHelper.setUploadFilePath(imagePath);
//                                ArrayList<NameValuePair> params = new ArrayList<>();
//                                params.add(new BasicNameValuePair("u_id", MainActivity.currentUserModel.getUserId()));
//                                sqlHelper.setParams(params);
//                                sqlHelper.uploadFile(true);
                                    StringRequest stringRequest = new StringRequest(Request.Method.POST, uploadUrl, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Toast.makeText(ProfileImage.this, "Your profile image has been saved", Toast.LENGTH_SHORT).show();
                                            Bundle bundle = new ModelHelper(ProfileImage.this).buildUserModelBundle(MainActivity.currentUserModel, "ProfileFragment");
                                            bundle.putString("return_path", "ProfileFragment");
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            intent.putExtra("bundle", bundle);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(ProfileImage.this, ProfileImage.this.getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
                                        }
                                    }) {
                                        @Override
                                        protected Map<String, String> getParams() throws AuthFailureError {
                                            String imageData = imageToString(bitmap);
                                            Map<String, String> params = new HashMap<>();
                                            params.put("image", imageData);
                                            params.put("u_id", MainActivity.currentUserModel.getUserId());
                                            return params;
                                        }
                                    };

                                    RequestQueue requestQueue = Volley.newRequestQueue(ProfileImage.this);
                                    requestQueue.add(stringRequest);
                                    break;
                                }
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
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    Glide.with(ProfileImage.this)
                                            .setDefaultRequestOptions(requestOptions)
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
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(ProfileImage.this, EmailHelper.TECH_SUPPORT, "Error: ProfileImage", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
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
        try {
            if (requestCode == PermissionsHelper.REQUEST_IMAGE_CAPTURE) {
                if (resultCode == RESULT_OK) {
                    Glide.with(this)
                            .setDefaultRequestOptions(requestOptions)
                            .asBitmap()
                            .load(getRealPathFromURI(imageUri))
                            .into(imageView);
                    llMainButton.setVisibility(View.GONE);
                    llConfirmButton.setVisibility(View.VISIBLE);
                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                } else {
                    Toast.makeText(ProfileImage.this, getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == PermissionsHelper.REQUEST_READ_EXTERNAL_STORAGE || requestCode == PermissionsHelper.REQUEST_WRITE_EXTERNAL_STORAGE && resultCode == RESULT_OK) {
                if (resultCode == RESULT_OK) {
                    Uri fullPhotoUri = data.getData();
                    Glide.with(this)
                            .setDefaultRequestOptions(requestOptions)
                            .asBitmap()
                            .load(fullPhotoUri)
                            .into(imageView);
                    llMainButton.setVisibility(View.GONE);
                    llConfirmButton.setVisibility(View.VISIBLE);
                    InputStream inputStream = getContentResolver().openInputStream(fullPhotoUri);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                } else {
                    Toast.makeText(ProfileImage.this, getString(R.string.unexpected), Toast.LENGTH_SHORT).show();
                }
            }
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(ProfileImage.this, EmailHelper.TECH_SUPPORT, "Error: ProfileImage", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }

    private String imageToString(Bitmap bitmap){
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
            byte[] imageBytes = outputStream.toByteArray();

            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            return encodedImage;
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(ProfileImage.this, EmailHelper.TECH_SUPPORT, "Error: ProfileImage", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
        return "";
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
            EmailHelper emailHelper = new EmailHelper(ProfileImage.this, EmailHelper.TECH_SUPPORT, "Error: ProfileImage", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
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
            EmailHelper emailHelper = new EmailHelper(ProfileImage.this, EmailHelper.TECH_SUPPORT, "Error: ProfileImage", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
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
        try{

        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(ProfileImage.this, EmailHelper.TECH_SUPPORT, "Error: ProfileImage", StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }
}
