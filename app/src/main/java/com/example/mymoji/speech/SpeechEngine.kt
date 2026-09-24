package com.example.mymoji.speech

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject

interface SpeechEngine {
    fun setLanguage(locale: Locale): Int
    fun speak(text: String): Int
    fun shutdown()
}

fun interface SpeechEngineFactory {
    fun create(onInit: (status: Int) -> Unit, onServiceError: () -> Unit): SpeechEngine
}

class TextToSpeechEngineFactory @Inject constructor(
    @ApplicationContext private val context: Context
) : SpeechEngineFactory {

    override fun create(onInit: (status: Int) -> Unit, onServiceError: () -> Unit): SpeechEngine {
        val textToSpeech = TextToSpeech(context) { status -> onInit(status) }
        textToSpeech.setOnUtteranceProgressListener(ServiceErrorListener(onServiceError))
        return TextToSpeechEngine(textToSpeech)
    }

    private class TextToSpeechEngine(private val textToSpeech: TextToSpeech) : SpeechEngine {
        override fun setLanguage(locale: Locale): Int = textToSpeech.setLanguage(locale)

        override fun speak(text: String): Int =
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, UTTERANCE_ID)

        override fun shutdown() {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }

    private class ServiceErrorListener(private val onServiceError: () -> Unit) : UtteranceProgressListener() {
        private val mainHandler = Handler(Looper.getMainLooper())

        override fun onStart(utteranceId: String?) = Unit
        override fun onDone(utteranceId: String?) = Unit

        @Deprecated("Deprecated")
        override fun onError(utteranceId: String?) = Unit

        override fun onError(utteranceId: String?, errorCode: Int) {
            if (errorCode == TextToSpeech.ERROR_SERVICE) mainHandler.post(onServiceError)
        }
    }

    private companion object {
        const val UTTERANCE_ID = "header_item"
    }
}
