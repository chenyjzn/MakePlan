package com.yuchen.makeplan.task

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.yuchen.makeplan.DAY_MILLIS
import com.yuchen.makeplan.R
import com.yuchen.makeplan.databinding.FragmentTaskBinding
import com.yuchen.makeplan.ext.getVmFactory
import java.util.*


class TaskFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentTaskBinding.inflate(inflater, container, false)
        val viewModel: TaskViewModel by viewModels<TaskViewModel> { getVmFactory(TaskFragmentArgs.fromBundle(requireArguments()).projectHistory, TaskFragmentArgs.fromBundle(requireArguments()).projectHistoryPos,resources.getStringArray(R.array.color_array_1).toList())}
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        val colorAdaptor = TaskColorAdapter(viewModel)
        binding.taskColorRecycler.adapter = colorAdaptor
        binding.taskColorRecycler.layoutManager = GridLayoutManager(binding.taskColorRecycler.context, 5)
        val calendar = Calendar.getInstance()
        binding.taskStartDateEdit.setOnClickListener {
            calendar.timeInMillis = viewModel.newStartTimeMillis.value ?: calendar.timeInMillis
            val startDatePickerDialog = DatePickerDialog(
                requireNotNull(context),
                R.style.datepicker,
                object : DatePickerDialog.OnDateSetListener {
                    override fun onDateSet(
                        view: DatePicker?,
                        year: Int,
                        month: Int,
                        dayOfMonth: Int
                    ) {
                        viewModel.setStartDate(year, month, dayOfMonth)
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            startDatePickerDialog.show()
        }

        binding.taskStartTimeEdit.setOnClickListener {
            calendar.timeInMillis = viewModel.newStartTimeMillis.value ?: calendar.timeInMillis
            val startTimePickerDialog = TimePickerDialog(
                requireNotNull(context),
                R.style.datepicker,
                object : TimePickerDialog.OnTimeSetListener {
                    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                        viewModel.setStartTime(hourOfDay, minute)
                    }
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
            )
            startTimePickerDialog.show()
        }

        binding.taskEndDateEdit.setOnClickListener {
            calendar.timeInMillis = viewModel.newEndTimeMillis.value ?: calendar.timeInMillis
            val endDatePickerDialog = DatePickerDialog(
                requireNotNull(context),
                R.style.datepicker,
                object : DatePickerDialog.OnDateSetListener {
                    override fun onDateSet(
                        view: DatePicker?,
                        year: Int,
                        month: Int,
                        dayOfMonth: Int
                    ) {
                        viewModel.setEndDate(year, month, dayOfMonth)
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            viewModel.newStartTimeMillis.value?.let {
                endDatePickerDialog.datePicker.minDate = it
                endDatePickerDialog.datePicker.maxDate = it + 999 * DAY_MILLIS
            }
            endDatePickerDialog.show()
        }

        binding.taskEndTimeEdit.setOnClickListener {
            calendar.timeInMillis = viewModel.newEndTimeMillis.value ?: calendar.timeInMillis
            val endTimePickerDialog = TimePickerDialog(
                requireNotNull(context),
                R.style.datepicker,
                object : TimePickerDialog.OnTimeSetListener {
                    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                        viewModel.setEndTime(hourOfDay, minute)
                    }
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
            )
            endTimePickerDialog.show()
        }

        viewModel.newProject.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController().navigate(
                    TaskFragmentDirections.actionTaskFragmentToGanttFragment(
                        it.toTypedArray(),
                        it.lastIndex
                    )
                )
            }
        })

        binding.taskDurationDayEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val words = s.toString()
                if (words != "") {
                    val num = Integer.parseInt(words)
                    Log.d("chenyjzn", "edit day = $num")
                    if (num in 0..999) {
                        viewModel.setEndByDurationDay(num)
                    } else if (num > 999) {
                        viewModel.setEndByDurationDay(999)
                    }
                } else {
                    viewModel.setEndByDurationDay(0)
                }
            }
        })

        binding.taskDurationHourEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val words = s.toString()
                if (words != "") {
                    val num = Integer.parseInt(words)
                    Log.d("chenyjzn", "edit day = $num")
                    if (num in 0..23) {
                        viewModel.setEndByDurationHour(num)
                    } else {
                        viewModel.setEndByDurationHour(23)
                    }
                } else {
                    viewModel.setEndByDurationHour(0)
                }
            }
        })

        binding.taskDurationMinuteEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val words = s.toString()
                if (words != "") {
                    val num = Integer.parseInt(words)
                    Log.d("chenyjzn", "edit day = $num")
                    if (num in 0..59) {
                        viewModel.setEndByDurationMinute(num)
                    } else {
                        viewModel.setEndByDurationMinute(59)
                    }
                } else {
                    viewModel.setEndByDurationMinute(0)
                }
            }
        })

        binding.taskCancel.setOnClickListener {
            this.findNavController().popBackStack()
        }

        binding.taskSave.setOnClickListener {
            viewModel.addTaskToNewProject()
        }
        return binding.root
    }
}