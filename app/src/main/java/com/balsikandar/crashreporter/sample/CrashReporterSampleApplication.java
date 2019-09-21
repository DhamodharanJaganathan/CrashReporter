package com.balsikandar.crashreporter.sample;

import android.app.Application;
import android.os.Environment;
import android.widget.Toast;

import com.balsikandar.crashreporter.CrashReporter;

import java.io.File;

/**
 * Created by bali on 02/08/17.
 */

public class CrashReporterSampleApplication extends Application {

    private String folder_name="App_Logs";

    @Override
    public void onCreate() {
        super.onCreate();

        createFolder(folder_name);


        if (BuildConfig.DEBUG) {
            //initialise reporter with external path
            String crashReporterPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + folder_name;
            CrashReporter.initialize(this, crashReporterPath);
        }
    }


    public void createFolder(String fname) {
        String myfolder = Environment.getExternalStorageDirectory() + "/" + fname;
        File f = new File(myfolder);
        if (!f.exists())
            if (!f.mkdir()) {
                Toast.makeText(this, myfolder + " can't be created.", Toast.LENGTH_SHORT).show();

            } else
                Toast.makeText(this, myfolder + " can be created.", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, myfolder + " already exits.", Toast.LENGTH_SHORT).show();
    }
}
