package com.create.sidhu.movbox;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.create.sidhu.movbox.activities.MainActivity;

public class ProfileImage extends Activity {

    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_image);
        imageView = findViewById(R.id.imageView);
        Button buttonExit = findViewById(R.id.btn_exit);
        final Intent intent = getIntent();
        final Bundle bundle = intent.getBundleExtra("bundle");
        String image = bundle.getString("image");
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
            }
        });
    }
}
