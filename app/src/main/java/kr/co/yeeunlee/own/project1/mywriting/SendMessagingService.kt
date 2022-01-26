package kr.co.yeeunlee.own.project1.mywriting

import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class SendMessagingService: FirebaseMessagingService()  {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if(remoteMessage.data.isEmpty()){
            sendNotification(remoteMessage.notification?.title!!,
            remoteMessage.notification?.body!!)
        }else{Log.d("메시지 서비스","수신된 메시지 데이터가 텅 빔")}
    }

    private fun sendNotification(title: String, body: String){
        val intent = Intent(this, MainActivity::class.java)

    }
}