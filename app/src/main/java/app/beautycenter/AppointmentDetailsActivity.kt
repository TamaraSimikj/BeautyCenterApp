package app.beautycenter

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.core.app.NotificationCompat
import java.text.SimpleDateFormat
import java.util.*
class AppointmentDetailsActivity : AppCompatActivity() {

    private lateinit var reminderHeader: TextView
    private lateinit var tvAppointmentDate: TextView
    private lateinit var tvAppointmentTime: TextView
    private lateinit var tvServiceName: TextView
    private lateinit var btnRemindMeLater: Button
    private lateinit var btnClose: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment_details)

        val appointmentDate = intent.getStringExtra("appointment_date")
        val appointmentTime = intent.getStringExtra("time")
        val serviceName = intent.getStringExtra("service")

        reminderHeader = findViewById(R.id.Reminder)
        tvAppointmentDate = findViewById(R.id.tvAppointmentDate)
        tvAppointmentTime = findViewById(R.id.tvAppointmentTime)
        tvServiceName = findViewById(R.id.tvServiceName)
        btnRemindMeLater = findViewById(R.id.btnRemindMeLater)
        btnClose = findViewById(R.id.btnClose)

        reminderHeader.text = "Reminder for upcoming appointment"
        tvAppointmentDate.text = "Appointment Date: $appointmentDate"
        tvAppointmentTime.text = "Time: $appointmentTime"
        tvServiceName.text = "Service: $serviceName"

        btnRemindMeLater.setOnClickListener {
            val channelId = "appointment_channel"
            val channelName = "Appointment Channel"

            //notification channel
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }

            // Build the notification
            val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setContentTitle("Appointment Reminder")
                .setContentText("Your appointment is in 1 hour.")
                .setSmallIcon(R.drawable.ic_notification)
                .setAutoCancel(true)

            // Schedule the notification
            val notificationIntent = Intent(this, NotificationReceiver::class.java)
            notificationIntent.putExtra("notification_id", 1)
            notificationIntent.putExtra("notification", notificationBuilder.build())
            val pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val appointmentTimeMillis = convertTimeToMillis(appointmentTime)
            val reminderTimeMillis = appointmentTimeMillis - (60 * 60 * 1000) // 1 hour before appointment 75900000
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                reminderTimeMillis,
                pendingIntent
            )

            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnClose.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun convertTimeToMillis(time: String?): Long {
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = dateFormat.parse(time)
        return date?.time ?: 0
    }
}
