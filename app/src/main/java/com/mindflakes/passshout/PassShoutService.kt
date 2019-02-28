package com.mindflakes.passshout

import android.accessibilityservice.AccessibilityService
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class PassShoutService : AccessibilityService(), TextToSpeech.OnInitListener {
    val TAG = "PassShoutService"
    var lastBarCode = ""
    private lateinit var mTextToSpeech: TextToSpeech

    override fun onInit(status: Int) {
    }

    override fun onDestroy() {
        // Shutdown TTS
        mTextToSpeech.stop()
        mTextToSpeech.shutdown()
        super.onDestroy()
    }

    override fun onServiceConnected() {
        Log.i(TAG, "Service Connected!!")
        mTextToSpeech = TextToSpeech(this, this)

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
        Log.d(TAG, "Source childCount is 5")

//        https://stackoverflow.com/questions/36793154/accessibilityservice-not-returning-view-ids
        source.refresh()
        Log.d(TAG, "Refresh Source Workaround")


        if (source.getChild(0).viewIdResourceName != "com.eventbrite.organizer:id/scanner_status_info_title") {
            source.recycle()
            return
        }
        Log.i(TAG, "Scanner Status Info Panel Detected")

        val scannerStatusInfoTitle = source.getChild(0).text

        if (scannerStatusInfoTitle == "Already checked in") {
            Log.i(TAG, "Already Checked In")

            source.recycle()
            return
        }
        Log.i(TAG, "Code not already Checked In")

        val scannerBarCode = source.getChild(3).text.toString()

        if (scannerBarCode == lastBarCode) {
            Log.i(TAG, "Debounce Barcode")
            source.recycle()
            return
        }
        lastBarCode = scannerBarCode
        Log.i(TAG, "Scanner Barcode: $scannerBarCode")

        val scannerTicketType = source.getChild(2).text
        Log.i(TAG, "Ticket Type: $scannerTicketType")
        mTextToSpeech.speak(scannerTicketType, TextToSpeech.QUEUE_ADD, null, scannerBarCode)
        Log.i(TAG, "Spoke $scannerTicketType")


        source.recycle()
    }


}
