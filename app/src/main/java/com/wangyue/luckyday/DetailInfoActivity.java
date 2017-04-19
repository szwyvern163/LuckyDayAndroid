package com.wangyue.luckyday;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

public class DetailInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_info);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // getSupportActionBar().setTitle(R.string.app_name);
            //actionBar.setLogo();
            //actionBar.setDisplayUseLogoEnabled(true);

            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayUseLogoEnabled(false);

        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            //    Log.d("dsg","666666666");
                break;

            default:

                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
