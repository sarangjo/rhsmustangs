package com.sarangjoshi.rhsmustangs.helper;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.util.Log;

import com.sarangjoshi.rhsmustangs.content.SPeriod;
import com.sarangjoshi.rhsmustangs.content.SUpdatedDay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Handles the database data saving of updated days.
 *
 * @author Sarang Joshi
 */
public class ScheduleDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Schedule.db";
    public static final int DATABASE_VERSION = 1;
    public static final String LOG_ID = "ScheduleDatabase";

    private static final String DROP = "DROP TABLE IF EXISTS ";
    // Commands
    //private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + ScheduleContract.TABLE_NAME;

    public ScheduleDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        init(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        deleteAll(db);

        // Recreate tables
        onCreate(db);
    }

    public void init() {
        init(this.getWritableDatabase());
    }

    public void init(SQLiteDatabase db) {
        db.execSQL(UpdatedDayEntry.CREATE_TABLE);
        db.execSQL(PeriodEntry.CREATE_TABLE);
    }

    public void deleteAll() {
        deleteAll(this.getWritableDatabase());
    }

    public void deleteAll(SQLiteDatabase db) {
        db.execSQL(DROP + UpdatedDayEntry.TABLE_NAME);
        db.execSQL(DROP + PeriodEntry.TABLE_NAME);
    }

    /**
     * Saves the given updated day, as well as its periods.
     *
     * @param day
     * @return
     */
    public long saveUpdatedDay(SUpdatedDay day) {
        long uday_id = createUpdatedDay(day);

        // Save periods
        for (SPeriod p : day.getAllPeriods()) {
            createPeriod(p, uday_id);
        }

        return uday_id;
    }

    public long createPeriod(SPeriod p, long uday_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PeriodEntry.COLUMN_NAME_SHORT, p.getShort().substring(0, 2));
        values.put(PeriodEntry.COLUMN_NAME_NAME, p.getRawClassName());
        values.put(PeriodEntry.COLUMN_NAME_GROUP_N, p.getGroupN());
        values.put(PeriodEntry.COLUMN_NAME_START_HR, p.getStart().hour);
        values.put(PeriodEntry.COLUMN_NAME_START_MIN, p.getStart().minute);
        values.put(PeriodEntry.COLUMN_NAME_END_HR, p.getEnd().hour);
        values.put(PeriodEntry.COLUMN_NAME_END_MIN, p.getEnd().minute);

        values.put(PeriodEntry.COLUMN_NAME_UPDATED_DAY_ID, uday_id);

        return db.insert(PeriodEntry.TABLE_NAME, null, values);
    }

    /**
     * Saves the given day to the database.
     *
     * @param day
     * @return the id of the updated day
     */
    private long createUpdatedDay(SUpdatedDay day) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UpdatedDayEntry.COLUMN_NAME_DATE, SHelper.dateToString(day.getDate()));
        values.put(UpdatedDayEntry.COLUMN_NAME_GROUP_NAMES, SHelper.groupsToString(day.getGroupNames()));

        return db.insert(UpdatedDayEntry.TABLE_NAME, null, values);
    }

    /**
     * Retrieves the saved updated days from the database.
     *
     * @return
     */
    public List<SUpdatedDay> getUpdatedDays() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + UpdatedDayEntry.TABLE_NAME;

        Log.i(LOG_ID, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        List<SUpdatedDay> days = new ArrayList<>();

        // Scrolls through the list to create each Updated Day
        if (c.moveToFirst()) {
            do {
                long id = c.getLong(c.getColumnIndex(UpdatedDayEntry._ID));

                String dateString = c.getString(c.getColumnIndex(UpdatedDayEntry.COLUMN_NAME_DATE));
                String groupsString = c.getString(c.getColumnIndex(UpdatedDayEntry.COLUMN_NAME_GROUP_NAMES));

                Calendar date = SHelper.stringToCalendar(dateString);
                String[] names = SHelper.stringToGroups(groupsString);

                SUpdatedDay day = new SUpdatedDay(date, names);

                day.addPeriods(getPeriods(id));
                days.add(day);
            } while (c.moveToNext());
        }

        return days;
    }

    /**
     * SELECT * FROM period p WHERE p.updated_day_id = 134234;
     *
     * @param uday_id
     * @return
     */
    public List<SPeriod> getPeriods(long uday_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + PeriodEntry.TABLE_NAME + " p WHERE p." +
                PeriodEntry.COLUMN_NAME_UPDATED_DAY_ID + " = " + uday_id;

        Log.i(LOG_ID, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        List<SPeriod> periods = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                String periodShort = c.getString(c.getColumnIndex(PeriodEntry.COLUMN_NAME_SHORT));
                String name = c.getString(c.getColumnIndex(PeriodEntry.COLUMN_NAME_NAME));
                int sh = c.getInt(c.getColumnIndex(PeriodEntry.COLUMN_NAME_START_HR));
                int sm = c.getInt(c.getColumnIndex(PeriodEntry.COLUMN_NAME_START_MIN));
                int eh = c.getInt(c.getColumnIndex(PeriodEntry.COLUMN_NAME_END_HR));
                int em = c.getInt(c.getColumnIndex(PeriodEntry.COLUMN_NAME_END_MIN));
                int g = c.getInt(c.getColumnIndex(PeriodEntry.COLUMN_NAME_GROUP_N));
                SPeriod p = new SPeriod(periodShort, name, sh, sm, eh, em, g);

                periods.add(p);
            } while (c.moveToNext());
        }
        return periods;
    }

    public static abstract class UpdatedDayEntry implements BaseColumns {
        public static final String TABLE_NAME = "updated_day";
        //public static final String COLUMN_NAME_PARSE_ID = "parseid";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_GROUP_NAMES = "groups";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + _ID + " INTEGER PRIMARY KEY,"
                + COLUMN_NAME_DATE + " DATETIME,"
                + COLUMN_NAME_GROUP_NAMES + " TEXT"
                + ")";
    }

    public static abstract class PeriodEntry implements BaseColumns {
        public static final String TABLE_NAME = "period";
        //public static final String COLUMN_NAME_PARSE_ID = "parseid";
        public static final String COLUMN_NAME_SHORT = "short";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_GROUP_N = "group_n";
        public static final String COLUMN_NAME_START_HR = "start_hr";
        public static final String COLUMN_NAME_START_MIN = "start_min";
        public static final String COLUMN_NAME_END_HR = "end_hr";
        public static final String COLUMN_NAME_END_MIN = "end_min";
        public static final String COLUMN_NAME_UPDATED_DAY_ID = "updated_day_id";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + _ID + " INTEGER PRIMARY KEY,"
                + COLUMN_NAME_SHORT + " CHAR(2),"
                + COLUMN_NAME_NAME + " TEXT,"
                + COLUMN_NAME_GROUP_N + " INTEGER,"
                + COLUMN_NAME_START_HR + " INTEGER,"
                + COLUMN_NAME_START_MIN + " INTEGER,"
                + COLUMN_NAME_END_HR + " INTEGER,"
                + COLUMN_NAME_END_MIN + " INTEGER,"
                + COLUMN_NAME_UPDATED_DAY_ID + " INTEGER"
                + ")";
    }
}
