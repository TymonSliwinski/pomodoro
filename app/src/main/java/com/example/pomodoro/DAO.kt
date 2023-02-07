package com.example.pomodoro

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DAO(context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION
) {
    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "schedulesDB.db"
        private const val TABLE_SCHEDULES = "Schedules"

        private const val COLUMN_ID = "_id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_WORK_TIME = "workTime"
        private const val COLUMN_BREAK_TIME = "breakTime"
        private const val COLUMN_INTERVALS = "intervals"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_SCHEDULES_TABLE =
            "CREATE TABLE $TABLE_SCHEDULES( " +
            "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            "$COLUMN_TITLE TEXT," +
            "$COLUMN_WORK_TIME INTEGER," +
            "$COLUMN_BREAK_TIME INTEGER," +
            "$COLUMN_INTERVALS INTEGER)"
        db?.execSQL(CREATE_SCHEDULES_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_SCHEDULES")
        onCreate(db)
    }

    fun addSchedule(schedule: Schedule) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_TITLE, schedule.title)
        contentValues.put(COLUMN_WORK_TIME, schedule.workTime)
        contentValues.put(COLUMN_BREAK_TIME, schedule.breakTime)
        contentValues.put(COLUMN_INTERVALS, schedule.intervals)

        db.insert(TABLE_SCHEDULES, null, contentValues)
        db.close()
    }

    fun deleteSchedule(scheduleId: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_SCHEDULES, "$COLUMN_ID=${scheduleId}", null)
    }

    fun updateSchedule(id: Int, title: String, workTime: Int, breakTime: Int, intervals: Int) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_TITLE, title)
        contentValues.put(COLUMN_WORK_TIME, workTime)
        contentValues.put(COLUMN_BREAK_TIME, breakTime)
        contentValues.put(COLUMN_INTERVALS, intervals)

        db.update(TABLE_SCHEDULES, contentValues, "$COLUMN_ID=$id", null)
    }

    fun getSchedules(): List<Schedule> {
        val schedules: MutableList<Schedule> = ArrayList()
        val db = this.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM $TABLE_SCHEDULES",  null)
        if (cursor.moveToFirst()) {
            do {
                schedules.add(Schedule(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getInt(2),
                    cursor.getInt(3),
                    cursor.getInt(4)))
            } while (cursor.moveToNext())
        }
        db.close()
        cursor.close()
        return schedules
    }
}