package kr.co.yeeunlee.own.project1.mywriting

import android.util.Log
import androidx.lifecycle.MutableLiveData
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

    suspend fun setNoteAdd(textEditNote: String){
        val dcmRef= LoginStartActivity.db.collection("user").document(LoginStartActivity.mAuth.currentUser!!.email!!)

        coroutineScope {
            dcmRef.get().addOnSuccessListener {
                dcmRef.collection("note").document("${(it.get("numNote") as Long) +1}")
                    .set(Note(it.get("name") as String, textEditNote, Timestamp.now()))
            }
        }.await()

        dcmRef.update("numNote", FieldValue.increment(1))

    }

    fun getStorageBottle(_storageBottleSnap:MutableLiveData<DocumentSnapshot>){

    }

    suspend fun getNewCommentSnapshot(_commentSnapshot:MutableLiveData<DocumentSnapshot>){

    }
}