package com.example.tdmpa_511_3p_ex

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlinx.coroutines.currentCoroutineContext
import java.text.SimpleDateFormat
import java.util.Date

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "Diary"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "AppointmentTable"
        private const val KEY_ID = "id"
        private const val KEY_NAME = "name"
        private const val KEY_PHONE = "phone"
        private const val KEY_DATE = "date"
        private const val KEY_HOUR = "hour"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE $TABLE_NAME ($KEY_ID INTEGER PRIMARY KEY,$KEY_NAME TEXT, $KEY_PHONE TEXT, $KEY_DATE TEXT, $KEY_HOUR TEXT );")
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addAppointment(appointmentModel: AppointmentModel){
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_NAME, appointmentModel.name)
        values.put(KEY_PHONE, appointmentModel.phone)
        values.put(KEY_DATE, appointmentModel.date)
        values.put(KEY_HOUR, appointmentModel.hour)
        db.insert(TABLE_NAME, null,values)
        db.close()
    }

    fun updateAppointment(appointmentModel: AppointmentModel){
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_NAME, appointmentModel.name)
        values.put(KEY_PHONE, appointmentModel.phone)
        values.put(KEY_DATE, appointmentModel.date)
        values.put(KEY_HOUR, appointmentModel.hour)
        db.update(TABLE_NAME, values, "$KEY_ID=?", arrayOf(appointmentModel.id.toString()))
        db.close()
    }

    fun deleteAppointment(appointmentModel: AppointmentModel){
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "$KEY_ID=?", arrayOf(appointmentModel.id.toString()))
        db.close()
    }

    @SuppressLint("Range")
    fun getAllAppointments(): List<AppointmentModel> {
        val appointmentList = mutableListOf<AppointmentModel>()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"

        val cursor = db.rawQuery(query, null)

        cursor?.use {
            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                val name = cursor.getString(cursor.getColumnIndex(KEY_NAME))
                val phone = cursor.getString(cursor.getColumnIndex(KEY_PHONE))
                val date = cursor.getString(cursor.getColumnIndex(KEY_DATE))
                val hour = cursor.getString(cursor.getColumnIndex(KEY_HOUR))

                val appointment = AppointmentModel(id, name, phone, date, hour)
                appointmentList.add(appointment)
            }
        }

        cursor?.close()
        db.close()

        return appointmentList
    }

    @SuppressLint("Range")
    fun getFutureAppointments(): List<AppointmentModel> {
        val appointmentList = mutableListOf<AppointmentModel>()
        val db = this.readableDatabase
        val currentDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:00").format(Date())

        val query = "SELECT * FROM $TABLE_NAME WHERE datetime($KEY_DATE || ' ' || $KEY_HOUR || ':00') >= datetime(?, 'localtime')"

        val cursor = db.rawQuery(query, arrayOf(currentDateTime))

        cursor?.use {
            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                val name = cursor.getString(cursor.getColumnIndex(KEY_NAME))
                val phone = cursor.getString(cursor.getColumnIndex(KEY_PHONE))
                val date = cursor.getString(cursor.getColumnIndex(KEY_DATE))
                val hour = cursor.getString(cursor.getColumnIndex(KEY_HOUR))

                val appointment = AppointmentModel(id, name, phone, date, hour)
                appointmentList.add(appointment)
            }
        }

        cursor?.close()
        db.close()

        return appointmentList
    }

    @SuppressLint("Range")
    fun getAppointmentById(appointmentId: Int): AppointmentModel? {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $KEY_ID = ?"
        val selectionArgs = arrayOf(appointmentId.toString())

        val cursor = db.rawQuery(query, selectionArgs)

        var appointment: AppointmentModel? = null

        cursor?.use {
            if (cursor.moveToFirst()) {
                val id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                val name = cursor.getString(cursor.getColumnIndex(KEY_NAME))
                val phone = cursor.getString(cursor.getColumnIndex(KEY_PHONE))
                val date = cursor.getString(cursor.getColumnIndex(KEY_DATE))
                val hour = cursor.getString(cursor.getColumnIndex(KEY_HOUR))

                appointment = AppointmentModel(id, name, phone, date, hour)
            }
        }

        cursor?.close()
        db.close()

        return appointment
    }

    @SuppressLint("Range")
    fun getAppointmentsByDate(date: String): List<AppointmentModel> {
        val appointmentList = mutableListOf<AppointmentModel>()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $KEY_DATE = ?"

        val cursor = db.rawQuery(query, arrayOf(date))

        cursor?.use {
            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                val name = cursor.getString(cursor.getColumnIndex(KEY_NAME))
                val phone = cursor.getString(cursor.getColumnIndex(KEY_PHONE))
                val appointmentDate = cursor.getString(cursor.getColumnIndex(KEY_DATE))
                val hour = cursor.getString(cursor.getColumnIndex(KEY_HOUR))

                val appointment = AppointmentModel(id, name, phone, appointmentDate, hour)
                appointmentList.add(appointment)
            }
        }

        cursor?.close()
        db.close()

        return appointmentList
    }


}

data class AppointmentModel(val id:Int, val name: String, val phone: String, val date: String, val hour: String){
    override fun toString(): String {
        return "$id - $name"
    }
}
