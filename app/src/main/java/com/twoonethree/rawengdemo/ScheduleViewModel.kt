package com.twoonethree.rawengdemo

import GameSchedule
import Team
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class ScheduleViewModel(val repository: SheduleRepository) : ViewModel() {
    private val _schedule = MutableStateFlow<List<GameSchedule>>(emptyList())
    val schedule: StateFlow<List<GameSchedule>> = _schedule

    private val _teams = MutableStateFlow<List<Team>>(emptyList())
    val teams: StateFlow<List<Team>> = _teams

    init {
        fetchScheduleData()
    }

    private fun fetchScheduleData() {
        viewModelScope.launch {
            _schedule.value = repository.parseScheduleJson().reversed()
            _teams.value = repository.parseTeamsJson()
        }
    }
}
