package kr.co.yeeunlee.own.project1.mywriting

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.coroutineScope

class FirebaseRepository {
    private val db = LoginStartActivity.db
    private val userEmail = LoginStartActivity.mAuth.currentUser!!.email!!

    fun getUserSnapshot(_noteSnapshot:MutableLiveData<DocumentSnapshot>) {
       db.collection("user").document(userEmail)
           .get().addOnSuccessListener {
                _noteSnapshot.value = it
           }
    }

    suspend fun getBottleSnapshot(_noteSnapshot:MutableLiveData<DocumentSnapshot>){

    }

    suspend fun getNewCommentSnapshot(_commentSnapshot:MutableLiveData<DocumentSnapshot>){

    }
}