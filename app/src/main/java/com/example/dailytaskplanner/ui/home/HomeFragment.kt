package com.example.dailytaskplanner.ui.home

import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailytaskplanner.R
import com.example.dailytaskplanner.base.BaseFragment
import com.example.dailytaskplanner.custom.ViewContainer
import com.example.dailytaskplanner.custom.WeekDay
import com.example.dailytaskplanner.custom.WeekDayBinder
import com.example.dailytaskplanner.custom.atStartOfMonth
import com.example.dailytaskplanner.custom.displayText
import com.example.dailytaskplanner.custom.firstDayOfWeekFromLocale
import com.example.dailytaskplanner.custom.getWeekPageTitle
import com.example.dailytaskplanner.databinding.Example7CalendarDayBinding
import com.example.dailytaskplanner.databinding.FragHomeBinding
import com.example.dailytaskplanner.ui.dialog.AddTaskDialog
import com.example.dailytaskplanner.ui.home.adapter.TaskAdapter
import com.example.dailytaskplanner.utils.AppUtils
import com.example.dailytaskplanner.utils.setSafeOnClickListener
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragHomeBinding, HomeViewModel>() {

    private val viewModel: HomeViewModel by viewModels()
    lateinit var adapterTask: TaskAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    private var selectedDate = LocalDate.now()

    @RequiresApi(Build.VERSION_CODES.O)
    private val dateFormatter = DateTimeFormatter.ofPattern("dd")

    override fun getLayoutId() = R.layout.frag_home

    @RequiresApi(Build.VERSION_CODES.O)
    override fun viewCreated() {
        initRvTask()
        viewModel.getAllTasks()
        initCalender()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initCalender() {
        class DayViewContainer(view: View) : ViewContainer(view) {
            val bind = Example7CalendarDayBinding.bind(view)
            lateinit var day: WeekDay

            init {
                view.setOnClickListener {
                    if (selectedDate != day.date) {
                        val oldDate = selectedDate
                        selectedDate = day.date
                        binding.calendarView.notifyDateChanged(day.date)
                        oldDate?.let { binding.calendarView.notifyDateChanged(it) }
                    }
                }
            }

            @RequiresApi(Build.VERSION_CODES.O)
            fun bind(day: WeekDay) {
                this.day = day
                bind.exSevenDateText.text = dateFormatter.format(day.date)
                bind.exSevenDayText.text = day.date.dayOfWeek.displayText()

                val colorRes = if (day.date == selectedDate) {
                    R.color.example_7_yellow
                } else {
                    R.color.example_7_white
                }
                bind.exSevenDateText.setTextColor(view.resources.getColor(colorRes, null))
                bind.exSevenSelectedView.isVisible = day.date == selectedDate
            }
        }

        binding.calendarView.dayBinder = object : WeekDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)

            @RequiresApi(Build.VERSION_CODES.O)
            override fun bind(container: DayViewContainer, data: WeekDay) {
                container.bind(data)
            }
        }

        binding.calendarView.weekScrollListener = { weekDays ->
            binding.tvMonthYear.text = getWeekPageTitle(weekDays)
        }

        val currentMonth = YearMonth.now()
        binding.calendarView.setup(
            currentMonth.minusMonths(5).atStartOfMonth(),
            currentMonth.plusMonths(5).atEndOfMonth(),
            firstDayOfWeekFromLocale(),
            onClickListener = {
                Toast.makeText(context, AppUtils.formatDate(it.date.toString()), Toast.LENGTH_SHORT).show()
            }
        )
       binding.calendarView.scrollToDate(LocalDate.now())
    }

    override fun observersSomething() {
        viewModel.listTaskLiveData.observe(viewLifecycleOwner) {
            adapterTask.submitList(it)
        }
    }

    override fun bindingAction() {
        binding.btnAdd.setSafeOnClickListener {
            AddTaskDialog.newInstance(null).show(childFragmentManager, AddTaskDialog.TAG)
        }
    }

    private fun initRvTask() {
        adapterTask = TaskAdapter()
        adapterTask.onClickItem = {
            AddTaskDialog.newInstance(it).show(childFragmentManager, AddTaskDialog.TAG)
        }
        adapterTask.onClickCheckbox = {
            it.isCompleted = !it.isCompleted
            viewModel.updateTask(it)
        }
        binding.rvTask.adapter = adapterTask
        binding.rvTask.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

}