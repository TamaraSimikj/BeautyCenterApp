package app.beautycenter

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.beautycenter.model.Service
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*


class AppointmentActivity : AppCompatActivity() {

    private lateinit var tvUserName: TextView
    private lateinit var datePicker: DatePicker
    private lateinit var spinnerTimeSlots: Spinner
    private lateinit var spinnerServices: Spinner
    private lateinit var btnSubmit: Button
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var appointmentsRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment)

        // Initialize views
        tvUserName = findViewById(R.id.tvUserName)
        datePicker = findViewById(R.id.datePicker)
        spinnerTimeSlots = findViewById(R.id.spinnerTimeSlots)
        btnSubmit = findViewById(R.id.btnSubmit)
        spinnerServices = findViewById(R.id.spinnerServices)

        // Initialize Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance()

        // Initialize Firebase Realtime Database
        val database = FirebaseDatabase.getInstance()
        appointmentsRef = database.getReference("appointments")

        // Set up the user name
        val user = firebaseAuth.currentUser
        val userName = user?.displayName
        tvUserName.text = "Welcome, $userName"

        // Set up the DatePicker
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 1) // Set the minimum selectable date as tomorrow
        datePicker.minDate = calendar.timeInMillis

        // Set up the Time Slots Spinner
        val timeSlots = getTimeSlots()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, timeSlots)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTimeSlots.adapter = adapter

        // Set up Services Spinner
        val services = getServices()
        val serviceNames = services.map { it.name }
        val adapterServices = ArrayAdapter(this,android.R.layout.simple_spinner_item,serviceNames )
        adapterServices.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerServices.adapter = adapterServices

        // Set OnClickListener for the Submit button
        btnSubmit.setOnClickListener {
            val selectedDate = getSelectedDate()
            val selectedTimeSlot = spinnerTimeSlots.selectedItem.toString()

            // Perform the appointment booking based on the selected date, time slot, and service
            val selectedService = spinnerServices.selectedItem.toString()
            bookAppointment(userName, selectedService, selectedDate, selectedTimeSlot)
        }
    }

    private fun getTimeSlots(): List<String> {
        val timeSlots = mutableListOf<String>()
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 10)
        calendar.set(Calendar.MINUTE, 0)
        val dateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

        repeat(9) {
            timeSlots.add(dateFormat.format(calendar.time))
            calendar.add(Calendar.HOUR_OF_DAY, 1)
        }

        return timeSlots
    }

    private fun getSelectedDate(): String {
        val day = datePicker.dayOfMonth
        val month = datePicker.month + 1 // Months are zero-based, so add 1
        val year = datePicker.year

        // Format the date as desired
        return "$day/$month/$year"
    }

    private fun bookAppointment(
        userName: String?,
        selectedService: String,
        date: String,
        timeSlot: String
    ) {
        val appointmentKey = appointmentsRef.push().key

        if (appointmentKey != null) {
            val appointmentData = hashMapOf(
                "username" to (userName ?: ""),
                "service" to selectedService,
                "time" to "$date $timeSlot"
            )

            appointmentsRef.child(appointmentKey).setValue(appointmentData)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Appointment saved successfully
                        Toast.makeText(this, "Appointment booked successfully", Toast.LENGTH_SHORT).show()

                        // Redirect to the list of appointments for the current user
                        val intent = Intent(this, AppointmentListActivity::class.java)
                        startActivity(intent)
                        finish() // Optional: Finish the current activity to prevent going back to it
                    } else {
                        // Failed to save the appointment
                        Toast.makeText(this, "Failed to book appointment", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
    // Dummy data for services
    private fun getServices(): List<Service> {
        return listOf(
            Service("Haircut", "Get a stylish haircut by professional stylists", 25.0),
            Service("Nails", "Pamper yourself with a manicure and pedicure", 20.0),
            Service("Makeup", "Transform your look with professional makeup", 30.0),
            Service("Waxing", "Experience smooth skin with our waxing services", 15.0)
            // Add more services as needed
        )
    }
}
