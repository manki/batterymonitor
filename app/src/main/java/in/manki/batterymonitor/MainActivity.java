package in.manki.batterymonitor;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

    @Override
    protected void onStart() {
        super.onStart();
        startService(new Intent(this, BatteryLevelTrackerService.class));
        finish();
    }
}
