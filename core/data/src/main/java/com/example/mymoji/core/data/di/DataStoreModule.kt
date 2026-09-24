package com.example.mymoji.core.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.example.mymoji.core.data.DisplayHistorySerializer
import com.example.mymoji.core.data.proto.DisplayHistory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.displayHistoryDataStore: DataStore<DisplayHistory> by dataStore(
    fileName = "display_history.pb",
    serializer = DisplayHistorySerializer
)

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideDisplayHistoryDataStore(@ApplicationContext context: Context): DataStore<DisplayHistory> {
        return context.displayHistoryDataStore
    }
}
