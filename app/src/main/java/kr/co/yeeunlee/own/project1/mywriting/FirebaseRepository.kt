package kr.co.yeeunlee.own.project1.mywriting

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.*
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import okhttp3.ResponseBody
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import retrofit2.Response
import java.lang.Exception

class FirebaseRepository(private val context: Context) {
    private val db = SplashActivity.db
    private var userName:String? = null

    private fun makeToast(exception: Exception){
        AlertDialog.Builder(context)
            .setTitle("서버 오류입니다.")
            .setMessage(" 관리자에게 문의해주세요. 오류코드:$exception")
            .setCancelable(false)
            .setPositiveButton("확인", object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, idx: Int) {
                    dialog!!.dismiss()
                }
            })
            .create()
            .show()
    }

    suspend fun getNameImgSnapshot():Pair<String?, Int?>{
        val userEmail = SplashActivity.mAuth.currentUser?.email ?: ""
        var name:String? = null
        var profileImg: Int? = null
        coroutineScope {
            db.collection("user").document(userEmail)
                .get().addOnSuccessListener {
                    name = it.get("name").toString()
                    if ( null != it.get("profileImg"))
                        profileImg = it.get("profileImg").toString().toInt()
                    else profileImg = null
                }
                .addOnFailureListener { makeToast(it) }
        }.await()
        return Pair(name, profileImg)
    }

//    suspend fun getUserProfileImgSnapshot():Int{
//        val userEmail = SplashActivity.mAuth.currentUser?.email ?: ""
//        var name:Int? = null
//        coroutineScope {
//            Log.d("이메일 오류 잡기", SplashActivity.mAuth.currentUser?.email.toString())
//            db.collection("user").document(userEmail)
//                .get().addOnSuccessListener {
//                    if (null == it.get("profileImg"))
//                        name = null
//                    else name = it.get("profileImg").toString().toInt()
//                    Log.d("이름 오류 잡기", name.toString())
//                }
//                .addOnFailureListener { makeToast(it) }
//        }.await()
//        return name?:0
//    }

    suspend fun getUserSnapshot(_userSnapshot:MutableLiveData<DocumentSnapshot>) {
        val userEmail = SplashActivity.mAuth.currentUser?.email ?: ""
        coroutineScope {    // 비동기가 하나라도 순서에 구애 받아야하면 무조건 코루틴을 넣어주자
            db.collection("user").document(userEmail)
                .get().addOnSuccessListener {
                    _userSnapshot.value = it
                    db.collection("user").document(userEmail)   // 변경이 있으면 다시 업뎃
                        .addSnapshotListener(MetadataChanges.INCLUDE){snapshot, e ->
                            Log.d("옵저버 변경", snapshot?.get("name").toString())
                            if (snapshot?.get("name") == null)  // 탈퇴할 때 에러방지
                                return@addSnapshotListener
                            _userSnapshot.value = snapshot
                            Log.d("메타데이터 변경", snapshot.toString())
                        }
                }
                .addOnFailureListener { makeToast(it) }
        }.await()
    }

    fun setUserStatusMsg(newStatus:String){
        val userEmail = SplashActivity.mAuth.currentUser?.email ?: ""
        db.collection("user").document(userEmail)
            .update("statusMsg", newStatus).addOnFailureListener { makeToast(it) }
    }

    suspend fun setNoteAdd(textEditNote: String, type:Int, post:Note?=null): DocumentSnapshot {
        val userEmail = SplashActivity.mAuth.currentUser?.email ?: ""
        val dcmRef= db.collection("user").document(userEmail)
        var resultRef:DocumentSnapshot? = null

        coroutineScope {
            dcmRef.get().addOnSuccessListener {
                if (post == null) {
                    dcmRef.collection("note").document("${(it.get("numNote") as Long) +1}")
                        .set(Note(it.get("name") as String, textEditNote, Timestamp.now()
                            ,true, type, false, -1))
                        .addOnFailureListener { makeToast(it) }
                }
                else{
                    dcmRef.collection("note").document("${(it.get("numNote") as Long) +1}")
                        .set(Note(post.name, post.text, post.time,true, post.type, true, -1))
                        .addOnFailureListener { makeToast(it) }
                }
            }
                .addOnFailureListener { makeToast(it) }
        }.await()

        coroutineScope {
            dcmRef.update("numNote", FieldValue.increment(1)).addOnFailureListener { makeToast(it) }
        }.await()

        coroutineScope {
            db.collection("user").document(userEmail).get().addOnSuccessListener {
                resultRef = it
            }
                .addOnFailureListener { makeToast(it) }
        }.await()

        return resultRef!!
    }

    suspend fun setNoteModify(textEditNote: String, order: String){
        val userEmail = SplashActivity.mAuth.currentUser?.email ?: ""
        val dcmRef= db.collection("user").document(userEmail)

        coroutineScope {
            dcmRef.collection("note").document(order).update("text", textEditNote)
                .addOnFailureListener { makeToast(it) }
        }.await()

    }

    suspend fun getStorageBottleLi(_stgBtSnapLi:MutableLiveData<ArrayList<BottleList>>
                         , __stgBtSnapLi:ArrayList<BottleList>, zeroBottle:MutableLiveData<Boolean>){
        val userEmail = SplashActivity.mAuth.currentUser?.email ?: ""
        __stgBtSnapLi.clear()
        coroutineScope {
            db.collection("user").document(userEmail)
                .get().addOnSuccessListener {
                    val numBottle: Int = it.get("numNote").toString().toInt() / 30
                    zeroBottle.value = numBottle == 0
                    Log.d("보틀 수", numBottle.toString())
                    for ( i in numBottle downTo (1) step(3)){   // 선반 하나에 보틀 3개
                        Log.d("보틀 수1", i.toString())
                        if ((i - 2) >= 1){
                            __stgBtSnapLi.add(BottleList(i*30, (i-1)*30, (i-2)*30))
                        }
                        else if(i == 2){
                            __stgBtSnapLi.add(BottleList(i*30, (i-1)*30, null))
                        }
                        else{
                            __stgBtSnapLi.add(BottleList(i*30, null, null))
                        }
                    }
                    //Log.d("보틀 수2", __stgBtSnapLi[0].toString())
                    _stgBtSnapLi.value = __stgBtSnapLi
                }
                .addOnFailureListener { makeToast(it) }
        }.await()
    }

    suspend fun getOpnNoteSnapshot(index: Int) : DocumentSnapshot{
        val userEmail = SplashActivity.mAuth.currentUser?.email ?: ""
        var resultRef:DocumentSnapshot? = null
        coroutineScope {
            db.collection("user").document(userEmail)
                .collection("note").document(index.toString())
                .get().addOnSuccessListener { resultRef = it }
                .addOnFailureListener { makeToast(it) }
        }.await()
        return resultRef!!
    }

    suspend fun setToken(): String{
        val userEmail = SplashActivity.mAuth.currentUser?.email ?: ""
        var token:String = ""
        coroutineScope {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.d("서비스 토큰", "토큰 등록에 실패함")
                    return@addOnCompleteListener
                }
                token = task.result
                db.collection("user").document(userEmail).addSnapshotListener{document, _->
                    userName = document!!.get("name").toString()    // 사용자 이름 초기화
                    Log.d("서비스 토큰 변경", userName+token)
                    if (document == null) return@addSnapshotListener

                    if (document["token"].toString() == "false") {
                        Log.d("서비스 토큰", "푸시 알림 거부")
                        return@addSnapshotListener
                    }else if (document["token"].toString() != token) {
                        Log.d("서비스 토큰 변경", "토큰 변경")
                        // 이 부분을 await으로 주었더니 정상 작동이 안된 이유는 addSnapshotListener가 사용되는 메서드 역시 비동기이기 때문.
                        // addSnapshotListener은 데베가 업데이트 되는대로 여러번 실행될 수 있기 때문에 await이 사용될 수 없다.
                        // await은 단일 결과만 기다리기 때문에 addSnapshotListener의 메서드와 패러다임이 맞지 않다고 한다.
                        db.collection("user").document(userEmail).update("token", token).addOnSuccessListener {
                            Log.d("서비스 토큰 변경success", "정상 작동")
                        }
//                            .addOnFailureListener { makeToast(it) }
                        return@addSnapshotListener
                    }
                }

            }
//                .addOnFailureListener { makeToast(it) }
        }.await()


        return token
    }

    suspend fun setPushAlarm(setVaild:Boolean){
        val userEmail = SplashActivity.mAuth.currentUser?.email ?: ""
        if (setVaild == false){
            coroutineScope {
                db.collection("user").document(userEmail).update("token","false")
                    .addOnFailureListener { makeToast(it) }
            }.await()
        }else if (setVaild == true){
            coroutineScope {
                db.collection("user").document(userEmail).update("token","true")
                    .addOnFailureListener { makeToast(it) }
            }.await()
        }
    }

    suspend fun setPostNoteAdd(
        receiver: String, textEditNote: String, type: Int, profileImg:Int, vaild:MutableLiveData<Boolean>){
        val userEmail = SplashActivity.mAuth.currentUser?.email ?: ""
        var dcmRef:DocumentReference? = null
        var userName:String = ""

        coroutineScope { db.collection("user").document(userEmail).get()
            .addOnSuccessListener { userName = it.get("name").toString() }
            .addOnFailureListener { makeToast(it) }
        }.await()

        coroutineScope { db.collection("user").whereEqualTo("name",receiver).get()
            .addOnSuccessListener { documents ->
                for (document in documents){
                    dcmRef = document.reference
                }
            }
            .addOnFailureListener { makeToast(it) }
        }.await()

        coroutineScope {
            dcmRef!!.get().addOnSuccessListener {
                val timeNow = Timestamp.now()
                dcmRef!!.collection("postbox").document("${timeNow}")
                    .set(Note(userName, textEditNote, timeNow,false, type, true, profileImg))
                    .addOnFailureListener { makeToast(it) }
            }
                .addOnFailureListener { makeToast(it) }
        }.await()

        coroutineScope {
            dcmRef!!.update("numPost", FieldValue.increment(1)).addOnSuccessListener {
                vaild.value = true
            }
                .addOnFailureListener { makeToast(it) }
        }.await()
    }

    suspend fun deletePostNote(note:Note){
        val userEmail = SplashActivity.mAuth.currentUser?.email ?: ""
        val dcmRef:DocumentReference = db.collection("user").document(userEmail)
        coroutineScope {
            dcmRef.collection("postbox").document(note.time.toString()).delete()
                .addOnFailureListener { makeToast(it) }
        }.await()
        coroutineScope {
            dcmRef.update("numPost", FieldValue.increment(-1))
                .addOnFailureListener { makeToast(it) }
        }.await()
    }

    suspend fun sendNotification(myResponce: MutableLiveData<Response<ResponseBody>>,notification: NotificationBody){
        myResponce.value = RetrofitInstance.api.sendNotification(notification)
    }

    fun getPostSnapshot(__checkPost: ArrayList<Note> ,_checkPost: MutableLiveData<ArrayList<Note>>){
        val userEmail = SplashActivity.mAuth.currentUser?.email ?: ""
        db.collection("user").document(userEmail).collection("postbox")
            .orderBy("time", Query.Direction.ASCENDING).addSnapshotListener { querySnapshot, _ ->
                if (querySnapshot == null) return@addSnapshotListener
                if (__checkPost.size == querySnapshot.size())   // 업데이트 중복 방지
                    return@addSnapshotListener
                querySnapshot.documentChanges.forEachIndexed { i, dcm ->
                    if (dcm.type == DocumentChange.Type.ADDED) {
                        var post = dcm.document.toObject(Note::class.java)
                        Log.d("데이터 변경", post.toString())
                        __checkPost.add(0, post)
                        _checkPost.value = __checkPost
                    }
                    if (SendFragment.deletePosition != null) {
                        if (dcm.type == DocumentChange.Type.REMOVED) {
                            dcm.document.get("time").toString()
                            //Log.d("데이터 삭제", "${deletenote}")
                            __checkPost.removeAt(SendFragment.deletePosition!!)
                            SendFragment.deletePosition = null
                            _checkPost.value = __checkPost
                        }
                    }
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
                .addOnFailureListener { makeToast(it) }
        }.await()
        return token.toString()
    }

}
