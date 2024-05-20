package com.example.crashcontrol.data.remote

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class FBUser(
    val email: String = "",
    val username: String = "",
    val name: String = "",
    val surname: String = "",
    val birthday: String = "",
    val picture: String = "",
)

data class FBCrash(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val exclamation: String = "",
    val date: String = "",
    val time: String = "",
    val face: String = ""
)

class FBDataSource(database: FirebaseFirestore) {
    private val refToUsers: CollectionReference = database.collection("Users")
    private val refToCrashes: CollectionReference = database.collection("Crashes")

    fun saveUser(userId: String, user: FBUser) {
        refToUsers.document(userId).set(user)
    }

    fun saveCrash(userId: String, crash: FBCrash) {
        refToCrashes.document(userId).set(crash)
    }

    suspend fun loadUser(userId: String): FBUser? {
        val document = refToUsers.document(userId).get().await()
        return if (document.exists()) {
            document.toObject(FBUser::class.java)
        } else {
            null
        }
    }

    suspend fun loadCrash(userId: String): FBCrash? {
        val document = refToCrashes.document(userId).get().await()
        return if (document.exists()) {
            document.toObject(FBCrash::class.java)
        } else {
            null
        }
    }

    suspend fun loadCrashes(): Set<FBCrash> {
        val document = refToCrashes.get().await()
        val crashes: MutableSet<FBCrash> = mutableSetOf()
        for (doc in document) {
            if (doc.exists()) {
                crashes.add(doc.toObject(FBCrash::class.java))
            }
        }
        return crashes
    }

    fun deleteUser(userId: String) {
        refToUsers.document(userId).delete()
    }

    fun deleteCrash(userId: String) {
        refToCrashes.document(userId).delete()
    }
}