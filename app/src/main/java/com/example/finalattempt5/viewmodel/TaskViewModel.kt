package com.example.finalattempt5.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.finalattempt5.data.TaskDatabase
import com.example.finalattempt5.model.Task
import com.example.finalattempt5.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskViewModel(application: Application): AndroidViewModel(application) {
    val readAllData: LiveData<List<Task>>
    private val repository: TaskRepository
    init {
        val taskDao = TaskDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)
        readAllData = repository.getAll
    }

    fun addTask(task: Task)  {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addTask(task)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTask(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTask(task)
        }
    }

    fun deleteAllTasks() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllTasks()
        }
    }
    fun filterWeekday(filterWeekday: String): LiveData<List<Task>> {
        return repository.filterTask(filterWeekday)
    }

    fun getNoficationId(notification_id: Int): LiveData<List<Task>> {
        return repository.getNotificationId(notification_id)
    }
}