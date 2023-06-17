package app.beautycenter

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AppointmentListActivity : AppCompatActivity() {

    private lateinit var tvUserName: TextView
    private lateinit var listAppointments: ListView
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var appointmentsRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointmentlist)

        // Initialize views
        tvUserName = findViewById(R.id.tvUserName)
        listAppointments = findViewById(R.id.listAppointments)

        // Initialize Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance()

        // Define the appointmentList as an empty ArrayList<String>
        val appointmentList = ArrayList<String>()

        val database = FirebaseDatabase.getInstance()
        val appointmentsRef = database.getReference("appointments")
        val user = firebaseAuth.currentUser
        val userName = user?.displayName

        appointmentsRef.orderByChild("username").equalTo(userName).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Iterate through the dataSnapshot to retrieve each appointment
                for (snapshot in dataSnapshot.children) {
                    val appointmentData = snapshot.value as Map<*, *>
                    val service = appointmentData["service"] as String
                    val dateTime = appointmentData["time"] as String

                    // Create a string representation of the appointment details
                    val appointmentDetails = "$service - $dateTime"

                    // Add the appointment details to the appointmentList
                    appointmentList.add(appointmentDetails)
                }

                // Create an ArrayAdapter with the appointmentList and set it to the ListView
                val adapter = ArrayAdapter(
                    this@AppointmentListActivity,
                    android.R.layout.simple_list_item_1,
                    appointmentList
                )
                listAppointments.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors that occur during the database operation
                Toast.makeText(this@AppointmentListActivity, "Failed to retrieve appointments", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
