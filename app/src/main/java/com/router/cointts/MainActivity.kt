package com.router.cointts

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val viewModel : MainViewModel by viewModels()
    private var coinListArray = ArrayList<String>()
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

        //Viewmodel을 사용하여
        viewModel.fetchCoinInfo()
        viewModel.apply {
            coinListLiveData.observe(this@MainActivity, Observer {

                it.forEach {
                    coinListArray.add(it.korean_name)
                }
                val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                    this@MainActivity,
                    android.R.layout.simple_spinner_item, coinListArray
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                coinList_spinner.adapter = adapter
            })
        }


        coinList_spinner.setTitle("Select Coin")
        coinList_spinner.setPositiveButton("OK")
        
    }
}
