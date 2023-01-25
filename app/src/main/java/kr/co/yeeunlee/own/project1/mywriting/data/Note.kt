package kr.co.yeeunlee.own.project1.mywriting

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.io.IOException
import java.util.*

// 유저 등록
@Parcelize
data class User(var name:String, val email:String, val vaildPassWord:Boolean
    , var password:String?, val numNote: Int, val numPost: Int, var token:String
    , val profileImg:Int , var statusMsg: String = "되새기고 싶은 한 마디") : Parcelable

// 글 등록
@Keep
data class Note(val name: String="", val text: String="", val time: Timestamp= Timestamp.now()
                , val check: Boolean=false, val type: Int=0, var post:Boolean=false, val profileImg: Int=-1)

// 앱에서의 저금통 데이터
data class BottleList(var first: Int? = null, var second: Int? = null, var third: Int? = null)

// 푸시알림 전송 데이터
data class MessageDTO(var fromName: String? = null, var toName: String? = null,
var text: String? = null, var timestamp: Long? = null)

data class NotificationBody(val to: String, val data: NotificationData){
    data class NotificationData(val title: String, val name: String, val message: String, val profileImg: Int)
}


interface FcmInterface{ // 푸시 메시지를 서버로 보냄
    @POST("fcm/send")
    suspend fun sendNotification(   // 서버 통신은 비동기 처리
        @Body notification: NotificationBody
    ) : retrofit2.Response<ResponseBody>
}

// 레트로핏 생성: 서버와 HTTP 통신을 해서 받은 데이터 앱에서 보여줌
object RetrofitInstance{
    class AppInterceptor: Interceptor{  // 인증토큰 필요하기 때문에 헤더 추가
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response = with(chain) {
            val newRequest = request().newBuilder()
                .addHeader("Authorization", "key=${MainActivity.SERVER_KEY}")
                .addHeader("Content-Type",MainActivity.CONTENT_TYPE)
                .build()
            proceed(newRequest)
        }
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(MainActivity.FCM_URL)
            .client(provideOkHttpClient(AppInterceptor()))
            .addConverterFactory(GsonConverterFactory.create())  // JSON 타입 결과를 객체로 매핑
            .build()
    }

    val api: FcmInterface by lazy{
        retrofit.create(FcmInterface::class.java)
    }

    // 클라이언트
    private fun provideOkHttpClient(interceptor: AppInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .run {
            addInterceptor(interceptor) // httpResponse 받아옴
            build()
        }
}

// 네트워크 연결 상태 확인
class NetworkManager {

    companion object {
        fun checkNetworkState(context: Context): Boolean {
            val connectivityManager: ConnectivityManager =
                context.getSystemService(ConnectivityManager::class.java)
            val network: Network = connectivityManager.activeNetwork ?: return false
            val actNetwork: NetworkCapabilities =
                connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                else -> false
            }
        }
    }

}

