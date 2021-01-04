package com.example.lab2.fragments

import android.app.Service
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Binder
import android.os.CountDownTimer
import android.os.IBinder
import java.util.*

class BroadcastService : Service() {
    private lateinit var timer: CountDownTimer
    val COUNTDOWN_BR = "BroadcastService"
    var bi = Intent(COUNTDOWN_BR)
    var cdt: CountDownTimer? = null

    lateinit var timeList: ArrayList<Int>
    lateinit var phaseList: ArrayList<String>
    var currentIndex = 0

    inner class LocalBinder : Binder() {
        fun getService() : BroadcastService? {
            return this@BroadcastService
        }
    }

    override fun onStart(intent: Intent?, startId: Int) {
        timeList = intent!!.getIntegerArrayListExtra("timeList")!!
        phaseList = intent.getStringArrayListExtra("phaseList")!!
        startTimer()
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
        cdt!!.cancel()
        super.onDestroy()
    }

    override fun onBind(arg0: Intent?): IBinder? {
        return LocalBinder()
    }

    fun nextStep() {
        cdt?.cancel()
        if (currentIndex < timeList.size - 1)
            currentIndex += 1
        startTimer()
    }

    fun prevStep() {
        cdt?.cancel()
        if (currentIndex > 0)
            currentIndex -= 1
        startTimer()
    }

    fun stopTimer() {
        cdt?.cancel()
    }

    fun resumeTimer() {
        startTimer()
    }

    private fun startTimer() {
        if (currentIndex > timeList.size - 1)
            return
        cdt = object : CountDownTimer(timeList[currentIndex].toLong() * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeList[currentIndex] = millisUntilFinished.toInt() / 1000
                bi.putExtra("countdown", millisUntilFinished / 1000)
                bi.putExtra("phase", phaseList[currentIndex])
                sendBroadcast(bi)
            }

            override fun onFinish() {
                currentIndex += 1
                notifier()
                startTimer()
            }
        }.start()
    }

    private fun notifier() {
        val notify: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val r = RingtoneManager.getRingtone(this, notify)
        r.play()
    }
}