package com.example.finalattempt5.fragments.weekdays

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalattempt5.R
import com.example.finalattempt5.viewmodel.TaskViewModel
import kotlinx.android.synthetic.main.fragment_sunday.view.*
import kotlinx.android.synthetic.main.fragment_tuesday.view.*


class TuesdayFragment : Fragment() {
    private lateinit var mTaskViewModel: TaskViewModel
    val adapter = ListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_tuesday, container, false)

        val recyclerView = view.recyclerview_tuesday
        recyclerView.adapter = adapter

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        mTaskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)
        mTaskViewModel.filterWeekday("Tuesday").observe(viewLifecycleOwner, Observer { task ->
            adapter.setData(task)
        })

        return view
    }

}