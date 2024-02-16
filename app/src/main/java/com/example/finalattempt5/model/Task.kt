package com.example.finalattempt5.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "task_table")
class Task (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val description: String,
    val status: String,
    val start_time: String,
    val end_time: String,
    val weekday: String,
    val start_hour: Int,
    val start_minute: Int,
    val notification_id: Int,
): Parcelable