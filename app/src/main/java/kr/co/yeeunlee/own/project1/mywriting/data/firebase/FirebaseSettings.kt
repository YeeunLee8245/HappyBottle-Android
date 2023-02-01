package kr.co.yeeunlee.own.project1.mywriting.data.firebase

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import com.jakewharton.threetenabp.AndroidThreeTen.init

object FirebaseSettings {

    private val settings = firestoreSettings {
        isPersistenceEnabled = true
        setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED).build()
    }

    private val firestore = Firebase.firestore
    private val auth by lazy { Firebase.auth }

    init {
        firestore.firestoreSettings = settings
    }

    fun getFirestore() = firestore

    fun getAuthentication() = auth

}