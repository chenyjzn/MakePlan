package com.yuchen.makeplan.multitask

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.yuchen.makeplan.*
import com.yuchen.makeplan.databinding.FragmentMultiTaskBinding
import com.yuchen.makeplan.ext.getVmFactory
import java.util.*

class MultiTaskFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentMultiTaskBinding.inflate(inflater, container, false)
        val viewModel: MultiTaskViewModel by viewModels<MultiTaskViewModel> {
            getVmFactory(
                MultiTaskFragmentArgs.fromBundle(requireArguments()).project,
                MultiTaskFragmentArgs.fromBundle(requireArguments()).task
            )
        }
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        val colorAdaptor = MultiTaskColorAdapter(viewModel)
        binding.multiTaskColorRecycler.adapter = colorAdaptor
        binding.multiTaskColorRecycler.layoutManager = GridLayoutManager(binding.multiTaskColorRecycler.context, 5)
        val calendar = Calendar.getInstance()

        binding.multiTaskStartDateEdit.setOnClickListener {
            calendar.timeInMillis = viewModel.newStartTimeMillis.value ?: calendar.timeInMillis
            val startDatePickerDialog = DatePickerDialog(requireNotNull(context),
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth -> viewModel.setStartDate(year, month, dayOfMonth) },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            startDatePickerDialog.show()
        }

        viewModel.loadingStatus.observe(viewLifecycleOwner, Observer {
            when (it) {
                is LoadingStatus.LOADING -> {
                    binding.multiTaskProgress.visibility = View.VISIBLE
                    binding.multiTaskCancel.isClickable = false
                    binding.multiTaskSave.isClickable = false
                }
                is LoadingStatus.DONE -> {
                    binding.multiTaskProgress.visibility = View.INVISIBLE
                    binding.multiTaskCancel.isClickable = true
                    binding.multiTaskSave.isClickable = true
                }
                is LoadingStatus.ERROR -> {
                    (activity as MainActivity).showErrorMessage(it.message)
                }
            }
        })

        binding.multiTaskStartTimeEdit.setOnClickListener {
            calendar.timeInMillis = viewModel.newStartTimeMillis.value ?: calendar.timeInMillis
            val startTimePickerDialog = TimePickerDialog(
                requireNotNull(context),
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute -> viewModel.setStartTime(hourOfDay, minute) },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
            )
            startTimePickerDialog.show()
        }

        binding.multiTaskEndDateEdit.setOnClickListener {
            calendar.timeInMillis = viewModel.newEndTimeMillis.value ?: calendar.timeInMillis
            val endDatePickerDialog = DatePickerDialog(
                requireNotNull(context),
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth -> viewModel.setEndDate(year, month, dayOfMonth) },
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

        binding.multiTaskEndTimeEdit.setOnClickListener {
            calendar.timeInMillis = viewModel.newEndTimeMillis.value ?: calendar.timeInMillis
            val endTimePickerDialog = TimePickerDialog(
                requireNotNull(context),
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute -> viewModel.setEndTime(hourOfDay, minute) },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
            )
            endTimePickerDialog.show()
        }

        viewModel.saveTask.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) {
                    this.findNavController().popBackStack()
                    viewModel.saveTaskDone()
                }
            }
        })

        binding.multiTaskDurationDayEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val words = s.toString()
                if (words != "") {
                    val num = Integer.parseInt(words)
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

        binding.multiTaskDurationHourEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val words = s.toString()
                if (words != "") {
                    val num = Integer.parseInt(words)
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

        binding.multiTaskDurationMinuteEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val words = s.toString()
                if (words != "") {
                    val num = Integer.parseInt(words)
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

        binding.multiTaskCancel.setOnClickListener {
            this.findNavController().popBackStack()
        }

        binding.multiTaskSave.setOnClickListener {
            viewModel.updateTaskToFirebase()
        }

        viewModel.newStartTimeMillis.observe(viewLifecycleOwner, Observer {
            it?.let {
                viewModel.setNewDuration()
            }
        })

        viewModel.newEndTimeMillis.observe(viewLifecycleOwner, Observer {
            it?.let {
                viewModel.setNewDuration()
            }
        })

        viewModel.newDuration.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it < 5L * MINUTE_MILLIS) {
                    binding.multiTaskSave.isClickable = false
                    binding.multiTaskDurationWarning.visibility = View.VISIBLE
                    binding.multiTaskDuration.setTextColor(resources.getColor(R.color.red_200))
                } else {
                    binding.multiTaskSave.isClickable = true
                    binding.multiTaskDurationWarning.visibility = View.INVISIBLE
                    binding.multiTaskDuration.setTextColor(resources.getColor(R.color.my_gray_180))
                }
            }
        })

        return binding.root
    }

}
