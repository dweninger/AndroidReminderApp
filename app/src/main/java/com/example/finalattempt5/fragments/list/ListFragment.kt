package com.example.finalattempt5.fragments.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.finalattempt5.R
import com.example.finalattempt5.adapters.VPAdapter
import com.example.finalattempt5.fragments.weekdays.*
import com.example.finalattempt5.viewmodel.TaskViewModel
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_list.view.*
import java.time.LocalDateTime

class ListFragment : Fragment() {
    var adapter = ListAdapter()
    private lateinit var mTaskViewModel: TaskViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_list, container, false)

        mTaskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)
        mTaskViewModel.readAllData.observe(viewLifecycleOwner, Observer { task ->
            adapter.setData(task)
        })

        val fragmentList = arrayListOf<Fragment>(
            SundayFragment(),
            MondayFragment(),
            TuesdayFragment(),
            WednesdayFragment(),
            ThursdayFragment(),
            FridayFragment(),
            SaturdayFragment(),
        )

        val adapter = VPAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        view.viewpager.adapter = adapter

        view.viewpager.setCurrentItem(LocalDateTime.now().dayOfWeek.value, false)

        TabLayoutMediator(view.tab_layout, view.viewpager) {
                tab, position->
            when(position) {
                0->{
                    tab.text="SUN"
                }
                1->{
                    tab.text="MON"
                }
                2->{
                    tab.text="TUE"
                }
                3->{
                    tab.text="WED"
                }
                4->{
                    tab.text="THU"
                }
                5->{
                    tab.text="FRI"
                }
                6->{
                    tab.text="SAT"
                }
            }
        }.attach()

        setHasOptionsMenu(true)

        return view

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_add) {
            findNavController().navigate(R.id.action_listFragment_to_addFragment)
        } else if(item.itemId == R.id.menu_delete) {
            deleteAllTasks()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun deleteAllTasks() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") {_,_ ->
//            mTaskViewModel.readAllData.value?.forEach { task ->
//                NotificationHelper(requireContext()).cancelNotificaiton(task.notification_id)
//            }
            mTaskViewModel.deleteAllTasks()
            Toast.makeText(requireContext(), "Successfully deleted all tasks", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("No") {_,_ ->

        }
        builder.setTitle("Delete all tasks?")
        builder.setMessage("Are you sure you want to delete all tasks?")
        builder.create().show()
    }

}