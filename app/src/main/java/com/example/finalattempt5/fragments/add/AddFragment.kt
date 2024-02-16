package com.example.finalattempt5.fragments.add

import android.app.TimePickerDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.finalattempt5.MainActivity
import com.example.finalattempt5.R
import com.example.finalattempt5.fragments.weekdays.ListAdapter
import com.example.finalattempt5.model.Task
import com.example.finalattempt5.model.Weekdays
import com.example.finalattempt5.viewmodel.TaskViewModel
import kotlinx.android.synthetic.main.fragment_add.*
import java.time.LocalDateTime
import java.util.*


class AddFragment : Fragment() {

    private lateinit var mTaskViewModel: TaskViewModel
    var start_time = "0 : 0"
    var end_time = "0 : 0"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view =  inflater.inflate(R.layout.fragment_add, container, false)

        mTaskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)

        val spinner: Spinner = view.findViewById(R.id.spinnerDay)
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.Weekdays,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            // Apply the adapter to the spinner
            spinner.adapter = adapter

            view.findViewById<Spinner>(R.id.spinnerDay).setSelection(LocalDateTime.now().dayOfWeek.value)
        }

        view.findViewById<Button>(R.id.buttonStartTime).setOnClickListener {
            openStartTimeDialog()
        }

        view.findViewById<Button>(R.id.buttonEndTime).setOnClickListener {
            openEndTimeDialog()
        }

        setHasOptionsMenu(true)
        return view
    }

    private fun insertDataToDatabase() {
        val title = editTitle.text.toString()
        val day = spinnerDay.selectedItem.toString()
        val description = editTextDescription.text.toString()

        if(inputCheck(title, day, start_time, end_time, description) && timeCheck()) {
            val startTimeParts = start_time.split(" : ")
            val start_hour = startTimeParts[0].toInt()
            val start_minute = startTimeParts[1].toInt()
            var notification_id = getNotificationId()

            val task = Task(0, title, description, "Incomplete", start_time, end_time, day, start_hour, start_minute, notification_id)
            val newId = mTaskViewModel.addTask(task)

            Toast.makeText(requireContext(), "Successfully added task!", Toast.LENGTH_SHORT).show()
            // Create notification
            MainActivity().createWorkRequest(title, description, notification_id, getTimeDelay(day))
            // Navigate to List
            findNavController().navigate(R.id.action_addFragment_to_listFragment)
        } else if(!timeCheck()){
            Toast.makeText(requireContext(), "Please select an end time after the start time!", Toast.LENGTH_SHORT).show()
        }
        else {
            Toast.makeText(requireContext(), "Please enter data in all input fields!", Toast.LENGTH_SHORT).show()
        }
    }

    fun getNotificationId(): Int {
        var curNotificationId = mTaskViewModel.getNoficationId(ListAdapter().itemCount).value

        print("CurNotificationId: ")
        println(curNotificationId)
        var curRandom = 0

        while(curNotificationId != null && curNotificationId.isNotEmpty()) {
            curRandom = (0..500).random()
            curNotificationId = mTaskViewModel.getNoficationId(ListAdapter().itemCount + curRandom).value
        }
        println("NOTIFICATION ID: " + (ListAdapter().itemCount + curRandom))
        return ListAdapter().itemCount + curRandom
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
        userSelectedDateTime.set(Calendar.MILLISECOND, 0)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_save) {
            insertDataToDatabase()
        } else if(item.itemId == R.id.menu_back) {
            findNavController().navigate(R.id.action_addFragment_to_listFragment)
        }
        return super.onOptionsItemSelected(item)
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

    fun openStartTimeDialog() {
        var hour: Int = 0
        var minute: Int = 0
        val mTimePicker = TimePickerDialog(requireContext(), object: TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hour: Int, minute: Int) {
                val formattedTime = formatTime(String.format("%s : %s", hour.toString(), minute.toString()))
                val formattedTimeParts = formattedTime.split(" : ", " ")
                buttonStartTime.setText(formattedTime)
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
                buttonEndTime.setText(formattedTime)
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