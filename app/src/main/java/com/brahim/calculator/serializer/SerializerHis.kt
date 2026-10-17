package com.brahim.calculator.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.brahim.calculator.HistoryList
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object SerializerHis : Serializer<HistoryList> {

  override val defaultValue: HistoryList = HistoryList.getDefaultInstance()

  override suspend fun readFrom(input: InputStream): HistoryList {
    try {
      return HistoryList.parseFrom(input)
    } catch (e: InvalidProtocolBufferException) {
      throw CorruptionException("Cannot read proto : ", e)
    }
  }

  override suspend fun writeTo(t: HistoryList, output: OutputStream) = t.writeTo(output)
}

