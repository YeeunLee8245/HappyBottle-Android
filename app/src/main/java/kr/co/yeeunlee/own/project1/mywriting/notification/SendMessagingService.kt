package kr.co.yeeunlee.own.project1.mywriting.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.graphics.drawable.IconCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kr.co.yeeunlee.own.project1.mywriting.MainActivity
import kr.co.yeeunlee.own.project1.mywriting.R
import kr.co.yeeunlee.own.project1.mywriting.ui.SplashActivity

class SendMessagingService: FirebaseMessagingService()  {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // 다른 기기에서 서버로 보냈을 때
        val title = remoteMessage.data["title"]!!
        val name = remoteMessage.data["name"]!!
        val message = "쪽지가 도착했어요!"
        val profileImg = remoteMessage.data["profileImg"]!!.toInt()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) { // Android 8(Oreo)
            sendMessageNotification(title, name, message, profileImg)
        }else{
            sendNotification(remoteMessage.notification?.title,
                remoteMessage.notification?.body!!)
        }
        // 알림 비허용: user 토큰을 없애자.
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun sendMessageNotification(title: String, name: String, message: String, profileImg: Int){
        val profileImgLi = arrayListOf(
            R.drawable.blue, R.drawable.green, R.drawable.mint,
            R.drawable.orange, R.drawable.pink, R.drawable.purple, R.drawable.sky, R.drawable.yellow
        )
        // 팝업 클릭시 이동할 액티비티
        val intentSend = Intent(this, SplashActivity::class.java)
        intentSend.putExtra("service", "service")
        intentSend.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // 액티비티 중복생성 방지
        val pendingIntent = PendingIntent.getActivity(this, 0, intentSend,
            PendingIntent.FLAG_IMMUTABLE) // 일회성

        // messageStyle 설정
        val user: androidx.core.app.Person = Person.Builder()
            .setName(name)
            .setIcon(IconCompat.createWithResource(this, profileImgLi[profileImg]))
            .build()
        val compatMsg = NotificationCompat.MessagingStyle.Message(
            message,
            System.currentTimeMillis(),
            user
        )
        val messageStyle = NotificationCompat.MessagingStyle(user).addMessage(compatMsg)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)  // 소리
        val notificationBuilder = NotificationCompat.Builder(this,"service")
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(messageStyle)
            .setSmallIcon(R.drawable.icon_popup)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // Android 8(Oreo) 버전 예외처리
            val channel = NotificationChannel("service","알림 메시지",
                NotificationManager.IMPORTANCE_LOW) // 소리 없앰
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, notificationBuilder.build())
    }

    private fun sendNotification(title: String?, message: String){
        // 팝업 클릭시 이동할 액티비티
        val intentSend = Intent(this, MainActivity::class.java)
        intentSend.putExtra("service", "service")
        intentSend.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // 액티비티 중복생성 방지
        val pendingIntent = PendingIntent.getActivity(this, 0, intentSend,
        PendingIntent.FLAG_IMMUTABLE)
        val notificationBuilder = NotificationCompat.Builder(this,"service")
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.icon_popup)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // Android 8(Oreo) 버전 예외처리
            val channel = NotificationChannel("service","알림 메시지",
                NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, notificationBuilder.build())

    }

}
