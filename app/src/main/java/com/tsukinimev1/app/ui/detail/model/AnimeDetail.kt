package com.tsukinimev1.app.ui.detail.model

import com.tsukinimev1.app.data.AnimeItem

data class Episode(
    val episodeId: String,
    val endpoint: String,
    val title: String,
    val date: String?,
    val views: Int?,
    val watched: Boolean = false,
)

data class AnimeDetail(
    val animeId: String,
    val title: String,
    val poster: String?,
    val banner: String?,
    val score: String?,
    val status: String?,
    val type: String?,
    val synopsis: String?,
    val genres: List<String>,
    val released: String?,
    val author: String?,
    val totalEpisodes: Int?,
    val altTitle: String? = null,
    val scheduleDay: String? = null,
    val views: Long? = null,
    val subscribers: Long? = null,
    val episodes: List<Episode>,
) {
    val cleanTitle: String
        get() = title.replace("Subtitle Indonesia", "", ignoreCase = true).trim()

    val isOngoing: Boolean
        get() = !(status ?: "").uppercase().contains("COMPLETED")

    val hasRating: Boolean
        get() {
            val raw = score?.trim().orEmpty()
            if (raw.isEmpty()) return false
            val v = raw.toFloatOrNull() ?: return false
            return v > 0f
        }
}

fun AnimeItem.toDetail(watchedSet: Set<String>): AnimeDetail = AnimeDetail(
    animeId = animeId,
    title = title,
    poster = poster,
    banner = banner,
    score = score,
    status = status,
    type = type,
    synopsis = synopsis,
    genres = genres,
    released = released,
    author = author,
    totalEpisodes = totalEpisodes,
    episodes = episodeList.map {
        Episode(
            episodeId = it.episodeId,
            endpoint = it.endpoint,
            title = it.title,
            date = it.date,
            views = it.views,
            watched = it.episodeId in watchedSet,
        )
    },
)
