package com.example.mymoji.speech

import android.speech.tts.TextToSpeech
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject

class AndroidTextSpeaker @Inject constructor(
    private val engineFactory: SpeechEngineFactory
) : TextSpeaker {

    private enum class State { Disconnected, Connecting, Ready }

    private var engine: SpeechEngine? = null
    private var state = State.Disconnected
    private var pendingText: String? = null

    // Bumped on every connect, so callbacks from an engine that was already replaced are ignored.
    private var generation = 0

    override fun speak(text: String) {
        when (state) {
            State.Ready -> speakNow(text, reconnectOnFailure = true)
            State.Connecting -> pendingText = text
            State.Disconnected -> {
                pendingText = text
                connect()
            }
        }
    }

    override fun shutdown() {
        generation++
        release()
        pendingText = null
    }

    private fun speakNow(text: String, reconnectOnFailure: Boolean) {
        val result = engine?.speak(text) ?: TextToSpeech.ERROR
        if (result == TextToSpeech.SUCCESS) return

        // speak() fails once the engine's service is gone, e.g. the TTS app crashed or was updated.
        // Only retry once, so a broken engine can't cause a reconnect loop.
        if (reconnectOnFailure) {
            Timber.w("Text to speech engine unavailable, reconnecting")
            pendingText = text
            connect()
        } else {
            Timber.w("Text to speech still failing after reconnecting")
        }
    }

    private fun connect() {
        release()
        state = State.Connecting
        val connection = ++generation
        engine = engineFactory.create(
            onInit = { status -> onEngineInit(connection, status) },
            onServiceError = { onEngineDied(connection) }
        )
    }

    private fun onEngineInit(connection: Int, status: Int) {
        if (connection != generation) return
        if (status != TextToSpeech.SUCCESS) {
            Timber.w("Text to speech engine failed to start (status $status)")
            state = State.Disconnected
            pendingText = null
            return
        }
        val languageResult = engine?.setLanguage(Locale.US)
        if (languageResult == TextToSpeech.LANG_MISSING_DATA || languageResult == TextToSpeech.LANG_NOT_SUPPORTED) {
            Timber.w("English voice unavailable, using the engine's default voice")
        }
        state = State.Ready
        pendingText?.let { text ->
            pendingText = null
            speakNow(text, reconnectOnFailure = false)
        }
    }

    private fun onEngineDied(connection: Int) {
        if (connection != generation) return
        Timber.w("Text to speech engine died, reconnecting on next speak")
        release()
    }

    private fun release() {
        engine?.shutdown()
        engine = null
        state = State.Disconnected
    }
}
