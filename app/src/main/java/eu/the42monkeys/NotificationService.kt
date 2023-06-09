package eu.the42monkeys

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.Result
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import eu.the42monkeys.model.ResolutionViewModel
import java.nio.charset.Charset
import kotlin.random.Random

class NotificationService : FirebaseMessagingService() {

    private val DEFAULT_CHANNEL_ID = "default_channel"

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i("NotificationService ", "Refreshed token :: $token")
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {
        SharedPrefsHelper.saveNotificationToken(applicationContext, token)
        val requestBody = "{\"token\":\"$token\",\"platform\":\"android\"}"
        val jwtToken = SharedPrefsHelper.getJwtToken(applicationContext)

        if (jwtToken != null) {
            Fuel.post("${BuildConfig.BACKEND_URL}/devices.json")
                .header("Authorization", jwtToken)
                .header("Content-Type" to "application/json")
                .body(requestBody, Charset.forName("UTF-8"))
                .response { _, _, result ->
                    when (result) {
                        is Result.Success -> {
                        }

                        is Result.Failure -> {
                            val exception = result.getException()
                            Toast.makeText(
                                this,
                                "Send token to server error $exception",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    }
                }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.i("NotificationService ", "Message :: $message")
        val temper = message.data["temper"]!!
        val body = message.data["message"]!!

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("temper", temper)
        intent.putExtra("body", body)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt(3000)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupChannels(notificationManager)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        var largeIcon = BitmapFactory.decodeResource(
            resources,
            R.drawable.daruma_red
        )
        var smallIcon = R.drawable.daruma_red
        when (ResolutionViewModel.TemperType.valueOf(temper.uppercase())) {
            ResolutionViewModel.TemperType.AUTHORITARIAN -> {
                largeIcon = BitmapFactory.decodeResource(
                    resources,
                    R.drawable.daruma_black
                )
                smallIcon = R.drawable.daruma_black
            }
            ResolutionViewModel.TemperType.SARCASTIC -> {
                largeIcon = BitmapFactory.decodeResource(
                    resources,
                    R.drawable.daruma_green
                )
                smallIcon = R.drawable.daruma_green
            }
            ResolutionViewModel.TemperType.MOTIVATIONAL -> {
                largeIcon = BitmapFactory.decodeResource(
                    resources,
                    R.drawable.daruma_gold
                )
                smallIcon = R.drawable.daruma_gold
            }
            else -> {}
        }

        val notificationBuilder = NotificationCompat.Builder(this, DEFAULT_CHANNEL_ID)
            .setSmallIcon(smallIcon)
            .setLargeIcon(largeIcon)
            .setContentTitle(message.data["title"])
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        //Set notification color to match your app color template
//        notificationBuilder.color = resources.getColor(R.color.background_dark)
        notificationManager.notify(notificationID, notificationBuilder.build())
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun setupChannels(notificationManager: NotificationManager?) {
        val defaultChannelName = "New notification"
        val defaultChannelDescription = "daruma reminder"

        val defaultChannel: NotificationChannel =
            NotificationChannel(DEFAULT_CHANNEL_ID, defaultChannelName, NotificationManager.IMPORTANCE_HIGH)
        defaultChannel.description = defaultChannelDescription
        defaultChannel.enableLights(true)
        defaultChannel.lightColor = Color.RED
        defaultChannel.enableVibration(true)
        notificationManager?.createNotificationChannel(defaultChannel)
    }
}