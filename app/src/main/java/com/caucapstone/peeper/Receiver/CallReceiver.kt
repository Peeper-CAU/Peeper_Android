package com.caucapstone.peeper.Receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log

class CallReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent != null) {
            val intentAction = intent.action
            if(intentAction.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
                val intentState = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
                if(intentState == TelephonyManager.EXTRA_STATE_RINGING) {
                    Log.i("CallReeiver", "Call Incoming!!")
                } else if(intentState == TelephonyManager.EXTRA_STATE_OFFHOOK) {
                    Log.i("CallReeiver", "Call Ongoing!!")
                } else if(intentState == TelephonyManager.EXTRA_STATE_IDLE) {
                    Log.i("CallReeiver", "Call Ended!!")
                }
            }
        }
    }
}