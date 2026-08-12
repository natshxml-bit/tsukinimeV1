package com.tsukinimev1.app.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tsukinimev1.app.data.ApiClient
import com.tsukinimev1.app.data.AnimeItem
import com.tsukinimev1.app.data.LocalStore
import com.tsukinimev1.app.ui.detail.model.AnimeDetail
import com.tsukinimev1.app.ui.detail.model.toDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Success(
        val detail: AnimeDetail,
        val related: List<AnimeItem>,
    ) : DetailUiState

    data class Error(val message: String) : DetailUiState
}

class DetailViewModelFactory(
    private val store: LocalStore,
) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(store) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

class DetailViewModel(
    private val store: LocalStore,
) : ViewModel() {
    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private var animeId: String = ""

    fun load(slug: String) {
        if (animeId == slug && _uiState.value !is DetailUiState.Error) return
        animeId = slug
        _uiState.value = DetailUiState.Loading
        viewModelScope.launch {
            try {
                val detail = ApiClient.fetchAnimeDetail(slug)
                val watched = store.watchedEpisodes.first()
                val related = runCatching { ApiClient.fetchRecommendations() }.getOrDefault(emptyList())
                if (detail == null) {
                    _uiState.value = DetailUiState.Error("Anime tidak ditemukan")
                } else {
                    _uiState.value = DetailUiState.Success(
                        detail = detail.toDetail(watched),
                        related = related,
                    )
                }
            } catch (e: Exception) {
                _uiState.value = DetailUiState.Error(e.message ?: "Gagal memuat data")
            }
        }
    }
}
