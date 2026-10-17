package com.brahim.calculator.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.brahim.calculator.AppConfig
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream
import java.util.Locale

object SerializerAppConfig : Serializer<AppConfig> {

  @Suppress("ConstantLocale")
  override val defaultValue: AppConfig =
    AppConfig.newBuilder()
      .setDefaultLanguage(Locale.getDefault().language)
      .setDefaultMode("AUTO")
      .build()

  override suspend fun readFrom(input: InputStream): AppConfig {
    try {
      return AppConfig.parseFrom(input)
    } catch (exception: InvalidProtocolBufferException) {
      throw CorruptionException("Cannot read proto.", exception)
    }
  }

  override suspend fun writeTo(t: AppConfig, output: OutputStream) {
    t.writeTo(output)
  }
}