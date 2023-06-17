package app.beautycenter

import android.content.Intent
import android.os.Bundle
import android.widget.*
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
        //navbar
        val btnHome: Button = findViewById(R.id.btnHome)
        val btnAppointments: Button = findViewById(R.id.btnAppointments)

        btnHome.setOnClickListener {
            // Handle home button click
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        btnAppointments.setOnClickListener {
            val intent = Intent(this, AppointmentListActivity::class.java)
            startActivity(intent)
        }

        // Set up the dropdown menu
        val btnDropdown: ImageButton = findViewById(R.id.btnDropdown)
        val popupMenu = PopupMenu(this, btnDropdown)
        popupMenu.menuInflater.inflate(R.menu.dropdown_menu, popupMenu.menu)

        // Set the username in the dropdown menu
        val user = firebaseAuth.currentUser
        val userName = user?.displayName
        popupMenu.menu.findItem(R.id.menu_username)?.title = userName

        // Set click listener for the dropdown menu items
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_username -> {
                    // Handle username option
                    Toast.makeText(this, "Current Logged user: $userName", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.menu_logout -> {
                    // Handle logout option
                    firebaseAuth.signOut()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }

        // Set click listener for the dropdown button
        btnDropdown.setOnClickListener {
            // Show the dropdown menu
            popupMenu.show()
        }
        //end of navbar

        tvUserName.text ="Appointments from,  "+ user?.displayName

        // Define the appointmentList as an empty ArrayList<String>
        val appointmentList = ArrayList<String>()

        val database = FirebaseDatabase.getInstance()
        val appointmentsRef = database.getReference("appointments")

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
