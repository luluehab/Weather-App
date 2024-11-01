package com.example.weatherapp.network

import com.example.weatherapp.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object APIClient {

    val apiRetrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    fun getApiService(): ApiServices {
         return apiRetrofit.create(ApiServices::class.java)
    }


}