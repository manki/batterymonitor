package in.manki.batterymonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by mkannan on 8/15/15.
 */
public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Alarm ticking");
        BatteryStatus batteryStatus = getBatteryStatus(context);
        Log.d(TAG, "Alarm ticking " + batteryStatus);
        if (batteryStatus.getChargeState() == ChargeState.NOT_CHARGING) {
            BatteryLogDatabse db = new BatteryLogDatabse(context, Calendar.getInstance());
            db.saveBatteryStatus(batteryStatus);
        }
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
