package com.tsukinimev1.app.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.decodeFromJsonElement

internal val json = Json { ignoreUnknownKeys = true; isLenient = true }

@Serializable
data class AnimeItem(
    @SerialName("animeId") val animeId: String = "",
    @SerialName("title") val title: String = "",
    @SerialName("poster") val poster: String? = null,
    @SerialName("banner") val banner: String? = null,
    @SerialName("score") val score: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("type") val type: String? = null,
    @SerialName("episode") val episode: Int? = null,
    @SerialName("totalEpisodes") val totalEpisodes: Int? = null,
    @SerialName("quality") val quality: String? = null,
    @SerialName("synopsis") val synopsis: String? = null,
    @SerialName("released") val released: String? = null,
    @SerialName("author") val author: String? = null,
    @SerialName("genres") val genres: List<String> = emptyList(),
    @SerialName("episodeList") val episodeList: List<EpisodeInfo> = emptyList(),
)

val AnimeItem.cleanTitle: String
    get() = title.replace("Subtitle Indonesia", "", ignoreCase = true).trim()

val AnimeItem.isOngoing: Boolean
    get() = !(status ?: "").uppercase().contains("COMPLETED")

@Serializable
data class EpisodeInfo(
    @SerialName("episodeId") val episodeId: String = "",
    @SerialName("endpoint") val endpoint: String = "",
    @SerialName("title") val title: String = "",
    @SerialName("date") val date: String? = null,
    @SerialName("views") val views: Int? = null,
)

@Serializable
data class HomeData(
    val recent: List<AnimeItem> = emptyList(),
    val ongoing: List<AnimeItem> = emptyList(),
    val completed: List<AnimeItem> = emptyList(),
    val film: List<AnimeItem> = emptyList(),
)

@Serializable
data class Recommendations(
    @SerialName("animeList") val animeList: List<AnimeItem> = emptyList(),
)

@Serializable
data class Genre(
    @SerialName("title") val title: String = "",
    @SerialName("endpoint") val endpoint: String = "",
)

@Serializable
data class ScheduleDay(
    @SerialName("day") val day: String = "",
    @SerialName("date") val date: String = "",
    @SerialName("anime_list") val animeList: List<AnimeItem> = emptyList(),
)

@Serializable
data class EpisodeDetail(
    @SerialName("episodeId") val episodeId: String = "",
    @SerialName("title") val title: String = "",
    @SerialName("animeTitle") val animeTitle: String? = null,
    @SerialName("defaultStreamingUrl") val defaultStreamingUrl: String? = null,
    @SerialName("streamUrl") val streamUrl: String? = null,
    @SerialName("server") val server: String? = null,
    @SerialName("servers") val servers: List<StreamServer> = emptyList(),
) {
    val bestStreamUrl: String
        get() = streamUrl ?: defaultStreamingUrl
            ?: servers.asSequence().flatMap { it.qualities.asSequence() }.map { it.url }.firstOrNull()
            ?: ""
}

@Serializable
data class StreamServer(
    @SerialName("server") val server: String = "",
    @SerialName("qualities") val qualities: List<Quality> = emptyList(),
)

@Serializable
data class Quality(
    @SerialName("quality") val quality: String = "",
    @SerialName("url") val url: String = "",
)

/**
 * API /home punya bentuk campur-campur:
 * recent = array langsung, ongoing/completed/film bisa { animeList: [...] }
 */
fun parseHome(jsonRoot: JsonObject): HomeData {
    fun listOf(key: String): List<AnimeItem> {
        val el = jsonRoot[key] ?: return emptyList()
        return when (el) {
            is JsonNull -> emptyList()
            is JsonArray -> el.mapNotNull { runCatching { json.decodeFromJsonElement<AnimeItem>(it) }.getOrNull() }
            else -> (el as? JsonObject)?.get("animeList")
                ?.let { arr -> arr as? JsonArray }
                ?.mapNotNull { runCatching { json.decodeFromJsonElement<AnimeItem>(it) }.getOrNull() }
                ?: emptyList()
        }
    }
    return HomeData(
        recent = listOf("recent"),
        ongoing = listOf("ongoing"),
        completed = listOf("completed"),
        film = listOf("film"),
    )
}

internal typealias JsonArray = kotlinx.serialization.json.JsonArray
