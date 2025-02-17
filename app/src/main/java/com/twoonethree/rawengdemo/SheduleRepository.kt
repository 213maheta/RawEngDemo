package com.twoonethree.rawengdemo

import GameSchedule
import Team
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SheduleRepository(val context: Context) {
    fun loadJsonFromAssets(context: Context, fileName: String): String {
        return context.assets.open(fileName).bufferedReader().use { it.readText() }
    }

    fun parseScheduleJson(): List<GameSchedule> {
        val json = loadJsonFromAssets(context, "Schedule.json")
        val schedules = Gson().fromJson<Map<String, Any>>(json, object : TypeToken<Map<String, Any>>() {}.type)
        val scheduleData = schedules["data"] as Map<*, *>
        val gamesList = scheduleData["schedules"] as List<Map<String, Any?>>
        return gamesList.map { GameSchedule.fromJson(it) }
    }

    fun parseTeamsJson(): List<Team> {
        val json = loadJsonFromAssets(context, "teams.json")
        val teams = Gson().fromJson<Map<String, Any>>(json, object : TypeToken<Map<String, Any>>() {}.type)
        val teamData = teams["data"] as Map<*, *>
        val teamsList = teamData["teams"] as List<Map<String, Any?>>
        return teamsList.map { Team.fromJson(it) }
    }

}