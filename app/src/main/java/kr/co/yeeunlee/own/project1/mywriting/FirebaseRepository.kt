package kr.co.yeeunlee.own.project1.mywriting

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.messaging.FirebaseMessaging
import okhttp3.ResponseBody
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import retrofit2.Response

class FirebaseRepository {
    private val db = LoginStartActivity.db
    private val userEmail = LoginStartActivity.mAuth.currentUser?.email.toString()
    private var userName:String? = null

    fun getUserSnapshot(_userSnapshot:MutableLiveData<DocumentSnapshot>) {
        db.collection("user").document(userEmail)
           .get().addOnSuccessListener {
                _userSnapshot.value = it
           }
    }

    suspend fun setNoteAdd(textEditNote: String): DocumentSnapshot {
        val dcmRef= LoginStartActivity.db.collection("user").document(userEmail)
        var resultRef:DocumentSnapshot? = null

        coroutineScope {
            dcmRef.get().addOnSuccessListener {
                dcmRef.collection("note").document("${(it.get("numNote") as Long) +1}")
                    .set(Note(it.get("name") as String, textEditNote, Timestamp.now(),true))
            }
        }.await()

        coroutineScope {
            dcmRef.update("numNote", FieldValue.increment(1))
        }.await()

        coroutineScope {
            db.collection("user").document(userEmail).get().addOnSuccessListener {
                resultRef = it
            }
        }.await()

        return resultRef!!
    }


    fun getStorageBottle(_stgBtSnapLi:MutableLiveData<ArrayList<BottleList>>
                         , __stgBtSnapLi:ArrayList<BottleList>){
        __stgBtSnapLi.clear()
        db.collection("user").document(userEmail)
            .get().addOnSuccessListener {
                val numBottle: Int = it.get("numNote").toString().toInt() / 5
                Log.d("보틀 수", numBottle.toString())
                for ( i in numBottle downTo (1) step(3)){
                    Log.d("보틀 수1", i.toString())
                    if ((i - 2) >= 1){
                        __stgBtSnapLi.add(BottleList(i*5, (i-1)*5, (i-2)*5))
                    }
                    else if(i == 2){
                        __stgBtSnapLi.add(BottleList(i*5, (i-1)*5, null))
                    }
                    else{
                        __stgBtSnapLi.add(BottleList(i*5, null, null))
                    }
                }
                //Log.d("보틀 수2", __stgBtSnapLi[0].toString())
                _stgBtSnapLi.value = __stgBtSnapLi
            }
    }

    suspend fun getOpnNoteSnapshot(index: Int) : DocumentSnapshot{
        var resultRef:DocumentSnapshot? = null
        coroutineScope {
            db.collection("user").document(userEmail)
                .collection("note").document(index.toString())
                .get().addOnSuccessListener { resultRef = it }
        }.await()
        return resultRef!!
    }

    suspend fun setToken(): String{
        var token:String = ""
        coroutineScope {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.d("서비스 토큰", "토큰 등록에 실패함")
                    return@addOnCompleteListener
                }
                token = task.result
                db.collection("user").document(userEmail)
                    .addSnapshotListener{document, _->
                        userName = document!!.get("name").toString()    // 사용자 이름 초기화
                        Log.d("서비스 토큰 변경", userName+token)
                        if (document == null) return@addSnapshotListener

                        if (document["token"].toString() != token){
                            Log.d("서비스 토큰 변경", "토큰 변경")
                            db.collection("user").document(userEmail).update("token",token)
                        }else
                            Log.d("서비스 토큰 변경X", "토큰 변경X")

                    }
            }
        }.await()
        return token
    }

    suspend fun setSendNoteAdd(receiver: String, textEditNote: String, vaild:MutableLiveData<Boolean>){
        var currentDcmRef:DocumentReference = db.collection("user").document(userEmail)
        var dcmRef:DocumentReference? = null
        var userName:String = ""

        coroutineScope { db.collection("user").document(userEmail).get()
            .addOnSuccessListener { userName = it.get("name").toString() }
        }.await()

        coroutineScope { db.collection("user").whereEqualTo("name",receiver).get()
            .addOnSuccessListener { documents ->
                for (document in documents){
                    dcmRef = document.reference
                }
            }
        }.await()

        coroutineScope {
            dcmRef!!.get().addOnSuccessListener {
                dcmRef!!.collection("postbox").document("${(it.get("numPost") as Long) +1}")
                    .set(Note(userName, textEditNote, Timestamp.now(),false))
            }
        }.await()

        coroutineScope {
            dcmRef!!.update("numPost", FieldValue.increment(1))
        }.await()

        vaild.value = true
    }

    suspend fun sendNotification(myResponce: MutableLiveData<Response<ResponseBody>>,notification: NotificationBody){
        myResponce.value = RetrofitInstance.api.sendNotification(notification)
    }

    suspend fun getNewCommentSnapshot(_commentSnapshot:MutableLiveData<DocumentSnapshot>){

    }
}