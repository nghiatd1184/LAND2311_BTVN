package com.nghiatd.mixic.auth

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

fun registerUserByEmail(email: String, password: String) = callbackFlow<Task<AuthResult>> {
    var listener: ((Task<AuthResult>) -> Unit)? = { task ->
        trySend(task)
    }
    val firebaseAuth = FirebaseAuth.getInstance()
    firebaseAuth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                task.result?.user?.let { addUserToFirestore(it) }
            }
            listener?.let { it(task) }
        }
    awaitClose {
        listener = null
    }
}

fun addUserToFirestore(user: FirebaseUser) {
    val userData = mapOf(
        "uid" to user.uid,
        "playlists" to emptyList<DocumentReference>()
    )
    val fireBaseFireStore = FirebaseFirestore.getInstance()
    fireBaseFireStore.collection("users").document(user.uid).set(userData)
}

fun updateProfile(name: String, photoUri: Uri?) = callbackFlow<Task<Void>> {
    var listener: ((Task<Void>) -> Unit)? = { task ->
        trySend(task)
    }
    val firebaseAuth = FirebaseAuth.getInstance()
    val user = firebaseAuth.currentUser ?: return@callbackFlow
    val profileUpdates = UserProfileChangeRequest.Builder()
        .setDisplayName(name)
        .setPhotoUri(photoUri)
        .build()
    user.updateProfile(profileUpdates)
        .addOnCompleteListener { task -> listener?.let { it(task) } }
    awaitClose {
        listener = null
    }
}

suspend fun uploadPhotoToFirebaseStorage(fileUri: Uri, fileName: String): String? {
    val storageReference: StorageReference = FirebaseStorage.getInstance().reference.child("/images/user_avatar/$fileName")
    return try {
        storageReference.putFile(fileUri).await()
        storageReference.downloadUrl.await().toString()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun login(email: String, password: String) = callbackFlow<Task<AuthResult>> {
    var listener: ((Task<AuthResult>) -> Unit)? = { task ->
        trySend(task)
    }
    val firebaseAuth = FirebaseAuth.getInstance()
    firebaseAuth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task -> listener?.let { it(task) } }
    awaitClose {
        listener = null
    }
}

fun changePassword(oldPassword: String, newPassword: String) = callbackFlow<Task<Void>> {
    var listener: ((Task<Void>) -> Unit)? = { task ->
        trySend(task)
    }
    val firebaseAuth = FirebaseAuth.getInstance()
    val user = firebaseAuth.currentUser ?: return@callbackFlow
    val email = user.email ?: return@callbackFlow
    val credential = EmailAuthProvider.getCredential(email, oldPassword)
    user.reauthenticate(credential).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            user.updatePassword(newPassword)
                .addOnCompleteListener { updateTask -> listener?.let { it(updateTask) } }
        } else {
            listener?.let { it(task) }
        }
    }
    awaitClose {
        listener = null
    }
}

fun forgotPassword(email: String) = callbackFlow<Task<Void>> {
    var listener: ((Task<Void>) -> Unit)? = {
        trySend(it)
    }
    val firebaseAuth = FirebaseAuth.getInstance()
    firebaseAuth.sendPasswordResetEmail(email)
        .addOnCompleteListener { task ->
            listener?.let { it(task) }
        }
    awaitClose {
        listener = null
    }
}