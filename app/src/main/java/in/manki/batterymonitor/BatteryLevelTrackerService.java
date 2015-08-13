package in.manki.batterymonitor;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class BatteryLevelTrackerService extends Service {
    private static final String TAG = "BatteryLevelTrackingSvc";

    public BatteryLevelTrackerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding. Return null.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service starting", Toast.LENGTH_LONG).show();
        Log.i(TAG, "onStartCommand " + getBatteryStatus());
        return START_STICKY;
    }

    private BatteryStatus getBatteryStatus() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getApplicationContext().registerReceiver(null, filter);
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        ChargeState chargeState = (status == BatteryManager.BATTERY_STATUS_CHARGING
                || status == BatteryManager.BATTERY_STATUS_FULL) ? ChargeState.CHARGING : ChargeState.NOT_CHARGING;

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int percentage = (int) (100.0 * level / scale);

        return new BatteryStatus(chargeState, percentage);
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
}
