package in.manki.batterymonitor;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
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
        Toast.makeText(this, "Service starting", Toast.LENGTH_LONG).show();
        Log.i(TAG, "Service starting");
        if (alarmManager == null) {
            alarmManager =
                    (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        }
        if (alarmIntent == null) {
            Intent alarmReceiverIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
            alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, alarmReceiverIntent, 0);
        }

        long whenToStartTicking = NOW;
        //~long interval = AlarmManager.INTERVAL_HOUR;
        long interval = 2000;
        assert alarmManager != null;
        alarmManager.setRepeating(
        //~alarmManager.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP, whenToStartTicking, interval, alarmIntent);

        return START_STICKY;
    }

    private static class AlarmReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Alarm ticking", Toast.LENGTH_LONG).show();
            Log.i(TAG, "alarmTick " + getBatteryStatus(context));
        }

        private BatteryStatus getBatteryStatus(Context context) {
            IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, filter);

            assert batteryStatus != null;
            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            ChargeState chargeState =
                    (status == BatteryManager.BATTERY_STATUS_CHARGING
                            || status == BatteryManager.BATTERY_STATUS_FULL)
                            ? ChargeState.CHARGING : ChargeState.NOT_CHARGING;

            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int percentage = (int) (100.0 * level / scale);

            return new BatteryStatus(chargeState, percentage);
        }
    }

    private static final class BatteryStatus {
        final ChargeState chargeState;
        final int chargePercentage;

        BatteryStatus(ChargeState chargeState, int chargePercentage) {
            this.chargeState = chargeState;
            this.chargePercentage = chargePercentage;
        }

        @Override
        public String toString() {
            return String.format("state=%s percent=%s%%", chargeState, chargePercentage);
        }
    }

    private static enum ChargeState {
        CHARGING,
        NOT_CHARGING,
        ;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding. Return null.
        return null;
    }
}
