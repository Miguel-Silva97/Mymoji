package com.example.mymoji.di

import com.example.mymoji.speech.AndroidTextSpeaker
import com.example.mymoji.speech.SpeechEngineFactory
import com.example.mymoji.speech.TextSpeaker
import com.example.mymoji.speech.TextToSpeechEngineFactory
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class SpeechModule {

    @Binds
    @ViewModelScoped
    abstract fun bindTextSpeaker(impl: AndroidTextSpeaker): TextSpeaker

    @Binds
    abstract fun bindSpeechEngineFactory(impl: TextToSpeechEngineFactory): SpeechEngineFactory
}
