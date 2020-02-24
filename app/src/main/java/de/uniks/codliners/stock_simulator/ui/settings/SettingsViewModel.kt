package de.uniks.codliners.stock_simulator.ui.settings

import android.app.Application
import androidx.lifecycle.*
import de.uniks.codliners.stock_simulator.repository.SymbolRepository
import de.uniks.codliners.stock_simulator.sourcedLiveData
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : ViewModel() {

    private val symbolRepository = SymbolRepository(application)

    // Button click indicator for reset button.
    private val _clickResetStatus = MutableLiveData<Boolean>()
    val clickResetStatus: LiveData<Boolean> = _clickResetStatus

    // Button click indicator for fingerprint button.
    private val _toggleFingerprintStatus = MutableLiveData<Boolean>()
    val toggleFingerprintStatus: LiveData<Boolean> = _toggleFingerprintStatus

    private val state = symbolRepository.state
    private val symbolRefreshInitiated = MutableLiveData<Boolean>(false)
    val symbolRepositoryStateAction = sourcedLiveData(symbolRefreshInitiated, state) {
        when (val state = state.value) {
            SymbolRepository.State.Refreshing -> if (symbolRefreshInitiated.value == true) state else null
            else -> state
        }
    }

    val refreshing = state.map { it === SymbolRepository.State.Refreshing }

    fun resetGame() {
        _clickResetStatus.value = true
    }

    fun onGameReset() {
        _clickResetStatus.value = false
    }

    fun toggleFingerprint() {
        _toggleFingerprintStatus.value = true
    }

    fun onFingerprintToggled() {
        _toggleFingerprintStatus.value = false
    }

    fun refreshSymbols() {
        viewModelScope.launch {
            symbolRefreshInitiated.value = true
            symbolRepository.refreshSymbols()
            symbolRefreshInitiated.value = false
        }
    }

    fun onSymbolActionCompleted() {
        (symbolRepositoryStateAction as MutableLiveData<SymbolRepository.State>).value = null
    }

    class Factory(
        private val application: Application
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SettingsViewModel(application) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}
