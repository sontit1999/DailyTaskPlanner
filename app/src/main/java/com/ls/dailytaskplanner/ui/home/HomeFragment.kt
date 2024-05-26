package com.ls.dailytaskplanner.ui.home

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.ls.dailytaskplanner.R
import com.ls.dailytaskplanner.adapter.CustomItemTouchHelperCallback
import com.ls.dailytaskplanner.ads.AdManager
import com.ls.dailytaskplanner.base.BaseFragment
import com.ls.dailytaskplanner.custom.ViewContainer
import com.ls.dailytaskplanner.custom.WeekDay
import com.ls.dailytaskplanner.custom.WeekDayBinder
import com.ls.dailytaskplanner.custom.atStartOfMonth
import com.ls.dailytaskplanner.custom.displayText
import com.ls.dailytaskplanner.custom.firstDayOfWeekFromLocale
import com.ls.dailytaskplanner.custom.getWeekPageTitle
import com.ls.dailytaskplanner.databinding.Example7CalendarDayBinding
import com.ls.dailytaskplanner.databinding.FragHomeBinding
import com.ls.dailytaskplanner.model.eventbus.RefreshDataTask
import com.ls.dailytaskplanner.ui.dialog.AddTaskDialog
import com.ls.dailytaskplanner.ui.home.adapter.TaskAdapter
import com.ls.dailytaskplanner.utils.AppUtils
import com.ls.dailytaskplanner.utils.MediaPlayerManager
import com.ls.dailytaskplanner.utils.setSafeOnClickListener
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragHomeBinding, HomeViewModel>() {

    private val viewModel: HomeViewModel by viewModels()
    lateinit var adapterTask: TaskAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    private var selectedDate = LocalDate.now()

    private var selectedDateString = AppUtils.getCurrentDate()

    @RequiresApi(Build.VERSION_CODES.O)
    private val dateFormatter = DateTimeFormatter.ofPattern("dd")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun getLayoutId() = R.layout.frag_home

    @RequiresApi(Build.VERSION_CODES.O)
    override fun viewCreated() {
        initRvTask()
        initCalender()
        viewModel.getTaskByDate(AppUtils.getCurrentDate(),true)
        AdManager.loadNativeAddTask()
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
                selectedDateString = it.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                viewModel.getTaskByDate(selectedDateString)
            }
        )
        binding.calendarView.scrollToDate(LocalDate.now())
    }

    override fun observersSomething() {
        viewModel.listTaskLiveData.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                showEmptyView(true)
            } else {
                showEmptyView(false)
            }
            adapterTask.submitList(it)
        }

        viewModel.showToastLiveData.observe(viewLifecycleOwner) {
            showToast(it)
        }

        viewModel.statusDoneAllTaskLiveData.observe(viewLifecycleOwner) {
            if(it) {
                Toast.makeText(context, getString(R.string.congratulation_done_task), Toast.LENGTH_SHORT).show()
                binding.lavCongratulation.isVisible = true
                binding.lavCongratulation.playAnimation()
                MediaPlayerManager.stopPlaying()
                MediaPlayerManager.playRawFile(R.raw.congratulation_sound)
            }
        }
    }

    private fun showEmptyView(isShown: Boolean = true) {
        if(isShown) {
            binding.emptyView.visibility = View.VISIBLE
            binding.lavEmpty.visibility = View.VISIBLE
            binding.lavEmpty.playAnimation()
        } else {
            binding.emptyView.visibility = View.GONE
            binding.lavEmpty.visibility = View.GONE
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun bindingAction() {
        binding.btnAdd.setSafeOnClickListener {
            AddTaskDialog.newInstance(null,selectedDateString).show(childFragmentManager, AddTaskDialog.TAG)
        }

        binding.tvToday.setSafeOnClickListener {
            binding.calendarView.smoothScrollToDate(LocalDate.now())
        }
    }

    private fun initRvTask() {
        val callback = CustomItemTouchHelperCallback(object :
            CustomItemTouchHelperCallback.ItemTouchHelperListener {
            override fun onItemMove(fromPosition: Int, toPosition: Int) {
                // Handle item move event
                adapterTask.notifyItemMoved(fromPosition, toPosition)
            }

            override fun onItemSwipe(position: Int) {
                // Handle item swipe event
                adapterTask.removeItem(position)
                viewModel.deleteTask(adapterTask.currentList[position])
            }
        })
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.rvTask)
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

    @Subscribe
    fun refreshData(event: RefreshDataTask) {
        viewModel.getTaskByDate(selectedDateString)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}