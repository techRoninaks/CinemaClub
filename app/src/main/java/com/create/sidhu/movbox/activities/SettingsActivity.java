package com.create.sidhu.movbox.activities;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.create.sidhu.movbox.R;

public class SettingsActivity extends AppCompatActivity {

    Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        bundle = getIntent().getBundleExtra("bundle");
        getSupportActionBar().setHomeButtonEnabled(true);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                intent.putExtra("bundle",bundle);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
