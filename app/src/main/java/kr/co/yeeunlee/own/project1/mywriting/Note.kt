package kr.co.yeeunlee.own.project1.mywriting

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import java.util.*

// 유저 등록
@Parcelize
data class User(var name:String, var email:String, var vaildPassWord:Boolean, var password:String?, val numNote: Int) : Parcelable

// 글 등록
data class Note(val name: String, val text: String, val time: Timestamp){
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
                time = snapshot["time"] as Timestamp
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
