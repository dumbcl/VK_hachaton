package ru.ok.android.itmohack2023.retrofit

import io.reactivex.rxjava3.core.Single
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET
import ru.ok.android.itmohack2023.retrofit.dto.CatFact

interface CatsApi {

    @GET("fact")
    fun getFactRx(): Single<CatFact>

    @GET("fact")
    fun getFactCall(): Call<CatFact>

    companion object {
        fun provideCatApi(retrofit: Retrofit): CatsApi {
            return retrofit.create(CatsApi::class.java)
        }
    }

}