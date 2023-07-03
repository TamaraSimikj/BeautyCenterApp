package app.beautycenter

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class NotificationWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    private val channelId = "appointment_reminder_channel"
    private val notificationId = 1

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.Default) {
            val appointmentDate = inputData.getString("appointment_date")
            val appointmentTime = inputData.getString("time")
            val service = inputData.getString("service")
            val formattedDate = formatDate(appointmentDate)
            if (service != null && appointmentTime != null) {
                    showNotification(formattedDate,appointmentTime,service)

            }
            Result.success()
        }
    }

    private fun formatDate(dateString: String?): String {
        val sdfInput = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val sdfOutput = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        val date = sdfInput.parse(dateString)
        return sdfOutput.format(date)
    }

    private fun showNotification(appointmentDate: String,time:String,service: String) {
        createNotificationChannel()

        val notificationBuilder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Appointment Reminder")
            .setContentText("You have an appointment on $appointmentDate")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val intent = Intent(applicationContext, AppointmentDetailsActivity::class.java).apply {
            putExtra("appointment_date", appointmentDate)
            putExtra("time",time)
            putExtra("service",service)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        notificationBuilder.setContentIntent(pendingIntent)

        with(NotificationManagerCompat.from(applicationContext)) {
            notify(notificationId, notificationBuilder.build())
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Appointment Reminder"
            val descriptionText = "Channel for appointment reminders"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }

            val notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
