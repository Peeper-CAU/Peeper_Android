package com.caucapstone.peeper.Receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import com.caucapstone.peeper.Service.BluetoothService

class CallReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if(context != null && intent != null) {
            val intentAction = intent.action
            if(intentAction.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
                val intentState = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
                if(intentState == TelephonyManager.EXTRA_STATE_RINGING) {
                    Log.i("CallReeiver", "Call Incoming!!")
                } else if(intentState == TelephonyManager.EXTRA_STATE_OFFHOOK) {
                    Log.i("CallReeiver", "Call Ongoing!!")
//                    context.startService(Intent(context, BluetoothService::class.java))
                    Log.i("CallReeiver", "Started BluetoothService!!")
                } else if(intentState == TelephonyManager.EXTRA_STATE_IDLE) {
                    Log.i("CallReeiver", "Call Ended!!")
//                    context.stopService(Intent(context, BluetoothService::class.java))
                    Log.i("CallReeiver", "Stopped BluetoothService!!")
                }
            }
        }
    }
}