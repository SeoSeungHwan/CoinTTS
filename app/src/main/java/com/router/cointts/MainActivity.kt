package com.router.cointts

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_main.*
import org.w3c.dom.Text
import java.lang.Exception
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {


    private val viewModel: MainViewModel by viewModels()
    private var coinListArray = java.util.ArrayList<String>()
    private var coinKoreanNameArray = java.util.ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //Adview 관련
        MobileAds.initialize(this, getString(R.string.admob_app_id))
        adView.loadAd(AdRequest.Builder().build())

        //반복 시간 Spinner관련
        val items = resources.getStringArray(R.array.sec_array)
        val sec_spinner_adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this@MainActivity,
            android.R.layout.simple_spinner_item,
            items
        )
        sec_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sec_spinner.adapter = sec_spinner_adapter

        //코인목록 Upbit에서 가져와서 Spinner에 표시
        coinList_spinner.setTitle("Select Coin(업비트 원화마켓)")
        coinList_spinner.setPositiveButton("OK")

        viewModel.apply {
            fetchCoinInfo()
            coinListLiveData.observe(this@MainActivity, Observer {
                coinListArray.clear()
                it.forEach {
                    if (it.market.substring(0, 3).equals("KRW")) {
                        coinListArray.add(it.market)
                        coinKoreanNameArray.add(it.korean_name)
                    }
                }
                val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                    this@MainActivity,
                    android.R.layout.simple_spinner_item, coinKoreanNameArray
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                coinList_spinner.adapter = adapter
            })
        }



        //handler를 사용하여 코인의 가격을 읽어오고 읽어온가격을 TTS로 변환

        var coinPrice: String? = null
        val decimalFormat = DecimalFormat("#.##")
        fun getCoinPrice() {
            viewModel.apply {
                fetchCoinPrice(coinListArray.get(coinList_spinner.selectedItemPosition))
                coinPriceLiveData.observe(this@MainActivity, Observer {
                    coinPrice = decimalFormat.format(it.get(0).trade_price.toDouble())+"원"

                })
            }
        }



        val handler = Handler()
        var repeatTime = (sec_spinner.selectedItem.toString().toInt()* 1000).toLong()
        val handlerTask = object : Runnable {
            override fun run() {
                repeatTime = (sec_spinner.selectedItem.toString().toInt()* 1000).toLong()
                getCoinPrice()
                tts?.speak(
                    coinPrice,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    null
                )
                price_tv.text = coinPrice
                handler.postDelayed(this, repeatTime)
            }
        }

        start_btn.setOnClickListener {
            ttsInit()
            handler.post(handlerTask)
            Toast.makeText(this,coinList_spinner.selectedItem.toString()+"알림을"+repeatTime/1000+"초마다 반복합니다.",Toast.LENGTH_SHORT).show()
            startService(Intent(applicationContext,MyService::class.java))
        }
        pause_btn.setOnClickListener {
            handler.removeCallbacks(handlerTask)
            Toast.makeText(this,"알림을 종료합니다.",Toast.LENGTH_SHORT).show()
            stopService(Intent(applicationContext,MyService::class.java))
        }

    }
}

