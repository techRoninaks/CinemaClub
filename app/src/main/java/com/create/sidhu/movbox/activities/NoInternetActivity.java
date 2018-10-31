package com.create.sidhu.movbox.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.create.sidhu.movbox.R;
import com.create.sidhu.movbox.helpers.SqlHelper;

public class NoInternetActivity extends AppCompatActivity {
    public static SqlHelper sqlHelper;
    Button btnretry;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);
        btnretry = (Button) findViewById(R.id.btn_Retry);
        btnretry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqlHelper.executeUrl(true);
                finish();
            }
        });
    }
}
