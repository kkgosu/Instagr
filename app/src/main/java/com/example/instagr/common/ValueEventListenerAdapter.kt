package com.example.instagr.common

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ValueEventListenerAdapter(val handler: (DataSnapshot) -> Unit) :
    ValueEventListener {
    private val TAG = "ValueEventListenerAdapt"
    override fun onCancelled(p0: DatabaseError) {
        Log.e(TAG, "onCancelled: ", p0.toException())
    }


    override fun onDataChange(p0: DataSnapshot) {
        handler(p0)
    }
}