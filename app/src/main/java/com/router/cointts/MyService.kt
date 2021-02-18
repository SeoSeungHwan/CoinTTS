package com.router.cointts

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import java.util.*
import androidx.activity.viewModels
import kotlinx.android.synthetic.main.activity_main.*

class MyService : Service() {

    private val viewModel: MainViewModel by viewModels()
    private var coinListArray = ArrayList<String>()
    private var coinKoreanNameArray = ArrayList<String>()
    companion object{
        var tts: TextToSpeech? = null
    }
    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    //서비스에서 가장 먼저 호출된다(최초 한번만)
    override fun onCreate() {
        super.onCreate()

        //TTS Init
        tts = TextToSpeech(this) {
            if (it == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.KOREA)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    return@TextToSpeech
                }
            }else {
            }
        }


    }

    //서비스 호출시마다 실행
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)

    }
    //서비스가 종료될 때 실행
    override fun onDestroy() {
        super.onDestroy()
    }
}