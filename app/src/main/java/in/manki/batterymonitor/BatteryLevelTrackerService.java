package in.manki.batterymonitor;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class BatteryLevelTrackerService extends Service {
    private static final String TAG = "BatteryLevelTrackingSvc";
    private static final int NOW = 0;  // 0 milliseconds from now.

    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service starting");
        if (alarmManager == null) {
            alarmManager =
                    (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        }
        if (alarmIntent == null) {
            Intent alarmReceiverIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
            alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, alarmReceiverIntent, 0);
        }

        long whenToStartTicking = NOW;
        long interval = AlarmManager.INTERVAL_HALF_HOUR;
        assert alarmManager != null;
        assert alarmIntent != null;
        alarmManager.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP, whenToStartTicking, interval, alarmIntent);
        Toast.makeText(this, "Alarm started!", Toast.LENGTH_LONG).show();

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding. Return null.
        return null;
    }
}
