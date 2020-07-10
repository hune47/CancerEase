package com.graduation.CancerEaseProj.Activities.SleepTracker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;

import com.graduation.CancerEaseProj.R;
import com.graduation.CancerEaseProj.Utilities.Utils;

import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class SleepQualityActivity extends AppCompatActivity {
    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;
	public static final int DETECT_NONE = 0;
	public static final int DETECT_SNORE = 1;
	public static int selectedDetection = DETECT_NONE;
	private DetectorThread detectorThread;
	private RecorderThread recorderThread;
	private DrawThread drawThread;
	public static int snoreValue = 0;
	private View mainView;
	private Button mSleepRecordBtn, mAlarmBtn, mRecordBtn, mTestBtn;
	private TextView txtAbs;
	private Toast mToast;
	private Handler rhandler = new Handler();
	private Handler showhandler = null;
	private Handler alarmhandler = null;
	private Intent intent;
	private PendingIntent pendingIntent;
	private AlarmManager am;
	private SurfaceView sfv;
	private Paint mPaint;
    /////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sleep_quality);
        setTitle("     خدمات المرضى");
        requestAudioPermissions();
        initializeItems();
	}
    /////////////////////////////////////////////////////////////////////////////////////////////////
    private void initializeItems(){
        Utils.forceRTLIfSupported(this);
        mSleepRecordBtn = this.findViewById(R.id.btnSleepRecord);
        mAlarmBtn =  findViewById(R.id.btnSelectAlarm);
        mRecordBtn =  findViewById(R.id.btnRecordAlarm);
        mTestBtn =  findViewById(R.id.btnAlarmTest);
        txtAbs = findViewById(R.id.txtaverageAbsValue);
        sfv =  this.findViewById(R.id.SurfaceView);

        intent = new Intent(SleepQualityActivity.this, AlarmReceiverActivity.class);
        pendingIntent = PendingIntent.getActivity(SleepQualityActivity.this, 2, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        am = (AlarmManager) getSystemService(ALARM_SERVICE);

        mPaint = new Paint();
        mPaint.setColor(Color.GREEN);

        /**
         * show variable handler
         */
        showhandler = new Handler() {
            public void handleMessage(Message msg) {
                txtAbs.setText(msg.obj.toString());
            }
        };

        /**
         * Output alarm handler
         */
        alarmhandler = new Handler() {
            public void handleMessage(Message msg) {
                int interval = 1;
                int i = msg.arg1;
                setLevel(i);
                AlarmStaticVariables.level = AlarmStaticVariables.level1;
                am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                        + (interval * 1000), pendingIntent);
            }
        };
        /**
         * Sleep Record Button
         */
        mSleepRecordBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                selectedDetection = DETECT_SNORE;
                recorderThread = new RecorderThread(showhandler);
                recorderThread.start();
                detectorThread = new DetectorThread(recorderThread,
                        alarmhandler);
                detectorThread.start();
                drawThread = new DrawThread(sfv.getHeight() / 2, sfv, mPaint);
                drawThread.start();
                mToast = Toast.makeText(getApplicationContext(),
                        "بدء التسجيل والكشف", Toast.LENGTH_LONG);
                mToast.show();
            }
        });
        ImageView backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                am.cancel(pendingIntent);
                finish();
            }
        });
        /**
         * Select alarm Button
         */
        mAlarmBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(SleepQualityActivity.this,
                        AlarmSelectActivity.class);
                startActivity(intent);
            }
        });

        /**
         * Record name Button
         */
        mRecordBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                rhandler.removeCallbacks(recordActivity);
                rhandler.postDelayed(recordActivity, 1000);
            }
        });

        /**
         * Test
         */
        mTestBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                int level = 1;
                setLevel(level);
                startOneShoot();
            }
        });
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////
	private Runnable recordActivity = new Runnable() {
		public void run() {
			Intent intent = new Intent(SleepQualityActivity.this,
					AlarmRecordActivity.class);
			startActivity(intent);
		}
	};
    /////////////////////////////////////////////////////////////////////////////////////////////////
	public void startOneShoot() {
		int i = 5;
		am.set(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis() + (i * 1000), pendingIntent);
	}
    /////////////////////////////////////////////////////////////////////////////////////////////////
	public void setLevel(int l) {
		switch (l) {
		case 0:
			AlarmStaticVariables.level = AlarmStaticVariables.level0;
			break;
		case 1:
			AlarmStaticVariables.level = AlarmStaticVariables.level1;
			break;
		case 2:
			AlarmStaticVariables.level = AlarmStaticVariables.level2;
			break;
		case 3:
			AlarmStaticVariables.level = AlarmStaticVariables.level3;
			break;
		default:
			AlarmStaticVariables.level = AlarmStaticVariables.level1;
			break;
		}
	}
    /////////////////////////////////////////////////////////////////////////////////////////////////
	private void goHomeView() {
		setContentView(mainView);
		if (recorderThread != null) {
			recorderThread.stopRecording();
			recorderThread = null;
		}
		if (detectorThread != null) {
			detectorThread.stopDetection();
			detectorThread = null;
		}
		selectedDetection = DETECT_NONE;
	}
    /////////////////////////////////////////////////////////////////////////////////////////////////
    private void requestAudioPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            //When permission is not granted by user, show them message why this permission is needed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText(this, "يرجى اعطاء الاذن لتسجيل الصوت لتتمكن من استخدام خاصية قياس جودة النوم", Toast.LENGTH_LONG).show();

                //Give user option to still opt-in the permissions
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);

            } else {
                // Show user dialog to grant permission to record audio
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);
            }
        }
        //If permission is granted, then go ahead recording audio
        else if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            //Go ahead with recording audio now
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "لا يوجد اذن لتسجيل الصوت!", Toast.LENGTH_LONG).show();
                    finish();
                }
                return;
            }
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			goHomeView();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
    /////////////////////////////////////////////////////////////////////////////////////////////////
	protected void onDestroy() {
		super.onDestroy();
		android.os.Process.killProcess(android.os.Process.myPid());
	}
    /////////////////////////////////////////////////////////////////////////////////////////////////
}
