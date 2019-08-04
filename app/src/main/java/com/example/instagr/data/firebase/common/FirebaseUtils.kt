package com.example.instagr.data.firebase.common

import androidx.lifecycle.LiveData
import com.example.instagr.models.Comment
import com.example.instagr.models.FeedPost
import com.example.instagr.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

fun DataSnapshot.asUser(): User? = getValue(User::class.java)?.copy(uid = key!!)

fun DataSnapshot.asFeedPost(): FeedPost? =
    getValue(FeedPost::class.java)?.copy(id = key!!)

fun DataSnapshot.asComment() : Comment? =
    getValue(Comment::class.java)?.copy(id = key!!)

fun DatabaseReference.setValueTrueOrRemove(value: Boolean) =
    if (value) setValue(true) else removeValue()

val auth: FirebaseAuth = FirebaseAuth.getInstance()
val database: DatabaseReference = FirebaseDatabase.getInstance()
    .reference
val storage: StorageReference = FirebaseStorage.getInstance()
    .reference

fun DatabaseReference.liveData(): LiveData<DataSnapshot> =
    FirebaseLiveData(this)