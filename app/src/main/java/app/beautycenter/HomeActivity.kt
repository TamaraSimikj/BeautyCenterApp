package app.beautycenter

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.beautycenter.adapters.ServicesAdapter
import app.beautycenter.model.Service
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {
    private lateinit var btnMakeAppointment: Button
    private lateinit var recyclerViewServices: RecyclerView
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        firebaseAuth = FirebaseAuth.getInstance()

        //navbar
        val btnHome: Button = findViewById(R.id.btnHome)
        val btnAppointments: Button = findViewById(R.id.btnAppointments)

        btnHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        btnAppointments.setOnClickListener {
            val intent = Intent(this, AppointmentListActivity::class.java)
            startActivity(intent)
        }

        val btnDropdown: ImageButton = findViewById(R.id.btnDropdown)
        val popupMenu = PopupMenu(this, btnDropdown)
        popupMenu.menuInflater.inflate(R.menu.dropdown_menu, popupMenu.menu)

        val user = firebaseAuth.currentUser
        val userName = user?.displayName
        popupMenu.menu.findItem(R.id.menu_username)?.title = userName

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_username -> {
                    // Handle username option
                    Toast.makeText(this, "Current Logged user: $userName", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.menu_logout -> {
                    firebaseAuth.signOut()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }

        btnDropdown.setOnClickListener {
            popupMenu.show()
        }
        //end of navbar

        btnMakeAppointment = findViewById(R.id.btnMakeAppointment)
        recyclerViewServices = findViewById(R.id.recyclerViewServices)

        btnMakeAppointment.setOnClickListener {
            val intent = Intent(this, AppointmentActivity::class.java)
            startActivity(intent)
        }

        recyclerViewServices.layoutManager = LinearLayoutManager(this)
        recyclerViewServices.adapter = ServicesAdapter(getServices())

    }


    private fun getServices(): List<Service> {
        return listOf(
            Service("Haircut", "Get a stylish haircut by professional stylists", 25.0),
            Service("Nails", "Pamper yourself with a manicure and pedicure", 20.0),
            Service("Makeup", "Transform your look with professional makeup", 30.0),
            Service("Waxing", "Experience smooth skin with our waxing services", 15.0)
        )
    }

    fun openMapActivity() {
        val intent = Intent(this, MapActivity::class.java)
        startActivity(intent)
    }

}

