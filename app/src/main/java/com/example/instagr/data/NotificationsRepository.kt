package com.example.instagr.data

import androidx.lifecycle.LiveData
import com.example.instagr.models.Notification
import com.google.android.gms.tasks.Task

interface NotificationsRepository {
    fun setNotificationsRead(uid: String, ids: List<String>, read: Boolean): Task<Unit>
    fun createNotification(uid: String, notification: Notification): Task<Unit>
    fun getNotifications(uid: String): LiveData<List<Notification>>
}