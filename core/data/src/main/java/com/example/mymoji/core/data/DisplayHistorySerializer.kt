package com.example.mymoji.core.data

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.example.mymoji.core.data.proto.DisplayHistory
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object DisplayHistorySerializer : Serializer<DisplayHistory> {

    override val defaultValue: DisplayHistory = DisplayHistory.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): DisplayHistory {
        try {
            return DisplayHistory.parseFrom(input)
        } catch (e: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read display history", e)
        }
    }

    override suspend fun writeTo(t: DisplayHistory, output: OutputStream) = t.writeTo(output)
}
