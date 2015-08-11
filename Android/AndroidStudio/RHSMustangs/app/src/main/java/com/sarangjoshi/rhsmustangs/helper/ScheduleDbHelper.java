package com.sarangjoshi.rhsmustangs.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.sarangjoshi.rhsmustangs.content.SPeriod;
import com.sarangjoshi.rhsmustangs.content.SUpdatedDay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.sarangjoshi.rhsmustangs.helper.ScheduleDbHelper.UpdatedDayEntry.*;

/**
 * Created by Sarang on 8/12/2015.
 */
public class ScheduleDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Schedule.db";
    public static final int DATABASE_VERSION = 1;

    private static final String DROP = "DROP TABLE IF EXISTS ";
    // Commands
    //private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + ScheduleContract.TABLE_NAME;

    public ScheduleDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        db.execSQL(PeriodEntry.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP + TABLE_NAME);
        db.execSQL(DROP + PeriodEntry.TABLE_NAME);

        onCreate(db);
    }

    public long saveUpdatedDay(SUpdatedDay day) {
        long uday_id = createUpdatedDay(day);

        // Save periods
        for(SPeriod p : day.getAllPeriods()) {
            createPeriod(p, uday_id);
        }

        return uday_id;
    }

    public long createPeriod(SPeriod p, long uday_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PeriodEntry.COLUMN_NAME_SHORT, p.getShort().substring(0, 2));
        values.put(PeriodEntry.COLUMN_NAME_NAME, p.getClassName());
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
        values.put(COLUMN_NAME_DATE, SHelper.dateToString(day.getDate()));
        values.put(COLUMN_NAME_GROUP_NAMES, SHelper.groupsToString(day.getGroupNames()));

        return db.insert(TABLE_NAME, null, values);
    }

    /**
     * Retrieves the saved updated days from the database.
     *
     * @return
     */
    public List<SUpdatedDay> getUpdatedDays() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        Cursor c = db.rawQuery(selectQuery, null);

        List<SUpdatedDay> days = new ArrayList<>();

        if (c.moveToFirst()) {
            do {
                String dateString = c.getString(c.getColumnIndex(COLUMN_NAME_DATE));
                String groupsString = c.getString(c.getColumnIndex(COLUMN_NAME_GROUP_NAMES));

                Calendar date = SHelper.stringToCalendar(dateString);
                String[] names = SHelper.stringToGroups(groupsString);

                SUpdatedDay day = new SUpdatedDay(date, names);
                days.add(day);
            } while (c.moveToNext());
        }

        return days;
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
                + COLUMN_NAME_SHORT + " CHAR(2)"
                + COLUMN_NAME_NAME + " TEXT"
                + COLUMN_NAME_GROUP_N + " INTEGER"
                + COLUMN_NAME_START_HR + " INTEGER"
                + COLUMN_NAME_START_MIN + " INTEGER"
                + COLUMN_NAME_END_HR + " INTEGER"
                + COLUMN_NAME_END_MIN + " INTEGER"
                + COLUMN_NAME_UPDATED_DAY_ID + " INTEGER"
                + ")";
    }
}
