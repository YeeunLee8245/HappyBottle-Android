package kr.co.yeeunlee.own.project1.mywriting

import android.os.Parcelable
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
data class Note(val name: String="", val text: String="", val time: Timestamp= Timestamp.now(), val check: Boolean=false, val type: Int=0, var post:Boolean=false){
     init {
         //AndroidThreeTen.init(this) 액티비티, 프래그먼트에서 해주기
     }

    companion object {
        fun getTime(uploadTime: Date): String {
            val nowTime = LocalDateTime.now()
            val newUploadT: LocalDateTime = Instant.ofEpochMilli(uploadTime.time)
                .atZone(ZoneId.systemDefault()).toLocalDateTime() // Date를 LocalDateTime으로 변환

            return if (nowTime.year == uploadTime.year) {
                //월/일 표시
                "$${newUploadT.monthValue}월 ${newUploadT.dayOfMonth}일"
            } else {
                //년/월/일 표시
                "${newUploadT.year}년 ${newUploadT.monthValue}월 ${newUploadT.dayOfMonth}일"
            }
        }

        fun getNote(snapshot: DocumentSnapshot): Note {
            return Note(
                name = snapshot["name"] as String,
                text = snapshot["text"] as String,
                time = snapshot["time"] as Timestamp,
                check = snapshot["check"] as Boolean,
                type = snapshot["type"] as Int,
                post = snapshot["post"] as Boolean
            )
        }

        fun getBottleInfo(persent: Int): String {
            return when (persent) { // MAX: 5
                in 0..1 -> "일상 속의 행복을 기록하여 행복 저금통에 넣어주세요."
                in 2..3 -> "행복이 점점 저금되고 있어요."
                else -> "행복 저금통 하나가 곧 완성될 것 같아요."
//                in 0..9 -> "일상 속의 행복을 기록하여 행복 저금통에 넣어주세요."
//                in 10..25 -> "행복이 점점 저금되고 있어요."
//                else -> "행복 저금통 하나가 곧 완성될 것 같아요."
            }
        }
    }
}

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

