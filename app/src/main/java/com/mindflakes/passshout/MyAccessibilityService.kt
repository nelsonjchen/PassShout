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
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        Log.v(TAG, "EVENT!!! Type: ${event.eventType}" )
    }

}
