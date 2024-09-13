package com.breezebioqempharma.features.weather.api

import com.breezebioqempharma.features.task.api.TaskApi
import com.breezebioqempharma.features.task.api.TaskRepo

object WeatherRepoProvider {
    fun weatherRepoProvider(): WeatherRepo {
        return WeatherRepo(WeatherApi.create())
    }
}