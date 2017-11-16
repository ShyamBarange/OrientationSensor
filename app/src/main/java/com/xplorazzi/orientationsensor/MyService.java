package com.xplorazzi.orientationsensor;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by Xplorazzi on 09-Nov-17.
 */

public class MyService extends Service {

    final private ToneGenerator beeper = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);

    Context context = this;

    private static final int NOTIFY_ID = 9906;
    static final String EXTRA_RESULT_CODE = "resultCode";
    static final String EXTRA_RESULT_INTENT = "resultIntent";

    static final String STATUS = "";
    static final String ACTION_STOP = BuildConfig.APPLICATION_ID + ".STOP";
    private int resultCode;
    private Intent resultData;


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful
//        beeper.startTone(ToneGenerator.TONE_PROP_ACK);
//        return Service.START_NOT_STICKY;

        if (intent != null && intent.getExtras() != null) {
            float pitch = intent.getIntExtra("pitch_value", 0);
            float roll = intent.getIntExtra("roll_value", 0);


            Log.d("Pitch:", Float.toString(pitch));
        }


        if (intent.getAction() == null) {
            resultCode = intent.getIntExtra(EXTRA_RESULT_CODE, 1337);
            resultData = intent.getParcelableExtra(EXTRA_RESULT_INTENT);
            foregroundify();
        }

//            else if (ACTION_RECORD.equals(intent.getAction())) {
//                if (resultData != null) {
//
//                    try {
//                        TimeUnit.SECONDS.sleep(1);
////                    startCapture();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//                } else {
//                    Intent ui = new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(ui);
//                }
//            }

        else if (ACTION_STOP.equals(intent.getAction())) {
            beeper.startTone(ToneGenerator.TONE_PROP_NACK);
            stopForeground(true);
            Log.d("align service", "Service stoped");
            stopSelf();
        }


        return (START_NOT_STICKY);


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void foregroundify() {
        NotificationCompat.Builder b =
                new NotificationCompat.Builder(this);

        b.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL);

        b.setContentTitle(getString(R.string.app_name))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("Alignment Status");

        b.addAction(R.drawable.ic_record_white_24dp,
                getString(R.string.status),
                buildPendingIntent(STATUS)
        );

        b.addAction(R.drawable.ic_eject_white_24dp,
                getString(R.string.stop_service),
                buildPendingIntent(ACTION_STOP));

        startForeground(NOTIFY_ID, b.build());

    }

    private PendingIntent buildPendingIntent(String action) {
        Intent i = new Intent(this, getClass());
        i.setAction(action);

        return (PendingIntent.getService(this, 0, i, 0));
    }


    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        throw new IllegalStateException("Binding not supported. Go away.");
    }
}