package in.manki.batterymonitor;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private SimpleCursorAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        BatteryLogDatabse db = new BatteryLogDatabse(getApplicationContext(), Calendar.getInstance());
        Cursor statuses = db.listBatteryStatuses();

        String[] fromColumns = {
                BatteryLogDatabse.Fields.UTC_TIMESTAMP,
                BatteryLogDatabse.Fields.BATTERY_PERCENT
        };
        int[] toViews = { android.R.id.text2 };
        adapter = new SimpleCursorAdapter(
                getApplicationContext(), android.R.layout.simple_list_item_2, statuses,
                fromColumns, toViews, 0);

        ListView list = (ListView) findViewById(R.id.battery_statuses);
        list.setAdapter(adapter);
    }

    private class LoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(getApplicationContext()) {
                @Override
                public Cursor loadInBackground() {
                    BatteryLogDatabse db =
                            new BatteryLogDatabse(getApplicationContext(), Calendar.getInstance());
                    return db.listBatteryStatuses();
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            adapter.swapCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            adapter.swapCursor(null);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        startService(new Intent(this, BatteryLevelTrackerService.class));
        setContentView(R.layout.activity_main);
    }
}
