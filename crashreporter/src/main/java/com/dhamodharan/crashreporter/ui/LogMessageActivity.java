package com.dhamodharan.crashreporter.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.dhamodharan.crashreporter.R;
import com.dhamodharan.crashreporter.utils.AppUtils;
import com.dhamodharan.crashreporter.utils.FileUtils;

import java.io.File;

public class LogMessageActivity extends AppCompatActivity {

    private TextView appInfo;
    private String file_path_for_delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_message);
        appInfo = (TextView) findViewById(R.id.appInfo);

        Intent intent = getIntent();
        if (intent != null) {
            String dirPath = intent.getStringExtra("LogMessage");
            File file = new File(dirPath);
            String crashLog = FileUtils.readFromFile(file);
            TextView textView = (TextView) findViewById(R.id.logMessage);
            textView.setText(crashLog);
        }

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitle(getString(R.string.crash_reporter));
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getAppInfo();
    }

    private void getAppInfo() {
        appInfo.setText(AppUtils.getDeviceDetails(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.crash_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = getIntent();
        String filePath = null;
        if (intent != null) {
            filePath = intent.getStringExtra("LogMessage");
            file_path_for_delete = filePath;
        }

        if (item.getItemId() == R.id.delete_log) {
            new AlertDialog.Builder(this)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (FileUtils.delete(file_path_for_delete)) {
                                finish();
                            }
                        }
                    })
                    .setMessage(R.string.delete_confirmation_msg_logpage)
                    .setNegativeButton(android.R.string.no, null)
                    .show();

            return true;
        } else if (item.getItemId() == R.id.share_crash_log) {
            //shareCrashReport(filePath);
            shareCrashReport(filePath);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }

    }

//    private void shareCrashReport(String filePath) {
//        Intent intent = new Intent(Intent.ACTION_SEND);
//        intent.setType("*/*");
//        intent.putExtra(Intent.EXTRA_TEXT, appInfo.getText().toString());
//        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filePath)));
//        startActivity(Intent.createChooser(intent, "Share via"));
//    }


    private void shareCrashReport(String filePath) {
        Uri path = FileProvider.getUriForFile(this, "com.dhamodharan.crashreporter", new File(filePath));

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, appInfo.getText().toString());
        shareIntent.putExtra(Intent.EXTRA_STREAM, path);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.setType("*/*");
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

}
