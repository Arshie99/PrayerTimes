package com.example.prayertimes


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.widget.Toast


/** * Receiver */

class AlarmReceiver : BroadcastReceiver() {
    var mp: MediaPlayer? = null
    override fun onReceive(context: Context?, intent: Intent?) {
        mp = MediaPlayer.create(context, R.raw.azhan)
        mp!!.start()
        Toast.makeText(context, "Alarm....", Toast.LENGTH_LONG).show()
    }
}

