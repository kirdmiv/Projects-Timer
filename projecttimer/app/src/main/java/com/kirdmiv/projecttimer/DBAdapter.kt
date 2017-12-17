package com.kirdmiv.projecttimer

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast
import org.apache.commons.lang3.StringEscapeUtils
import kotlin.properties.Delegates

/**
 * Created by Kirill on 12/16/2017.
 */

/**
 * Created by Kirill on 12/16/2017.
 */
public class DBAdapter(val context: Context) {
    public val KEY_ROWID: String = "_id"
    public val KEY_PROJECT: String = "project"
    public val KEY_STATUS: String = "status"
    public val KEY_TIMESPENT: String = "timespent"
    public val KEY_LASTACTIVATED: String = "lastactivated"

    public val TAG: String = "DBAdapter"
    public  val DATABASE_NAME: String = "timecard"
    public  val DATABASE_TABLE: String = "projects"
    public  val DATABASE_VERSION: Int = 1
    public  val DATABASE_CREATE: String = "create table IF NOT EXISTS `" + DATABASE_TABLE +
            "` (_id integer primary key autoincrement, " +
            "project text not null, status text not null, " +
            "timespent long, " + "lastactivated long);"



    private val DBHelper = DatabaseHelper(context, DATABASE_NAME, DATABASE_VERSION)


    public class DatabaseHelper(context: Context, DATABASE_NAME: String, DATABASE_VERSION: Int) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        public val TAG: String = "DBAdapter"
        public  val DATABASE_NAME: String = "timecard"
        public  val DATABASE_TABLE: String = "projects"
        public  val DATABASE_CREATE: String = "create table IF NOT EXISTS `" + DATABASE_TABLE +
                "` (_id integer primary key autoincrement, " +
                "project text not null, status text not null, " +
                "timespent long, " + "lastactivated long);"


        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(DATABASE_CREATE)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            Log.w(TAG, "Upgrading does not affect the database")
        }

    }


    private var db: SQLiteDatabase by Delegates.notNull()

    //---opens the database---
    public fun open(): DBAdapter {
        db = DBHelper.getWritableDatabase()!!
        return this
    }

    //---closes the database---
    public fun close() {
        DBHelper.close()
    }

    public fun destroy() {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE)
    }


    //---insert a title into the database---
    public fun insert(project: String, status: String, timespent: Long, lastactivated: Long): Long {
        val initialValues = ContentValues()
        initialValues.put(KEY_PROJECT, StringEscapeUtils.escapeJava(project))
        initialValues.put(KEY_STATUS, status)
        initialValues.put(KEY_TIMESPENT, timespent)
        initialValues.put(KEY_LASTACTIVATED, lastactivated)

        return db.insert(DATABASE_TABLE, null, initialValues)
    }

    //---deletes a particular title---
    public fun deleteById(rowId: Long): Boolean {
        return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0
    }

    public fun deleteByProject(project: String): Boolean {
        return db.delete(DATABASE_TABLE, KEY_PROJECT + "='" + StringEscapeUtils.escapeJava(project) + "'", null) > 0
    }


    //---retrieves all the data---
    public fun getAll(): Cursor? {
        val ars = arrayOf(KEY_ROWID, KEY_PROJECT, KEY_STATUS, KEY_TIMESPENT, KEY_LASTACTIVATED)
        val result : Cursor? = db.query(DATABASE_TABLE, ars, null, null, null, null, null)
        return result
    }

    public fun updateSpentTimeByProject(project: String, status: String, timespent: Long) {
        val args = ContentValues()
        args.put(KEY_STATUS, status)
        args.put(KEY_TIMESPENT, timespent)
        try {
            db.update(DATABASE_TABLE, args, KEY_PROJECT + "='" + StringEscapeUtils.escapeJava(project) + "'", null)

        } catch (e: Exception) {
            Toast.makeText(context, "Query to update timespent field failed " + e.message, Toast.LENGTH_LONG).show()
        }

    }
    public fun updateActivatedByProject(project: String, status: String, lastactivated: Long) {

        val args = ContentValues()
        args.put(KEY_STATUS, status)
        args.put(KEY_LASTACTIVATED, lastactivated)

        try {
            db.update(DATABASE_TABLE, args, KEY_PROJECT + "='" + StringEscapeUtils.escapeJava(project) + "'", null)
        } catch (e: Exception) {
            Toast.makeText(context, "Query to update lastactivated field failed " + e.message, Toast.LENGTH_LONG).show()
        }

    }
}