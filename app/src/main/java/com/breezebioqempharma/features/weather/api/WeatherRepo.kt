package com.breezebioqempharma.features.weather.api

import com.breezebioqempharma.base.BaseResponse
import com.breezebioqempharma.features.task.api.TaskApi
import com.breezebioqempharma.features.task.model.AddTaskInputModel
import com.breezebioqempharma.features.weather.model.ForeCastAPIResponse
import com.breezebioqempharma.features.weather.model.WeatherAPIResponse
import io.reactivex.Observable

class WeatherRepo(val apiService: WeatherApi) {
    fun getCurrentWeather(zipCode: String): Observable<WeatherAPIResponse> {
        return apiService.getTodayWeather(zipCode)
    }

    fun getWeatherForecast(zipCode: String): Observable<ForeCastAPIResponse> {
        return apiService.getForecast(zipCode)
    }
}