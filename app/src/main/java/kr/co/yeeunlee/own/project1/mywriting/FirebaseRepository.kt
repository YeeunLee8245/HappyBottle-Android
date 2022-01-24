package kr.co.yeeunlee.own.project1.mywriting

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

class FirebaseRepository {
    private val db = LoginStartActivity.db
    private val userEmail = LoginStartActivity.mAuth.currentUser!!.email!!

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
                    .set(Note(it.get("name") as String, textEditNote, Timestamp.now()))
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
                Log.d("보틀 수2", __stgBtSnapLi[0].toString())
                _stgBtSnapLi.value = __stgBtSnapLi
            }
    }

    suspend fun getNewCommentSnapshot(_commentSnapshot:MutableLiveData<DocumentSnapshot>){

    }
}