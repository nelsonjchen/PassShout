package com.mindflakes.passshout

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class PassShoutService : AccessibilityService() {
    val TAG = "PassShoutService"
    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.v(TAG, "Service Connected!!")
    }
    override fun onInterrupt() {
        Log.v(TAG, "Interrupt!!")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        Log.v(TAG, "EVENT!!! Type: ${event.eventType}" )
        val source: AccessibilityNodeInfo = event.source ?: return
        Log.v(TAG, "Got source" )

        source.refresh()
        Log.v(TAG, "Refresh Source")

        for (i in 0 until source.childCount){
            val child = source.getChild(i)
            child.refresh()
            Log.v(TAG, "${i}: resourceId: ${child.viewIdResourceName}, text: ${child.text}, childCount: ${child.childCount}")

        }
//
//        if (source.childCount != 3) {
//            Log.v(TAG, "Child Count was ${source.childCount}" )
//            source.recycle()
//            return
//        }
//        Log.v(TAG, "Child Count is 3" )
//
//        val panelNode = source.getChild(3) ?: run {
//            source.recycle()
//            return
//        }
//        Log.v(TAG, "Got third child" )
//
//        if (panelNode.viewIdResourceName != "com.eventbrite.organizer:id/scanner_status_panel") {
//            source.recycle()
//            return
//        }
//        Log.v(TAG, "Got scanner status panel" )
    }



}
