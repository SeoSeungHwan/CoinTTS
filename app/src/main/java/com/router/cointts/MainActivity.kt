package com.router.cointts

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
import kotlinx.android.synthetic.main.activity_main.*
import org.w3c.dom.Text
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {

    private val viewModel : MainViewModel by viewModels()
    private var coinListArray = ArrayList<String>()
    private var coinKoreanNameArray = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
        coinList_spinner.setTitle("Select Coin")
        coinList_spinner.setPositiveButton("OK")

        viewModel.apply {
            fetchCoinInfo()
            coinListLiveData.observe(this@MainActivity, Observer {
                coinListArray.clear()
                it.forEach {
                    coinListArray.add(it.english_name)
                }
                val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                        this@MainActivity,
                        android.R.layout.simple_spinner_item, coinListArray
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                coinList_spinner.adapter = adapter
            })
        }



        //TTS Init
        var tts: TextToSpeech? = null
        tts = TextToSpeech(this@MainActivity){
            if(it==TextToSpeech.SUCCESS){
                val result = tts?.setLanguage(Locale.KOREA)
                if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                    Toast.makeText(this@MainActivity,"언어를 지원하지 않습니다.",Toast.LENGTH_SHORT).show()
                    return@TextToSpeech
                }
                Toast.makeText(this@MainActivity,"세팅 성공",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this@MainActivity,"세팅 실패",Toast.LENGTH_SHORT).show()
            }
        }

        //handler를 사용하여 코인의 가격을 읽어오고 읽어온가격을 TTS로 변환

        val handler = Handler()
        val repeatTime = 4000L
        //val repeatTime = sec_spinner.selectedItem.toString().toLong()*10000
        val handlerTask = object : Runnable{
            override fun run() {

                viewModel.apply {
                    fetchCoinPrice(coinListArray.get(coinList_spinner.selectedItemPosition))
                    coinPriceLiveData.observe(this@MainActivity, Observer {
                        Log.d("test", "onCreate: "+it.get(0).market)
                        Log.d("test", "onCreate: "+it.get(0).trade_price)
                        tts?.speak(it.get(0).trade_price,TextToSpeech.QUEUE_ADD,null,null)
                    })
                }


                handler.postDelayed(this,repeatTime)
            }
        }

        start_btn.setOnClickListener {
            handler.post(handlerTask)
        }
        pause_btn.setOnClickListener {
            handler.removeCallbacks(handlerTask)
        }

    }
}
