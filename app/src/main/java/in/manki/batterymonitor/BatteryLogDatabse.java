package in.manki.batterymonitor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Calendar;

public class BatteryLogDatabse extends SQLiteOpenHelper {
    private static final String TAG = "BatteryLogDatabase";
    private static final String DB_NAME = "BatteryLevelLog";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "BatteryLevels";

    private final Calendar calendar;

    public BatteryLogDatabse(Context context, Calendar calendar) {
        super(context, DB_NAME, null, DB_VERSION);
        this.calendar = calendar;
    }

    public void saveBatteryStatus(BatteryStatus batteryStatus) {
        ContentValues values = new ContentValues();
        values.put(Fields.UTC_TIMESTAMP, calendar.getTimeInMillis());
        values.put(Fields.LOCAL_TIME_HOURS, calendar.get(Calendar.HOUR_OF_DAY));
        values.put(Fields.LOCAL_TIME_MINUTES, calendar.get(Calendar.MINUTE));
        values.put(Fields.LOCAL_TIME_SECONDS, calendar.get(Calendar.SECOND));
        values.put(Fields.BATTERY_PERCENT, batteryStatus.getChargePercentage());

        SQLiteDatabase db = getWritableDatabase();
        try {
            db.insertOrThrow(TABLE_NAME, null, values);
        } catch (SQLException e) {
            Log.e(TAG, "Saving battery status failed", e);
        } finally {
            db.close();
        }
    }

    public Cursor listBatteryStatuses() {
        SQLiteDatabase db = getReadableDatabase();
        try {
            String[] columns = new String[] {
                    Fields.UTC_TIMESTAMP,
                    Fields.LOCAL_TIME_HOURS,
                    Fields.LOCAL_TIME_MINUTES,
                    Fields.LOCAL_TIME_SECONDS,
                    Fields.BATTERY_PERCENT
            };
            String whereClause = null;
            String[] whereArgs = new String[] {};
            String groupBy = null;
            String having = null;
            String orderBy = Fields.UTC_TIMESTAMP + " DESC";
            String limit = "100";
            return db.query(TABLE_NAME, columns, whereClause, whereArgs, groupBy, having, orderBy, limit);
        } finally {
            db.close();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + TABLE_NAME + " ("
                + Fields.RECORD_ID          + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Fields.UTC_TIMESTAMP      + " INTEGER NOT NULL, "
                + Fields.LOCAL_TIME_HOURS   + " INTEGER NOT NULL, "
                + Fields.LOCAL_TIME_MINUTES + " INTEGER NOT NULL, "
                + Fields.LOCAL_TIME_SECONDS + " INTEGER NOT NULL, "
                + Fields.BATTERY_PERCENT    + " INTEGER NOT NULL "
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new UnsupportedOperationException();
    }

    public static final class Fields {
        static final String RECORD_ID = "RecordId";
        static final String UTC_TIMESTAMP = "UtcTimestamp";
        static final String LOCAL_TIME_HOURS = "LocalTimeHours";
        static final String LOCAL_TIME_MINUTES = "LocalTimeMinutes";
        static final String LOCAL_TIME_SECONDS = "LocalTimeSeconds";
        static final String BATTERY_PERCENT = "BatteryPercent";
    }
}
