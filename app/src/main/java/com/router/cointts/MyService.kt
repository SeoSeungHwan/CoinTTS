package com.router.cointts


import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.lifecycle.LifecycleService
import java.util.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Runnable
import java.text.DecimalFormat

class MyService : LifecycleService() {

    private val viewModel: MainViewModel = MainViewModel()
    private var coinListArray = ArrayList<String>()
    private var coinName: Int = 0
    private var reapeatTime: String = ""
    private val myBinder: IBinder = MyLocalBinder()
    private val handler = Handler()
    private var handlerTask : Runnable? = null

    companion object {
        var tts: TextToSpeech? =null
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        coinName = intent.getIntExtra("coinName",0)
        reapeatTime = intent.getStringExtra("repeatTime").toString()
        coinListArray = intent.getStringArrayListExtra("coinListArray") as ArrayList<String>
        Log.d(TAG, "onBind: " + reapeatTime)

        var coinPrice: String? = null
        val decimalFormat = DecimalFormat("#.##")
        fun getCoinPrice() {
            viewModel.apply {
                fetchCoinPrice(coinListArray.get(coinName))
                coinPriceLiveData.observe(this@MyService, {
                    coinPrice = decimalFormat.format(it.get(0).trade_price.toDouble()) + "원"

                })
            }
        }

        var repeatTime = (reapeatTime.toInt() * 1000).toLong()
        handlerTask = object : Runnable {
            override fun run() {
                repeatTime = (reapeatTime.toInt() * 1000).toLong()
                getCoinPrice()
                tts?.speak(
                    coinPrice,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    null
                )
                handler.postDelayed(this, repeatTime)
            }
        }
        Log.d(TAG, "onBind: ")
        handler.post(handlerTask as Runnable)



        return myBinder
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: ")

        //TTS Init
        tts = TextToSpeech(this) {
            if (it == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.KOREA)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    return@TextToSpeech
                }
            } else {
            }
        }
    }
    //Service를 종료할떄 handler 종료
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(handlerTask!!)
        Log.d(TAG, "onDestroy: ")
    }

    inner class MyLocalBinder: Binder() {
        fun getService() : MyService = this@MyService
    }
}


