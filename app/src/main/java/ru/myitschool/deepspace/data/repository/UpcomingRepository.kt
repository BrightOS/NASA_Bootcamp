package ru.myitschool.deepspace.data.repository

import retrofit2.Response
import ru.myitschool.deepspace.data.dto.upcoming.UpcomingLaunch

interface UpcomingRepository {
    suspend fun getUpcomingLaunches(): Response<List<UpcomingLaunch>>
}