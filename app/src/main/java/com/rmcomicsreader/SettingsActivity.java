package com.rmcomicsreader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by Ромочка on 11.05.2015.
 */
public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
    }

    public void onClickToView(View view) {
        Intent intent = new Intent(SettingsActivity.this,MainActivity.class);
        startActivity(intent);
    }
}
