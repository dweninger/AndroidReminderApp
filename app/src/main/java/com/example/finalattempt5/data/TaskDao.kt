package com.example.finalattempt5.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.finalattempt5.model.Task

@Dao
interface TaskDao {

    @Query("SELECT * FROM task_table")
    fun getAll(): LiveData<List<Task>>

    @Update
    fun updateTask(task: Task)

    @Query("SELECT * FROM task_table WHERE id = (:taskId)")
    fun loadAllById(taskId: List<Int>): List<Task>

    @Insert
    suspend fun insertAll(task: Task): Long

    @Delete
    fun deleteTask(task: Task)

    @Query("DELETE FROM task_table")
    suspend fun deleteAllTasks()

    @Query("SELECT * FROM task_table ORDER BY id ASC")
    fun readAllData(): LiveData<List<Task>>

    @Query("SELECT * FROM task_table WHERE weekday LIKE :filterWeekday ORDER BY start_hour ASC, start_minute ASC")
    fun filterTask(filterWeekday: String): LiveData<List<Task>>


    @Query("SELECT * FROM task_table WHERE notification_id = (:notification_id)")
    fun getNotificationId(notification_id: Int): LiveData<List<Task>>
}