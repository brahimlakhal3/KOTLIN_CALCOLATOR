package com.brahim.calculator.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.brahim.calculator.config.appConfigStore
import java.util.Locale
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppConfigVM(application: Application) : AndroidViewModel(application) {

  private val store = application.appConfigStore

  val language: StateFlow<String> =
      store.data
          .map { it.defaultLanguage.ifBlank { Locale.getDefault().language } }
          .distinctUntilChanged()
          .stateIn(viewModelScope, SharingStarted.Eagerly, Locale.getDefault().language)

  val mode: StateFlow<String> =
      store.data.map { it.defaultMode }.stateIn(viewModelScope, SharingStarted.Eagerly, "")

  fun setLanguage(language: String) {
    viewModelScope.launch {
      store.updateData { config -> config.toBuilder().setDefaultLanguage(language).build() }
    }
  }

  fun setMode(mode: String) {
    viewModelScope.launch {
      store.updateData { config -> config.toBuilder().setDefaultMode(mode).build() }
    }
  }

  companion object {
    const val LANGUAGE_FRENCH = "fr"
    const val LANGUAGE_ENGLISH = "en"

    val Factory: ViewModelProvider.Factory = viewModelFactory {
      initializer { AppConfigVM(this[APPLICATION_KEY] as Application) }
    }
  }
}
