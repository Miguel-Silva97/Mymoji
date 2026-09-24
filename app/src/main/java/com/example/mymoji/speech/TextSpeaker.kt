package com.example.mymoji.speech

interface TextSpeaker {
    fun speak(text: String)
    fun shutdown()
}
