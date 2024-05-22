package com.example.tdmpa_511_3p_ex

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import java.util.Calendar

class activity_appointment : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment)
        
        val editTextName = findViewById<EditText>(R.id.editText_name)
        val editTextPhone = findViewById<EditText>(R.id.editText_phone)
        val buttonPickDate = findViewById<Button>(R.id.button_pickDate)
        val buttonPickTime = findViewById<Button>(R.id.button_pickTime)
        val buttonConfirmAppointment = findViewById<Button>(R.id.button_confirmAppointment)
        val ModifyAppointmentFlag = intent.getBooleanExtra("ModifyAppointmentFlag", false)
        val IdToModify = intent.getIntExtra("IdToModify", 0)
        val dbHelper = DatabaseHelper(this)

        var datePicked = ""
        var timePicked = ""

        fun isNotEmptyOrNull(): Boolean {
            if (editTextName.text.isNullOrEmpty() || editTextPhone.text.isNullOrEmpty()){
                Toast.makeText(this@activity_appointment, "Por favor complete los campos vacíos.", Toast.LENGTH_SHORT).show()
                return false;
            }

            if (datePicked == "" || datePicked == null){
                Toast.makeText(this@activity_appointment, "Por favor seleccione una fecha", Toast.LENGTH_SHORT).show()
                return false;
            }

            if (timePicked == "" || timePicked == null){
                Toast.makeText(this@activity_appointment, "Por favor seleccione una hora", Toast.LENGTH_SHORT).show()
                return false;
            }

            return true;
        }

        fun showDatePickerDialog() {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { _, selectedYear, monthOfYear, dayOfMonth ->
                    val selectedDate = "${selectedYear}-${String.format("%02d", monthOfYear + 1)}-${String.format("%02d", dayOfMonth)}"
                    buttonPickDate?.text = selectedDate
                    datePicked = selectedDate
                },
                year,
                month,
                day
            )

            datePickerDialog.show()
        }

        fun showTimePickerDialog() {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                this,
                TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
                    val selectedTime = "${String.format("%02d", selectedHour)}:${String.format("%02d", selectedMinute)}"
                    buttonPickTime?.text = selectedTime
                    timePicked = selectedTime
                },
                hour,
                minute,
                true
            )

            timePickerDialog.show()
        }

        fun calculateEndTime(startTime: String): String {
            val timeParts = startTime.split(":")
            val hour = timeParts[0].toInt()
            val minute = timeParts[1].toInt()

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            calendar.add(Calendar.HOUR_OF_DAY, 1)

            val endTimeHour = calendar.get(Calendar.HOUR_OF_DAY)
            val endTimeMinute = calendar.get(Calendar.MINUTE)

            return String.format("%02d:%02d", endTimeHour, endTimeMinute)
        }

        fun isTimeSlotAvailable(date: String, hour: String): Boolean {
            val existingAppointments = dbHelper.getAppointmentsByDate(date)
            val selectedStartTime = hour
            val selectedEndTime = calculateEndTime(hour)

            for (appointment in existingAppointments) {
                val appointmentStartTime = appointment.hour
                val appointmentEndTime = calculateEndTime(appointment.hour)

                if (selectedStartTime < appointmentEndTime && selectedEndTime > appointmentStartTime) {
                    return false
                }
            }

            return true
        }

        if (ModifyAppointmentFlag){
            val appointment = dbHelper.getAppointmentById(IdToModify)
            editTextName.setText(appointment?.name)
            editTextPhone.setText(appointment?.phone)
            buttonPickDate.text = appointment?.date
            buttonPickTime.text = appointment?.hour
            datePicked = appointment!!.date
            timePicked = appointment!!.hour
        }

        buttonPickDate.setOnClickListener {
            showDatePickerDialog()
        }

        buttonPickTime.setOnClickListener {
            showTimePickerDialog()
        }

        buttonConfirmAppointment.setOnClickListener {
            if (isNotEmptyOrNull()) {
                val name = editTextName.text.toString()
                val phone = editTextPhone.text.toString()
                val date = datePicked
                val hour = timePicked

                if (!isTimeSlotAvailable(date, hour)) {
                    Toast.makeText(this@activity_appointment, "El horario ya está ocupado.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (ModifyAppointmentFlag) {
                    val appointment = AppointmentModel(IdToModify, name, phone, date, hour)
                    dbHelper.updateAppointment(appointment)
                    Toast.makeText(this@activity_appointment, "Cita modificada correctamente.", Toast.LENGTH_SHORT).show()
                } else {
                    val appointment = AppointmentModel(0, name, phone, date, hour)
                    dbHelper.addAppointment(appointment)
                    Toast.makeText(this@activity_appointment, "Cita agendada correctamente.", Toast.LENGTH_SHORT).show()
                }

                finish()
            }
        }
        
    }
}