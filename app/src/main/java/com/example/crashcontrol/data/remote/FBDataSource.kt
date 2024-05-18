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

class FBDataSource(database: FirebaseFirestore) {
    private val refToUsers: CollectionReference = database.collection("Users")

    fun saveUser(userId: String, user: FBUser) {
        refToUsers.document(userId).set(user)
    }

    suspend fun loadUser(userId: String): FBUser? {
        val document = refToUsers.document(userId).get().await()
        return if (document.exists()) {
            document.toObject(FBUser::class.java)
        } else {
            null
        }

    }

    fun deleteUser(userId: String) {
        refToUsers.document(userId).delete()
    }
}