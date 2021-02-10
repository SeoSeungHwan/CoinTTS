package com.router.cointts

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.router.cointts.model.CoinInfo
import com.router.cointts.model.CoinList
import com.router.cointts.repository.UpbitService
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MainViewModel : ViewModel() {

    val coinListLiveData = MutableLiveData<List<CoinList>>()
    val coinPriceLiveData = MutableLiveData<List<CoinInfo>>()

    private val upbitService : UpbitService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(UpbitService.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        upbitService = retrofit.create(UpbitService::class.java)
    }

    fun fetchCoinInfo(){
        viewModelScope.launch {
            coinListLiveData.value = upbitService.getAllCoinList()
        }
    }

    fun fetchCoinPrice(coinName: String){
        viewModelScope.launch {
                coinPriceLiveData.value = upbitService.getCoinInfo(coinName)

        }
    }

}