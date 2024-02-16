package com.example.finalattempt5.repository

import androidx.lifecycle.LiveData
import com.example.finalattempt5.data.TaskDao
import com.example.finalattempt5.model.Task

class TaskRepository(private val taskDao: TaskDao) {
    val getAll: LiveData<List<Task>> = taskDao.getAll()



    suspend fun addTask(task: Task) {
        taskDao.insertAll(task)
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }

    suspend fun deleteAllTasks() {
        taskDao.deleteAllTasks()
    }

    fun filterTask(filterWeekday: String): LiveData<List<Task>> {
        return taskDao.filterTask(filterWeekday)
    }

    fun getNotificationId(notification_id: Int): LiveData<List<Task>> {
        return taskDao.getNotificationId(notification_id)
    }

}