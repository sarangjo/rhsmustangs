package com.sarangjoshi.rhsmustangs.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.sarangjoshi.rhsmustangs.content.SDay;
import com.sarangjoshi.rhsmustangs.content.SHoliday;
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
    //private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + ScheduleContract.UDAY_TABLE_NAME;

    public ScheduleDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        init(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        deleteUpdates(db);

        // Recreate tables
        onCreate(db);
    }

    /**
     * Initializes the database.
     */
    public void init() {
        init(this.getWritableDatabase());
    }

    /**
     * Initializes the database
     *
     * @param db
     */
    public void init(SQLiteDatabase db) {
        db.execSQL(UpdatedDayEntry.CREATE_TABLE);
        db.execSQL(PeriodEntry.CREATE_UDAY_TABLE);
        db.execSQL(HolidayEntry.CREATE_TABLE);
    }

    public void deleteUpdates() {
        deleteUpdates(this.getWritableDatabase());
    }

    public void deleteUpdates(SQLiteDatabase db) {
        db.execSQL(DROP + UpdatedDayEntry.TABLE_NAME);
        db.execSQL(DROP + PeriodEntry.UDAY_TABLE_NAME);
        db.execSQL(DROP + HolidayEntry.TABLE_NAME);
    }

    // GROUPS

    /**
     * Updates the group number of the given updated day
     *
     * @param day
     * @return how many rows were updated
     */
    public int updateGroup(SUpdatedDay day) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UpdatedDayEntry.COLUMN_GROUP_N, day.getGroupN());

        // Selecting the row
        String selection = UpdatedDayEntry.COLUMN_DATE + " = ?";
        String[] args = {SHelper.dateToString(day.getDate())};

        // Updates
        return db.update(UpdatedDayEntry.TABLE_NAME, values, selection, args);
    }

    // UPDATED DAYS

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
            createUpdatedDayPeriod(p, uday_id);
        }

        return uday_id;
    }

    /**
     * Creates a Period entry to the database.
     *
     * @param p
     * @param uday_id
     * @return
     */
    public long createUpdatedDayPeriod(SPeriod p, long uday_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Generate values from period
        ContentValues values = PeriodEntry.periodToContentValues(p);
        if(p.getNote() != null) {
            values.put(PeriodEntry.COLUMN_NOTE, p.getNote());
        }
        values.put(PeriodEntry.COLUMN_UPDATED_DAY_ID, uday_id);

        return db.insert(PeriodEntry.UDAY_TABLE_NAME, null, values);
    }

    /**
     * Saves the given day to the database.
     *
     * @param day
     * @return the id of the updated day
     */
    private long createUpdatedDay(SUpdatedDay day) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = UpdatedDayEntry.updatedDayToContentValues(day);//new ContentValues();
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

        Cursor c;
        List<SUpdatedDay> days = new ArrayList<>();

        try {
            c = db.rawQuery(selectQuery, null);
        } catch (Exception e) {
            return days;
        }

        // Scrolls through the list to create each Updated Day
        if (c.moveToFirst()) {
            do {
                long id = c.getLong(c.getColumnIndex(UpdatedDayEntry._ID));

                String dateString = c.getString(c.getColumnIndex(UpdatedDayEntry.COLUMN_DATE));
                String groupsString = c.getString(c.getColumnIndex(UpdatedDayEntry.COLUMN_GROUP_NAMES));

                Calendar date = SHelper.stringToCalendar(dateString);
                String[] names = SHelper.stringToGroups(groupsString);

                SUpdatedDay day = new SUpdatedDay(date, names);

                int groupN;
                try {
                    groupN = c.getInt(c.getColumnIndex(UpdatedDayEntry.COLUMN_GROUP_N));
                } catch (IllegalStateException e) {
                    groupN = 0;
                }

                day.setGroupN(groupN);


                day.addPeriods(getPeriods("SELECT * FROM " + PeriodEntry.UDAY_TABLE_NAME + " p WHERE p." +
                        PeriodEntry.COLUMN_UPDATED_DAY_ID + " = " + id));

                days.add(day);
            } while (c.moveToNext());
        }
        c.close();
        return days;
    }

    public List<SPeriod> getPeriods(String selectQuery) {
        SQLiteDatabase db = this.getReadableDatabase();

        Log.i(LOG_ID, selectQuery);

        Cursor c;
        try {
            c = db.rawQuery(selectQuery, null);
        } catch (Exception e) {
            return new ArrayList<>();
        }

        List<SPeriod> periods = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                String periodShort = c.getString(c.getColumnIndex(PeriodEntry.COLUMN_SHORT));
                String name = c.getString(c.getColumnIndex(PeriodEntry.COLUMN_NAME));
                int sh = c.getInt(c.getColumnIndex(PeriodEntry.COLUMN_START_HR));
                int sm = c.getInt(c.getColumnIndex(PeriodEntry.COLUMN_START_MIN));
                int eh = c.getInt(c.getColumnIndex(PeriodEntry.COLUMN_END_HR));
                int em = c.getInt(c.getColumnIndex(PeriodEntry.COLUMN_END_MIN));
                int g = c.getInt(c.getColumnIndex(PeriodEntry.COLUMN_GROUP_N));

                String note;
                try {
                    note = c.getString(c.getColumnIndex(PeriodEntry.COLUMN_NOTE));
                } catch (Exception e) {
                    note = null;
                }

                SPeriod p = new SPeriod(periodShort, name, sh, sm, eh, em, g);
                p.setNote(note);

                periods.add(p);
            } while (c.moveToNext());
        }
        c.close();
        return periods;
    }

    // HOLIDAYS

    public long createHoliday(SHoliday holiday) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = HolidayEntry.holidayToContentValues(holiday);
        return db.insert(HolidayEntry.TABLE_NAME, null, values);
    }

    public List<SHoliday> getHolidays() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + HolidayEntry.TABLE_NAME;

        Log.i(LOG_ID, selectQuery);

        List<SHoliday> holidays = new ArrayList<>();
        Cursor c;

        try {
            c = db.rawQuery(selectQuery, null);
        } catch (Exception e) {
            return holidays;
        }

        if (c.moveToFirst()) {
            do {
                String name = c.getString(c.getColumnIndex(HolidayEntry.COLUMN_NAME));
                Calendar start = SHelper.stringToCalendar(c.getString(c.getColumnIndex(HolidayEntry.COLUMN_START_NAME)));
                Calendar end = SHelper.stringToCalendar(c.getString(c.getColumnIndex(HolidayEntry.COLUMN_END_NAME)));
                SHoliday h = new SHoliday(name, start, end);

                holidays.add(h);
            } while (c.moveToNext());
        }
        c.close();
        return holidays;
    }

    // BASE DAYS

    public long saveBaseDay(SDay day) {
        long bday_id = createBaseDay(day);

        // Save periods
        for (SPeriod p : day.getAllPeriods()) {
            long l = createBaseDayPeriod(p, day.getDayOfWeek());
            Log.d("lel", l + "");
        }

        return bday_id;
    }

    public long createBaseDay(SDay day) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = BaseDayEntry.dayToContentValues(day);
        return db.insert(BaseDayEntry.TABLE_NAME, null, values);
    }

    public long createBaseDayPeriod(SPeriod p, int dayOfWeek) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Generate values from period
        ContentValues values = PeriodEntry.periodToContentValues(p);
        values.put(PeriodEntry.COLUMN_DAY_OF_WEEK, dayOfWeek);

        return db.insert(PeriodEntry.BDAY_TABLE_NAME, null, values);
    }

    public List<SDay> getBaseDays() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + BaseDayEntry.TABLE_NAME;

        Log.i(LOG_ID, selectQuery);

        Cursor c;
        List<SDay> days = new ArrayList<>();

        try {
            c = db.rawQuery(selectQuery, null);
        } catch (Exception e) {
            return days;
        }

        // Scrolls through the list to create each base day
        if (c.moveToFirst()) {
            do {
                //long id = c.getLong(c.getColumnIndex(BaseDayEntry._ID));

                int dayOfWeek = c.getInt(c.getColumnIndex(BaseDayEntry.COLUMN_DAY_OF_WEEK));
                String groupsString = c.getString(c.getColumnIndex(UpdatedDayEntry.COLUMN_GROUP_NAMES));

                String[] names = SHelper.stringToGroups(groupsString);

                SDay day = new SDay(dayOfWeek, names);

                day.addPeriods(getPeriods("SELECT * FROM " + PeriodEntry.BDAY_TABLE_NAME + " p WHERE p." +
                        PeriodEntry.COLUMN_DAY_OF_WEEK + " = " + dayOfWeek));

                days.add(day);
            } while (c.moveToNext());
        }
        c.close();
        return days;
    }

    public void initBaseDays() {
        initBaseDays(this.getWritableDatabase());
    }

    public void initBaseDays(SQLiteDatabase db) {
        db.execSQL(BaseDayEntry.CREATE_TABLE);
        db.execSQL(PeriodEntry.CREATE_BDAY_TABLE);
    }

    public void deleteBaseDays() {
        deleteBaseDays(this.getWritableDatabase());
    }

    public void deleteBaseDays(SQLiteDatabase db) {
        db.execSQL(DROP + BaseDayEntry.TABLE_NAME);
        db.execSQL(DROP + PeriodEntry.BDAY_TABLE_NAME);
    }

    // TABLE INFO

    public static abstract class HolidayEntry implements BaseColumns {
        public static final String TABLE_NAME = "holiday";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_START_NAME = "start";
        public static final String COLUMN_END_NAME = "end";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + _ID + " INTEGER PRIMARY KEY,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_START_NAME + " DATETIME,"
                + COLUMN_END_NAME + " DATETIME"
                + ")";

        public static ContentValues holidayToContentValues(SHoliday holiday) {
            ContentValues values = new ContentValues();

            values.put(COLUMN_NAME, holiday.getName());
            values.put(COLUMN_START_NAME, SHelper.dateToString(holiday.getStart()));
            values.put(COLUMN_END_NAME, SHelper.dateToString(holiday.getEnd()));

            return values;
        }
    }

    public static abstract class UpdatedDayEntry implements BaseColumns {
        public static final String TABLE_NAME = "updated_day";
        //public static final String COLUMN_PARSE_ID = "parseid";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_GROUP_NAMES = "groups";
        public static final String COLUMN_GROUP_N = "group_n";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + _ID + " INTEGER PRIMARY KEY,"
                + COLUMN_DATE + " DATETIME,"
                + COLUMN_GROUP_NAMES + " TEXT,"
                + COLUMN_GROUP_N + " INTEGER"
                + ")";

        public static ContentValues updatedDayToContentValues(SUpdatedDay day) {
            ContentValues values = new ContentValues();

            values.put(UpdatedDayEntry.COLUMN_DATE, SHelper.dateToString(day.getDate()));
            values.put(UpdatedDayEntry.COLUMN_GROUP_NAMES, SHelper.groupsToString(day.getGroupNames()));
            values.put(UpdatedDayEntry.COLUMN_GROUP_N, day.getGroupN());

            return values;
        }
    }

    public static abstract class PeriodEntry implements BaseColumns {
        public static final String UDAY_TABLE_NAME = "uday_period";
        public static final String BDAY_TABLE_NAME = "bday_period";

        // TODO: Optimize with SPeriod's Parse constants
        public static final String COLUMN_SHORT = "short";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_GROUP_N = "group_n";
        public static final String COLUMN_START_HR = "start_hr";
        public static final String COLUMN_START_MIN = "start_min";
        public static final String COLUMN_END_HR = "end_hr";
        public static final String COLUMN_END_MIN = "end_min";

        public static final String COLUMN_NOTE = "note";

        public static final String COLUMN_UPDATED_DAY_ID = "updated_day_id";

        public static final String COLUMN_DAY_OF_WEEK = "day_of_week";

        public static final String CREATE_UDAY_TABLE = "CREATE TABLE " + UDAY_TABLE_NAME + "("
                + _ID + " INTEGER PRIMARY KEY,"
                + COLUMN_SHORT + " CHAR(2),"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_GROUP_N + " INTEGER,"
                + COLUMN_START_HR + " INTEGER,"
                + COLUMN_START_MIN + " INTEGER,"
                + COLUMN_END_HR + " INTEGER,"
                + COLUMN_END_MIN + " INTEGER,"
                + COLUMN_NOTE + " MEDIUMTEXT,"
                + COLUMN_UPDATED_DAY_ID + " INTEGER"
                + ")";

        public static final String CREATE_BDAY_TABLE = "CREATE TABLE " + BDAY_TABLE_NAME + "("
                + _ID + " INTEGER PRIMARY KEY,"
                + COLUMN_SHORT + " CHAR(2),"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_GROUP_N + " INTEGER,"
                + COLUMN_START_HR + " INTEGER,"
                + COLUMN_START_MIN + " INTEGER,"
                + COLUMN_END_HR + " INTEGER,"
                + COLUMN_END_MIN + " INTEGER,"
                + COLUMN_DAY_OF_WEEK + " INTEGER"
                + ")";

        public static ContentValues periodToContentValues(SPeriod p) {
            ContentValues values = new ContentValues();

            values.put(PeriodEntry.COLUMN_SHORT, p.getShort().substring(0, 2));
            values.put(PeriodEntry.COLUMN_NAME, p.getRawClassName());
            values.put(PeriodEntry.COLUMN_GROUP_N, p.getGroupN());
            values.put(PeriodEntry.COLUMN_START_HR, p.getStart().hour);
            values.put(PeriodEntry.COLUMN_START_MIN, p.getStart().minute);
            values.put(PeriodEntry.COLUMN_END_HR, p.getEnd().hour);
            values.put(PeriodEntry.COLUMN_END_MIN, p.getEnd().minute);

            return values;
        }
    }

    public static abstract class BaseDayEntry implements BaseColumns {
        public static final String TABLE_NAME = "base_day";

        public static final String COLUMN_DAY_OF_WEEK = "day_of_week";
        public static final String COLUMN_GROUP_NAMES = "groups";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + _ID + " INTEGER PRIMARY KEY,"
                + COLUMN_DAY_OF_WEEK + " INTEGER,"
                + COLUMN_GROUP_NAMES + " TEXT"
                + ")";

        public static ContentValues dayToContentValues(SDay day) {
            ContentValues values = new ContentValues();

            values.put(BaseDayEntry.COLUMN_DAY_OF_WEEK, day.getDayOfWeek());
            values.put(BaseDayEntry.COLUMN_GROUP_NAMES, SHelper.groupsToString(day.getGroupNames()));

            return values;
        }

    }
}
