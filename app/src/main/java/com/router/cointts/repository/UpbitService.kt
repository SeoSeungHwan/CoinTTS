package com.router.cointts.repository

import com.router.cointts.model.CoinInfo
import com.router.cointts.model.CoinList
import retrofit2.http.GET
import retrofit2.http.Query

interface UpbitService {
    companion object{
        const val BASE_URL = "https://api.upbit.com/v1/"
    }

    @GET("market/all")
    suspend fun getAllCoinList() : List<CoinList>

    @GET("ticker")
    suspend fun getCoinInfo(
        @Query("markets") markets: String
    ) : List<CoinInfo>

}