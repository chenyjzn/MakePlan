package com.yuchen.makeplan.task

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.yuchen.makeplan.R
import com.yuchen.makeplan.databinding.FragmentTaskBinding
import com.yuchen.makeplan.ext.getVmFactory
import com.yuchen.makeplan.util.TimeUtil.StampToDate
import java.util.*


class TaskFragment : Fragment() {



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentTaskBinding.inflate(inflater, container, false)
        val viewModel: TaskViewModel by viewModels<TaskViewModel> { getVmFactory(TaskFragmentArgs.fromBundle(requireArguments()).projectHistory,
            TaskFragmentArgs.fromBundle(requireArguments()).projectHistoryPos,resources.getStringArray(R.array.color_array).toList())}
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        val colorAdaptor = TaskColorAdapter(viewModel)
        binding.taskColorRecycler.adapter = colorAdaptor
        binding.taskColorRecycler.layoutManager = GridLayoutManager(binding.taskColorRecycler.context,6)
        val calendar = Calendar.getInstance()

        viewModel.newStartTimeMillis.observe(viewLifecycleOwner, Observer {
            it?.let {startTime ->
                Log.d("chenyjzn","start change = ${StampToDate(startTime)}")
                viewModel.newEndTimeMillis.value?.let { endTime ->
                    if (startTime > endTime)
                        viewModel.setEndTimeByTimeMillis(startTime)

                    viewModel.duration = endTime - startTime
                }
            }
        })

        viewModel.newEndTimeMillis.observe(viewLifecycleOwner, Observer {
            it?.let {endTime ->
                Log.d("chenyjzn","end change = ${StampToDate(endTime)}")
                viewModel.newStartTimeMillis.value?.let { startTime ->
                    if (startTime > endTime)
                        viewModel.setEndTimeByTimeMillis(startTime)

                    viewModel.duration = endTime - startTime
                }
            }
        })



        binding.taskStartDateEdit.setOnClickListener {
            calendar.timeInMillis = viewModel.newStartTimeMillis.value?:calendar.timeInMillis
            val startDatePickerDialog = DatePickerDialog(requireNotNull(context),object : DatePickerDialog.OnDateSetListener{
                override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                    viewModel.setStartDate(year, month, dayOfMonth)
                }
            },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH))
            startDatePickerDialog.show()
        }

        binding.taskStartTimeEdit.setOnClickListener {
            calendar.timeInMillis = viewModel.newStartTimeMillis.value?:calendar.timeInMillis
            val startTimePickerDialog = TimePickerDialog(requireNotNull(context),object : TimePickerDialog.OnTimeSetListener{
                override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                    viewModel.setStartTime(hourOfDay,minute)
                }
            },calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false)
            startTimePickerDialog.show()
        }

        binding.taskEndDateEdit.setOnClickListener {
            calendar.timeInMillis = viewModel.newEndTimeMillis.value?:calendar.timeInMillis
            val endDatePickerDialog = DatePickerDialog(requireNotNull(context),object : DatePickerDialog.OnDateSetListener{
                override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                    viewModel.setEndtDate(year, month, dayOfMonth)
                }
            },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH))
            viewModel.newStartTimeMillis.value?.let {
                endDatePickerDialog.datePicker.minDate = it
            }
            endDatePickerDialog.show()
        }

        binding.taskEndTimeEdit.setOnClickListener {
            calendar.timeInMillis = viewModel.newEndTimeMillis.value?:calendar.timeInMillis
            val endTimePickerDialog = TimePickerDialog(requireNotNull(context),object : TimePickerDialog.OnTimeSetListener{
                override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                    viewModel.setEndTime(hourOfDay,minute)
                }
            },calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false)
            endTimePickerDialog.show()
        }

        viewModel.newProject.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController()
                    .navigate(TaskFragmentDirections.actionTaskFragmentToGanttFragment(it.toTypedArray(),it.lastIndex))
            }
        })

        viewModel.newTask.observe(viewLifecycleOwner, Observer {
            colorAdaptor.notifyDataSetChanged()
            Log.d("chenyjzn","new task = $it")
        })

        return binding.root
    }

}