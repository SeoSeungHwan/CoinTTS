package com.router.cointts


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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

                //notification알람으로 상단알림으로 가격 전달
               createNotificationChannel(this, NotificationManagerCompat.IMPORTANCE_DEFAULT,
                    false, getString(R.string.app_name), "App notification channel") // 1

                val channelId = "coinTTS"
                val title = coinListArray.get(coinName)
                val content = coinPrice+"원"

                val intent = Intent(baseContext, MyService::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                val pendingIntent = PendingIntent.getActivity(baseContext, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT)    // 3

                val builder = NotificationCompat.Builder(this@MyService, channelId)  // 4
                builder.setSmallIcon(R.drawable.icon)    // 5
                builder.setContentTitle(title)    // 6
                builder.setContentText(content)    // 7
                builder.priority = NotificationCompat.PRIORITY_DEFAULT    // 8
                builder.setAutoCancel(true)   // 9
                builder.setContentIntent(pendingIntent)   // 10

                val notificationManager = NotificationManagerCompat.from(this@MyService)
                notificationManager.notify(1001, builder.build())
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

    //notification
    private fun createNotificationChannel(
        param: Runnable,
        importanceDefault: Int,
        b: Boolean,
        string: String,
        s1: String
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "coinTTS"
            val descriptionText = "coinTTS"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("coinTTS", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}


