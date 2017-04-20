package com.wangyue.luckyday;


import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

public class DetailInfoActivity extends AppCompatActivity {


    TextView showDetailInfoTextView ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_info);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayUseLogoEnabled(false);

        }
/*
        showDetailInfoTextView = (TextView) findViewById(R.id.showDetailInfoTextView);
        final String sText = "测试自定义标签：<br><h1><mxgsa>测试自定义标签</mxgsa></h1>";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            showDetailInfoTextView.setText(Html.fromHtml(sText,Html.FROM_HTML_MODE_LEGACY));
        } else {
            showDetailInfoTextView.setText(Html.fromHtml(sText));
        }
       */
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            //    Log.d("dsg","666666666");
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    TaskStackBuilder.create(this)
                            .addNextIntentWithParentStack(upIntent)
                            .startActivities();
                } else {
                    upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    NavUtils.navigateUpTo(this, upIntent);
                }
                break;

            default:

                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
