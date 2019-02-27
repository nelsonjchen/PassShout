package com.mindflakes.passshout

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class MyAccessibilityService : AccessibilityService() {
    val TAG = "MASSERVICE"
    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.v(TAG, "Service Connected!!")

    }
    override fun onInterrupt() {
        Log.v(TAG, "Interrupt!!")
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        Log.v(TAG, "EVENT!!!")
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
