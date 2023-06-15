package app.beautycenter
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import app.beautycenter.adapters.ServicesAdapter
import app.beautycenter.model.Service

class HomeActivity : AppCompatActivity() {
    private lateinit var btnMakeAppointment: Button
    private lateinit var recyclerViewServices: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize views
        btnMakeAppointment = findViewById(R.id.btnMakeAppointment)
        recyclerViewServices = findViewById(R.id.recyclerViewServices)

        // Set OnClickListener for the make appointment button
        btnMakeAppointment.setOnClickListener {
            // Open the appointment activity
            val intent = Intent(this, AppointmentActivity::class.java)
            startActivity(intent)
        }

        // Set up the RecyclerView
        recyclerViewServices.layoutManager = LinearLayoutManager(this)
        recyclerViewServices.adapter = ServicesAdapter(getServices())
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

