package kr.co.yeeunlee.own.project1.mywriting

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.*
import com.google.firebase.messaging.FirebaseMessaging
import okhttp3.ResponseBody
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import retrofit2.Response

class FirebaseRepository {
    private val db = LoginStartActivity.db
    private val userEmail = LoginStartActivity.mAuth.currentUser?.email.toString()
    private var userName:String? = null

    suspend fun getUserNameSnapshot():String{
        var name:String? = null
        coroutineScope {
            db.collection("user").document(userEmail)
                .get().addOnSuccessListener {
                    name = it.get("name").toString()
                }
        }.await()
        return name.toString()
    }

    fun getUserSnapshot(_userSnapshot:MutableLiveData<DocumentSnapshot>) {
        db.collection("user").document(userEmail)
           .get().addOnSuccessListener {
                _userSnapshot.value = it
           }
    }

    suspend fun setNoteAdd(textEditNote: String, type:Int, post:Note?=null): DocumentSnapshot {
        val dcmRef= LoginStartActivity.db.collection("user").document(userEmail)
        var resultRef:DocumentSnapshot? = null

        coroutineScope {
            dcmRef.get().addOnSuccessListener {
                if (post == null) {
                    dcmRef.collection("note").document("${(it.get("numNote") as Long) +1}")
                        .set(Note(it.get("name") as String, textEditNote, Timestamp.now(),true, type, false))
                }
                else{
                    dcmRef.collection("note").document("${(it.get("numNote") as Long) +1}")
                        .set(Note(post.name, post.text, post.time,true, post.type, true))
                }
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

    suspend fun setNoteModify(textEditNote: String, order: String){
        val dcmRef= LoginStartActivity.db.collection("user").document(userEmail)

        coroutineScope {
            dcmRef.collection("note").document(order).update("text", textEditNote)
        }.await()

    }

    fun getStorageBottleLi(_stgBtSnapLi:MutableLiveData<ArrayList<BottleList>>
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
                            return@addSnapshotListener
                        }else
                            Log.d("서비스 토큰 변경X", "토큰 변경X")
                    }
            }
        }.await()
        return token
    }

    suspend fun setPostNoteAdd(receiver: String, textEditNote: String, type: Int, vaild:MutableLiveData<Boolean>){
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
                val timeNow = Timestamp.now()
                dcmRef!!.collection("postbox").document("${timeNow}")
                    .set(Note(userName, textEditNote, timeNow,false, type, true))
            }
        }.await()

        coroutineScope {
            dcmRef!!.update("numPost", FieldValue.increment(1)).addOnSuccessListener {
                vaild.value = true
            }
        }.await()
    }

    suspend fun deletePostNote(note:Note){
        val dcmRef:DocumentReference = db.collection("user").document(userEmail)
        var postVaild:Boolean = true
        coroutineScope {
            dcmRef.collection("postbox").document(note.time.toString()).delete()
                .addOnFailureListener {
                    Log.d("삭제 실패",it.toString())
                    postVaild = false
                }
        }.await()
        if (postVaild == false){
            // 메시지 삭제에 실패했습니다. 인터넷 연결을 확인해주세요 하는 다이얼로그 띄우기(리턴으로 그냥 결과 값 전송에서 프래그 먼트에서 판단하는게 좋을듯)
            return
        }
        coroutineScope {
            dcmRef.update("numPost", FieldValue.increment(-1))
        }.await()
    }

    suspend fun sendNotification(myResponce: MutableLiveData<Response<ResponseBody>>,notification: NotificationBody){
        myResponce.value = RetrofitInstance.api.sendNotification(notification)
    }

    fun getPostSnapshot(__checkPost: ArrayList<Note> ,_checkPost: MutableLiveData<ArrayList<Note>>){
        db.collection("user").document(userEmail).collection("postbox")
            .orderBy("time", Query.Direction.ASCENDING).addSnapshotListener { querySnapshot, _ ->
                if (querySnapshot == null) return@addSnapshotListener

                querySnapshot.documentChanges.forEachIndexed { i, dcm ->
                    if (dcm.type == DocumentChange.Type.ADDED) {
                        var post = dcm.document.toObject(Note::class.java)
                        Log.d("데이터 변경", post.toString())
                        __checkPost.add(0, post)
                    }
                    if (SendFragment.deletePosition != null) {
                        if (dcm.type == DocumentChange.Type.REMOVED) {
                            dcm.document.get("time").toString()
                            //Log.d("데이터 삭제", "${deletenote}")
                            __checkPost.removeAt(SendFragment.deletePosition!!)
                            SendFragment.deletePosition = null
                        }
                    }
                    _checkPost.value = __checkPost
                }
            }

    }

    suspend fun getToken(receiver: String): String{
        var token: String? = null
        coroutineScope {
            db.collection("user").whereEqualTo("name", receiver)
                .get().addOnSuccessListener { dcms ->
                    for (dcm in dcms){
                        token = dcm.get("token").toString()
                    }
                }
        }.await()
        return token.toString()
    }

    suspend fun getNewCommentSnapshot(_commentSnapshot:MutableLiveData<DocumentSnapshot>){

    }
}
