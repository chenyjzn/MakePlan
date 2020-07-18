package com.yuchen.makeplan.multitask


import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import com.yuchen.makeplan.R
import com.yuchen.makeplan.databinding.FragmentMultiTaskBinding
import com.yuchen.makeplan.ext.getVmFactory
import java.util.*

class MultiTaskFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentMultiTaskBinding.inflate(inflater, container, false)

        val viewModel: MultiTaskViewModel by viewModels<MultiTaskViewModel> {
            getVmFactory(MultiTaskFragmentArgs.fromBundle(requireArguments()).multiProject, MultiTaskFragmentArgs.fromBundle(requireArguments()).taskPos, resources.getStringArray(R.array.color_array).toList()) }
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        val colorAdaptor = MultiTaskColorAdapter(viewModel)
        binding.multiTaskColorRecycler.adapter = colorAdaptor
        binding.multiTaskColorRecycler.layoutManager =
            GridLayoutManager(binding.multiTaskColorRecycler.context, 6)
        val calendar = Calendar.getInstance()

        binding.multiTaskStartDateEdit.setOnClickListener {
            calendar.timeInMillis = viewModel.newStartTimeMillis.value ?: calendar.timeInMillis
            val startDatePickerDialog = DatePickerDialog(
                requireNotNull(context),
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

        binding.multiTaskStartTimeEdit.setOnClickListener {
            calendar.timeInMillis = viewModel.newStartTimeMillis.value ?: calendar.timeInMillis
            val startTimePickerDialog = TimePickerDialog(
                requireNotNull(context),
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

        binding.multiTaskEndDateEdit.setOnClickListener {
            calendar.timeInMillis = viewModel.newEndTimeMillis.value ?: calendar.timeInMillis
            val endDatePickerDialog = DatePickerDialog(
                requireNotNull(context),
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
            }
            endDatePickerDialog.show()
        }

        binding.multiTaskEndTimeEdit.setOnClickListener {
            calendar.timeInMillis = viewModel.newEndTimeMillis.value ?: calendar.timeInMillis
            val endTimePickerDialog = TimePickerDialog(
                requireNotNull(context),
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

        viewModel.saveProjectDone.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController().navigate(MultiTaskFragmentDirections.actionMultiTaskFragmentToMultiGanttFragment(it))
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

        binding.multiTaskDurationHourEdit.addTextChangedListener(object : TextWatcher {
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

        binding.multiTaskDurationMinuteEdit.addTextChangedListener(object : TextWatcher {
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

        binding.multiTaskCancel.setOnClickListener {
            this.findNavController().popBackStack()
        }

        binding.multiTaskSave.setOnClickListener {
            viewModel.addTaskToNewProject()
        }
        return binding.root
    }

}
