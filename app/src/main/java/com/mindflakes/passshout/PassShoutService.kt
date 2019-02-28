package com.mindflakes.passshout

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class PassShoutService : AccessibilityService() {
    val TAG = "PassShoutService"
    var lastBarCode = ""

    override fun onServiceConnected() {
        Log.i(TAG, "Service Connected!!")
    }

    override fun onInterrupt() {
        Log.i(TAG, "Interrupt!!")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        Log.d(TAG, "Event Type: ${event.eventType}")
        val source: AccessibilityNodeInfo = event.source ?: return
        Log.d(TAG, "Got source")

        if (source.childCount != 5) {
            source.recycle()
            return
        }
        Log.i(TAG, "Source childCount is 5")

//        https://stackoverflow.com/questions/36793154/accessibilityservice-not-returning-view-ids
        source.refresh()
        Log.i(TAG, "Refresh Source Workaround")


        if (source.getChild(0).viewIdResourceName != "com.eventbrite.organizer:id/scanner_status_info_title") {
            source.recycle()
            return
        }
        Log.i(TAG, "Scanner Status Info Panel Detected")

        val scannerStatusInfoTitle = source.getChild(0).text

        if (scannerStatusInfoTitle == "Already checked in") {
            source.recycle()
            return
        }
        Log.i(TAG, "Code not already Checked In")

        val scannerBarCode = source.getChild(3).text.toString()

        if (scannerBarCode == lastBarCode) {
            source.recycle()
            return
        }
        lastBarCode = scannerBarCode
        Log.i(TAG, "Scanner Barcode: $scannerBarCode")

        val scannerTicketType = source.getChild(2).text
        Log.i(TAG, "Ticket Type: $scannerTicketType")
    }


}
