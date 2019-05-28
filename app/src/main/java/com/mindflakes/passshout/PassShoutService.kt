package com.mindflakes.passshout

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.media.AudioAttributesCompat
import androidx.media.AudioFocusRequestCompat
import androidx.media.AudioManagerCompat

class PassShoutService : AccessibilityService(), TextToSpeech.OnInitListener {

    val TAG = "PassShoutService"
    var lastBarCode = ""
    private lateinit var mTextToSpeech: TextToSpeech
    private lateinit var audioManager: AudioManager

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
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

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

        val titleChild = source.getChild(0)
        if (titleChild.viewIdResourceName != "com.eventbrite.organizer:id/scanner_status_info_title") {
            titleChild.recycle()
            source.recycle()
            return
        }

        Log.i(TAG, "Scanner Status Info Panel Detected")
        val scannerStatusInfoTitle = titleChild.text

        if (scannerStatusInfoTitle == "Already checked in") {
            Log.i(TAG, "Already Checked In")
            titleChild.recycle()
            source.recycle()
            return
        }
        titleChild.recycle()

        Log.i(TAG, "Code not already Checked In")

        val barcodeChild = source.getChild(3)
        val scannerBarCode = barcodeChild.text.toString()

        if (scannerBarCode == lastBarCode) {
            Log.i(TAG, "Debounce Barcode")
            barcodeChild.recycle()
            source.recycle()
            return
        }
        lastBarCode = scannerBarCode
        barcodeChild.recycle()
        Log.i(TAG, "Scanner Barcode: $scannerBarCode")

        val ticketTypeChild = source.getChild(2)
        val scannerTicketType = ticketTypeChild.text
        Log.i(TAG, "Ticket Type: $scannerTicketType")
        ticketTypeChild.recycle()

        val focusRequest = AudioFocusRequestCompat.Builder(AudioManagerCompat.AUDIOFOCUS_GAIN_TRANSIENT).run {
            setOnAudioFocusChangeListener {
                // I'm just here so I don't get fined.
            }
            build()
        }

        mTextToSpeech.setOnUtteranceProgressListener(
            object: UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    // Nothing
                }

                override fun onError(utteranceId: String?) {
                    // Nothing //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDone(utteranceId: String?) {
                    AudioManagerCompat.abandonAudioFocusRequest(
                        audioManager, focusRequest)
                }

            }
        )

        val requestAudioFocusResult = AudioManagerCompat.requestAudioFocus(audioManager, focusRequest)
        if (requestAudioFocusResult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mTextToSpeech.speak(scannerTicketType, TextToSpeech.QUEUE_ADD, null, scannerBarCode)
            Log.i(TAG, "Spoke $scannerTicketType")
        } else {
            Log.i(TAG, "Could not speak $scannerTicketType")
        }


        source.recycle()
    }
}
