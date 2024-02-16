package com.example.finalattempt5.fragments.update

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.finalattempt5.MainActivity
import com.example.finalattempt5.NotificationHelper
import com.example.finalattempt5.R
import com.example.finalattempt5.model.Task
import com.example.finalattempt5.model.Weekdays
import com.example.finalattempt5.viewmodel.TaskViewModel
import kotlinx.android.synthetic.main.fragment_update.*
import java.time.LocalDateTime
import java.util.*

class UpdateFragment : Fragment() {
    private val args by navArgs<UpdateFragmentArgs>()

    private lateinit var mTaskViewModel: TaskViewModel
    var start_time = "0 : 0"
    var end_time = "0 : 0"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_update, container, false)
        mTaskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)

        val spinner: Spinner = view.findViewById(R.id.spinnerUpdateDay)
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.Weekdays,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            val currentPos = adapter.getPosition(args.currentTask.weekday)

            // Apply the adapter to the spinner
            spinner.adapter = adapter
            view.findViewById<Spinner>(R.id.spinnerUpdateDay).setSelection(currentPos)
        }
        // Inflate the layout for this fragment

        view.findViewById<EditText>(R.id.updateTitle).setText(args.currentTask.title)

        view.findViewById<Button>(R.id.buttonUpdateStartTime)
            .setText(formatTime(args.currentTask.start_time))
        start_time = args.currentTask.start_time
        view.findViewById<Button>(R.id.buttonUpdateEndTime).setText(formatTime(args.currentTask.end_time))
        end_time = args.currentTask.end_time
        view.findViewById<EditText>(R.id.updateDescription)
            .setText(args.currentTask.description)

        view.findViewById<Button>(R.id.buttonUpdateStartTime).setOnClickListener {
            openStartTimeDialog()
        }

        view.findViewById<Button>(R.id.buttonUpdateEndTime).setOnClickListener {
            openEndTimeDialog()
        }

        setHasOptionsMenu(true)

            return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.update_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_delete_update) {
            deleteTask()
        }
        else if(item.itemId == R.id.menu_save_update) {
            updateItem()
        } else {
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateItem() {
        val title = updateTitle.text.toString()
        val day = spinnerUpdateDay.selectedItem.toString()
        val description = updateDescription.text.toString()

        if(inputCheck(title, day, start_time, end_time, description) && timeCheck()) {
            val startTimeParts = start_time.split(" : ")
            val start_hour = startTimeParts[0].toInt()
            val start_minute = startTimeParts[1].toInt()

            val updatedTask = Task(args.currentTask.id, title, description, "incomplete", start_time, end_time, day, start_hour, start_minute, args.currentTask.notification_id)

            mTaskViewModel.updateTask(updatedTask)

            NotificationHelper(requireContext()).cancelNotificaiton(args.currentTask.notification_id)
            MainActivity().createWorkRequest(title, description, args.currentTask.notification_id, getTimeDelay(day))

            Toast.makeText(requireContext(), "Updated successfully!", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        } else if (!inputCheck(title, day, start_time, end_time, description)){
            Toast.makeText(requireContext(), "Please fill out all fields.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Please enter an end time after a start time!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun inputCheck(title: String, day: String, start_time: String, end_time: String, description: String): Boolean {
        return !(TextUtils.isEmpty(title) || TextUtils.isEmpty(day) || TextUtils.isEmpty(start_time) || TextUtils.isEmpty(end_time) || TextUtils.isEmpty(description))
    }

    private fun timeCheck(): Boolean {
        val startTimeParts = start_time.split(" : ")
        val endTimeParts = end_time.split(" : ")
        if(startTimeParts[0].toInt() < endTimeParts[0].toInt()) {
            return true
        } else if (startTimeParts[0].toInt() == endTimeParts[0].toInt()) {
            return startTimeParts[1] < endTimeParts[1]
        } else {
            return false
        }
    }

    fun getTimeDelay(weekday: String): Long {
        var ampm = 0
        var startHour = start_time.split(" : ")[0].toInt()
        if(startHour > 12){
            startHour -= 12
            ampm = 1
        }

        val startMinute = start_time.split(" : ")[1].toInt()
        val todayDateTime = Calendar.getInstance()
        var alarmWeekday = Weekdays.valueOf(weekday).ordinal
        val nowWeekday = LocalDateTime.now().dayOfWeek.value
        var daysDiff = 0

        val userSelectedDateTime = Calendar.getInstance()
        userSelectedDateTime.set(Calendar.HOUR, startHour)
        userSelectedDateTime.set(Calendar.MINUTE, startMinute)
        userSelectedDateTime.set(Calendar.SECOND, 0)
        userSelectedDateTime.set(Calendar.AM_PM, ampm)

        if(alarmWeekday < nowWeekday || (alarmWeekday == nowWeekday && userSelectedDateTime.time < todayDateTime.time)) {
            alarmWeekday += 7
        }

        daysDiff = alarmWeekday - nowWeekday


        userSelectedDateTime.add(Calendar.DAY_OF_MONTH, daysDiff)


        val delayInSeconds = (userSelectedDateTime.timeInMillis/1000L) - (todayDateTime.timeInMillis/1000L)
        println("DELAY: " + delayInSeconds)
        println(todayDateTime)
        println(userSelectedDateTime)
        return delayInSeconds
    }

    private fun deleteTask() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") {_,_ ->
            mTaskViewModel.deleteTask(args.currentTask)
            Toast.makeText(requireContext(), "Successfully deleted: ${args.currentTask.title}", Toast.LENGTH_SHORT).show()
            //NotificationHelper(requireContext()).cancelNotificaiton(args.currentTask.notification_id)
            println("NOTIFICATION ID: " + args.currentTask.notification_id)
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        }
        builder.setNegativeButton("No") {_,_ ->
        }
        builder.setTitle("Delete ${args.currentTask.title}?")
        builder.setMessage("Are you sure you want to delete ${args.currentTask.title}?")
        builder.create().show()
    }

    fun openStartTimeDialog() {
        var hour: Int = 0
        var minute: Int = 0
        val mTimePicker = TimePickerDialog(requireContext(), object: TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hour: Int, minute: Int) {
                val formattedTime = formatTime(String.format("%s : %s", hour.toString(), minute.toString()))
                val formattedTimeParts = formattedTime.split(" : ", " ")
                buttonUpdateStartTime.setText(formattedTime)
                start_time = "" + hour + " : " + formattedTimeParts[1]
            }
        }, hour, minute, false)
        mTimePicker.show()

    }

    fun openEndTimeDialog() {
        var hour: Int = 0
        var minute: Int = 0
        val mTimePicker = TimePickerDialog(requireContext(), object: TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hour: Int, minute: Int) {
                val formattedTime = formatTime(String.format("%s : %s", hour.toString(), minute.toString()))
                val formattedTimeParts = formattedTime.split(" : ", " ")
                buttonUpdateEndTime.setText(formattedTime)
                end_time = "" + hour + " : " + formattedTimeParts[1]
            }
        }, hour, minute, false)
        mTimePicker.show()
    }

    fun formatTime(time: String): String {
        val parts = time.split(" : ")
        var hourString = parts[0]
        var minuteString = parts[1]
        var hour = hourString.toInt()
        var minute = minuteString.toInt()
        var ampm = "AM"

        if(hour == 0) {
            hourString = "12"
        }
        if(hour > 11) {
            ampm = "PM"
            if(hour > 12) {
                hourString = (hour - 12).toString()
            }
        }
        if(minute < 10) {
            minuteString = "0" + minute
        }
        return String.format("%s : %s  %s", hourString, minuteString, ampm)
    }

}