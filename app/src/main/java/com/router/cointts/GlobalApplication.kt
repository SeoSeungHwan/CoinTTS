package com.router.cointts

import android.app.Application

class GlobalApplication : Application() {

     companion object{
         var isNotificationCheck = false
     }



    override fun onCreate() {
        super.onCreate()
    }

}