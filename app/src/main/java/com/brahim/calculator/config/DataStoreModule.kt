package com.brahim.calculator.config

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.brahim.calculator.HistoryList
import com.brahim.calculator.serializer.SerializerAppConfig
import com.brahim.calculator.serializer.SerializerHis

val Context.historyDataStore: DataStore<HistoryList> by dataStore(
  fileName = "history.pb",
  serializer = SerializerHis
)

val Context.appConfigStore by dataStore(
  fileName = "app_config.pb",
  serializer = SerializerAppConfig
)
