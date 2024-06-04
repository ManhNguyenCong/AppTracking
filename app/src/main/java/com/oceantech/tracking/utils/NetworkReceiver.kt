package com.oceantech.tracking.utils

import android.app.AlertDialog.Builder
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.work.impl.utils.ForceStopRunnable.BroadcastReceiver
import kotlin.coroutines.coroutineContext

class NetworkReceiver(context: Context): BroadcastReceiver() {

    private var isStarted = true

    private val dialogBuilder by lazy {
        Builder(context)
            .setTitle("Internet Connection Error")
            .setMessage("Your internet is disconnected. Please, check your internet!")
            .create()
    }

    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("Test Tracking", "onReceive: in receiver")

        val noConnection = intent?.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)

        if (isStarted && noConnection == false) {
            isStarted = false
            return
        }

        intent?.let {
            if (noConnection == true) {
                dialogBuilder.show()
//                Toast.makeText(context, "Internet is disconnected!!!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Internet is restored!!!", Toast.LENGTH_SHORT).show()
                dialogBuilder.cancel()
            }
        }
    }
}