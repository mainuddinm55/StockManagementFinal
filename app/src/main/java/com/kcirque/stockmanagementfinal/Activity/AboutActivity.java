package com.kcirque.stockmanagementfinal.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.MenuItem;
import android.widget.TextView;

import com.bluejamesbond.text.DocumentView;
import com.kcirque.stockmanagementfinal.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("About this app");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        DocumentView documentView = findViewById(R.id.instruction);
        documentView.setText(getResources().getString(R.string.instruction_text));


    }


    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
