package com.dhamodharan.crashreporter.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.dhamodharan.crashreporter.CrashReporter;
import com.dhamodharan.crashreporter.R;
import com.dhamodharan.crashreporter.adapter.MainPagerAdapter;
import com.dhamodharan.crashreporter.utils.Constants;
import com.dhamodharan.crashreporter.utils.CrashUtil;
import com.dhamodharan.crashreporter.utils.FileUtils;
import com.dhamodharan.crashreporter.utils.SimplePageChangeListener;

import java.io.File;

public class CrashReporterActivity extends AppCompatActivity {

    private MainPagerAdapter mainPagerAdapter;
    private int selectedTabPosition = 0;

    //region activity callbacks
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.log_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_crash_logs) {
            new AlertDialog.Builder(this)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            clearCrashLog();
                        }
                    })
                    .setMessage(R.string.delete_confirmation_msg)
                    .setNegativeButton(android.R.string.no, null)
                    .show();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crash_reporter_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.crash_reporter));
        toolbar.setSubtitle(getApplicationName());
        setSupportActionBar(toolbar);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }
    //endregion

    private void clearCrashLog() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String crashReportPath = TextUtils.isEmpty(CrashReporter.getCrashReportPath()) ?
                        CrashUtil.getDefaultPath() : CrashReporter.getCrashReportPath();

                File[] logs = new File(crashReportPath).listFiles();
                for (File file : logs) {
                    FileUtils.delete(file);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mainPagerAdapter.clearLogs();
                    }
                });
            }
        }).start();
    }

    private void setupViewPager(ViewPager viewPager) {
        String[] titles = {getString(R.string.crashes), getString(R.string.exceptions)};
        mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), titles);
        viewPager.setAdapter(mainPagerAdapter);

        viewPager.addOnPageChangeListener(new SimplePageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                selectedTabPosition = position;
            }
        });

        Intent intent = getIntent();
        if (intent != null && !intent.getBooleanExtra(Constants.LANDING, false)) {
            selectedTabPosition = 1;
        }
        viewPager.setCurrentItem(selectedTabPosition);
    }

    private String getApplicationName() {
        ApplicationInfo applicationInfo = getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : getString(stringId);
    }

}
