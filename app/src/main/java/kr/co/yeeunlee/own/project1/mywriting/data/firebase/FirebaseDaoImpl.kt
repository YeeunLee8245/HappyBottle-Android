package kr.co.yeeunlee.own.project1.mywriting.data.firebase

import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase

class FirebaseDaoImpl { // Query를 통해 DB(Firebase)를 다루는 객체

    private val settings = firestoreSettings {
        isPersistenceEnabled = true
        setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED).build()
    }
    private val firebaseFireStore = Firebase.firestore

    init {
        firebaseFireStore.firestoreSettings = settings // 캐시와 오프라인 지속성 설정
    }

}