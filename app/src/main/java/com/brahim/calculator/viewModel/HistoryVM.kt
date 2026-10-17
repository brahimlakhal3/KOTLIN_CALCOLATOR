package com.brahim.calculator.viewModel

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.brahim.calculator.History
import com.brahim.calculator.HistoryList
import com.brahim.calculator.config.historyDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryVM(private val store: DataStore<HistoryList>) : ViewModel() {

  val history: StateFlow<List<History>> =
      store.data
          .map { it.historiesList }
          .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

  fun add(title: String, content: String, dateTime: String) {
    viewModelScope.launch {
      store.updateData { list ->
        val builder = list.toBuilder()
        val entry =
            History.newBuilder()
                .setTitle(title)
                .setContent(content)
                .setDateTime(dateTime)
                .build()
        builder.historiesList.indexOfFirst { it == entry }
            .takeIf { it >= 0 }
            ?.let(builder::removeHistories)
        builder.addHistories(entry)
        while (builder.historiesCount > MAX_ENTRIES) {
          builder.removeHistories(0)
        }
        builder.build()
      }
    }
  }

  fun deleteAt(index: Int) {
    viewModelScope.launch {
      store.updateData { list ->
        if (index in 0 until list.historiesCount) list.toBuilder().removeHistories(index).build()
        else list
      }
    }
  }

  fun clear() {
    viewModelScope.launch { store.updateData { it.toBuilder().clearHistories().build() } }
  }

  companion object {
    private const val MAX_ENTRIES = 200

    val Factory: ViewModelProvider.Factory = viewModelFactory {
      initializer { HistoryVM((this[APPLICATION_KEY] as Application).historyDataStore) }
    }
  }
}
