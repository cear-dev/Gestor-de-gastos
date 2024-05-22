package com.example.tdmpa_511_3p_ex

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast

class activity_diary : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary)

        val spinnerScheduledAppointments = findViewById<Spinner>(R.id.spinner_scheduledAppointments)
        val textViewName = findViewById<TextView>(R.id.textView_name)
        val textViewPhone = findViewById<TextView>(R.id.textView_phone)
        val textViewDate = findViewById<TextView>(R.id.textView_date)
        val textViewHour = findViewById<TextView>(R.id.textView_hour)
        val buttonCancelAppointment = findViewById<Button>(R.id.button_cancelAppointment)
        val buttonGoToModifyAppointmentLayout = findViewById<Button>(R.id.button_goToModifyAppointmentLayout)
        val dbHelper = DatabaseHelper(this)
        //val appointments = dbHelper.getAllAppointments()
        val appointments = dbHelper.getFutureAppointments()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, appointments)

        var actualId = 0

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerScheduledAppointments.adapter = adapter

        spinnerScheduledAppointments.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                val parts = selectedItem.split("-")
                if (parts.size == 2) {
                    val id = parts[0].trim().toIntOrNull()
                    if (id != null) {
                        //Toast.makeText(applicationContext, "ID obtenido: $id", Toast.LENGTH_SHORT).show()
                        actualId = id
                        val appointment = dbHelper.getAppointmentById(id)
                        textViewName.text = "Nombre: ${appointment?.name}"
                        textViewPhone.text = "Teléfono: ${appointment?.phone}"
                        textViewDate.text = "Fecha: ${appointment?.date}"
                        textViewHour.text = "Hora: ${appointment?.hour}"

                    } else {
                        Toast.makeText(applicationContext, "No se pudo obtener el ID", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(applicationContext, "La cadena no tiene el formato esperado.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Acción a realizar cuando no se selecciona ningún elemento
            }
        }

        buttonCancelAppointment.setOnClickListener {
            val appointmentModel = AppointmentModel(actualId, "", "", "", "")
            dbHelper.deleteAppointment(appointmentModel)
            finish()
        }

        buttonGoToModifyAppointmentLayout.setOnClickListener {
            val intent = Intent(this, activity_appointment::class.java)
            intent.putExtra("ModifyAppointmentFlag", true)
            intent.putExtra("IdToModify", actualId)
            startActivity(intent)
        }

    }
}