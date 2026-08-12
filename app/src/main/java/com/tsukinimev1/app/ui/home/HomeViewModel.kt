package com.tsukinimev1.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tsukinimev1.app.data.ApiClient
import com.tsukinimev1.app.data.AnimeItem
import com.tsukinimev1.app.data.Genre
import com.tsukinimev1.app.data.HomeData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val data: HomeData, val recommendations: List<AnimeItem>, val genres: List<Genre>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        _uiState.value = HomeUiState.Loading
        viewModelScope.launch {
            try {
                val home = ApiClient.fetchHome()
                val reco = ApiClient.fetchRecommendations()
                val genres = ApiClient.fetchGenres()
                _uiState.value = HomeUiState.Success(home, reco, genres)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Gagal memuat data")
            }
        }
    }
}
