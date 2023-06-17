package app.beautycenter

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import android.widget.TextView
import app.beautycenter.adapters.ServicesAdapter
import app.beautycenter.model.Service
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {
    private lateinit var btnMakeAppointment: Button
    private lateinit var recyclerViewServices: RecyclerView
    private lateinit var tvUserName: TextView
    private lateinit var btnLogout: Button

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()

        // Initialize views
        btnMakeAppointment = findViewById(R.id.btnMakeAppointment)
        recyclerViewServices = findViewById(R.id.recyclerViewServices)
        tvUserName = findViewById(R.id.tvUserName)
        btnLogout = findViewById(R.id.btnLogout)

        // Set OnClickListener for the make appointment button
        btnMakeAppointment.setOnClickListener {
            //startAppointmentActivity()
          //  val services = getServices()
            val intent = Intent(this, AppointmentActivity::class.java)
        //    intent.putExtra("services", services.toTypedArray())
            startActivity(intent)
        }

        // Set OnClickListener for the logout button
        btnLogout.setOnClickListener {
            // Sign out the user
            firebaseAuth.signOut()
            // Redirect to the login activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Optional: Finish the current activity if you don't want the user to navigate back to it
        }

        // Set up the RecyclerView
        recyclerViewServices.layoutManager = LinearLayoutManager(this)
        recyclerViewServices.adapter = ServicesAdapter(getServices())

        // Set the user name
        val currentUser = firebaseAuth.currentUser
        tvUserName.text = currentUser?.displayName
    }

    private fun startAppointmentActivity() {
        val services = getServices()
        val intent = Intent(this, AppointmentActivity::class.java)
        intent.putExtra("services", services.toTypedArray())
        startActivity(intent)
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

