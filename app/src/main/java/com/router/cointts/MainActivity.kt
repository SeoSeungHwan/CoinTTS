package com.router.cointts

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder

import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    //Viewmodel과 코인목록 ArrayList
    private val viewModel: MainViewModel by viewModels()
    private var coinListArray = java.util.ArrayList<String>()
    private var coinKoreanNameArray = java.util.ArrayList<String>()

    //Service 를 담을 객체와 연결확인하는 변수
    private lateinit var myService : MyService
    private var isBound = false

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            val binder = p1 as MyService.MyLocalBinder
            myService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isBound = false
        }
    }

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




        //시작버튼을 누르면 서비스 실행(코인이름과 반복시간 bind로 전달)
        start_btn.setOnClickListener {
            var intent = Intent(this,MyService::class.java)
            intent.putExtra("coinName",coinList_spinner.selectedItemPosition)
            intent.putExtra("repeatTime",sec_spinner.selectedItem.toString())
            intent.putExtra("coinListArray" , coinListArray)
            bindService(intent,connection, Context.BIND_AUTO_CREATE)

            Toast.makeText(this,coinKoreanNameArray.get(coinList_spinner.selectedItemPosition)+
                    "알림을"+
                    sec_spinner.selectedItem.toString()+
                    "초뒤에 시작합니다.",
                Toast.LENGTH_SHORT).show()
        }

        //일시정지버튼을 누르면 서비스 종료
        pause_btn.setOnClickListener {

            Toast.makeText(this,"알림을 종료합니다.",Toast.LENGTH_SHORT).show()
            unbindService(connection)
            isBound=false
        }

    }
}

