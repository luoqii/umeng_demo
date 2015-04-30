package demo.ument.bbs.org.umengdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.umeng.update.UmengDownloadListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateConfig;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import java.io.File;


public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UpdateConfig.setDebug(true);
        UpdateConfig.setDeltaUpdate(false);
        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
            @Override
            public void onUpdateReturned(int updateStatus,UpdateResponse updateInfo) {
                switch (updateStatus) {
                    case UpdateStatus.Yes: // has update
//                        UmengUpdateAgent.showUpdateDialog(MainActivity.this, updateInfo);
                        new ApkDonwloadMonitor(MainActivity.this, updateInfo).start();
                        break;
                    case UpdateStatus.No: // has no update
                        Toast.makeText(MainActivity.this, "没有更新", Toast.LENGTH_SHORT).show();
                        break;
                    case UpdateStatus.NoneWifi: // none wifi
                        Toast.makeText(MainActivity.this, "没有wifi连接， 只在wifi下更新", Toast.LENGTH_SHORT).show();
                        break;
                    case UpdateStatus.Timeout: // java.lang.Stringtime out
                        Toast.makeText(MainActivity.this, "超时", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        UmengUpdateAgent.setDownloadListener(new UmengDownloadListener(){

            @Override
            public void OnDownloadStart() {
                Toast.makeText(MainActivity.this, "download start" , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnDownloadUpdate(int progress) {
                Toast.makeText(MainActivity.this, "download progress : " + progress + "%" , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnDownloadEnd(int result, String file) {
                //Toast.makeText(mContext, "download result : " + result , Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this, "download file path : " + file , Toast.LENGTH_SHORT).show();
            }
        });
        UmengUpdateAgent.update(this);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detail = new Intent(MainActivity.this, DetailActivity.class);

                startActivity(detail);
            }
        });
        findViewById(R.id.button_force_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UmengUpdateAgent.forceUpdate(MainActivity.this);
            }
        });
        findViewById(R.id.button_silent_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UmengUpdateAgent.silentUpdate(MainActivity.this);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class ApkDonwloadMonitor extends Thread {

        private final  UpdateResponse mInfo;
        private final Context mContext;

        public ApkDonwloadMonitor(Context context, UpdateResponse updateInfo) {
            mContext = context;
            mInfo = updateInfo;
        }

        @Override
        public void run() {
            super.run();

            UmengUpdateAgent.downloadedFile(MainActivity.this, mInfo);

            while (UmengUpdateAgent.downloadedFile(mContext, mInfo) == null) {
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            File apkFile = UmengUpdateAgent.downloadedFile(mContext, mInfo);
            if (null != apkFile) {
                apkFileReady(apkFile);
            }
        }

        private void apkFileReady(File apkFile) {
            Log.d(TAG, "apkFileReady: apkFile:" + apkFile);
        }

    }
}
