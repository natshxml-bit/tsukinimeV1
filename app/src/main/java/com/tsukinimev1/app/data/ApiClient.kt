package com.tsukinimev1.app.data

import com.tsukinimev1.app.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import okhttp3.OkHttpClient
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import kotlinx.serialization.json.decodeFromJsonElement
import java.util.concurrent.TimeUnit

interface TsukiNimeApi {
    @GET("home")
    suspend fun home(): JsonElement

    @GET("recommendations")
    suspend fun recommendations(): JsonElement

    @GET("genres")
    suspend fun genres(): JsonElement

    @GET("genre/{slug}")
    suspend fun genre(@Path("slug") slug: String): JsonElement

    @GET("list/{type}")
    suspend fun list(@Path("type") type: String): JsonElement

    @GET("search/{query}")
    suspend fun search(@Path("query") query: String): JsonElement

    @GET("anime/{slug}")
    suspend fun animeDetail(@Path("slug") slug: String): JsonElement

    @GET("episode/{episodeId}")
    suspend fun episode(@Path("episodeId") episodeId: String): JsonElement

    @GET("schedule")
    suspend fun schedule(): JsonElement
}

object ApiClient {
    private const val BASE_URL = "https://backendnime.up.railway.app/"

    private val okHttp = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(40, TimeUnit.SECONDS)
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC
                else HttpLoggingInterceptor.Level.NONE
            }
        )
        .build()

    private val api: TsukiNimeApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttp)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))        .build()
        .create(TsukiNimeApi::class.java)

    private val anilistOkHttp = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(25, TimeUnit.SECONDS)
        .build()

    suspend fun fetchAniListBanner(title: String): String? = withContext(Dispatchers.IO) {
        val clean = title
            .replace("Subtitle Indonesia", "", ignoreCase = true)
            .trim()
        if (clean.length < 3) return@withContext null
        try {
            val query = "query(\$s: String) { Media(search: \$s, type: ANIME) { bannerImage coverImage { extraLarge } } }"
            val body = kotlinx.serialization.json.buildJsonObject {
                put("query", JsonPrimitive(query))
                put("variables", kotlinx.serialization.json.buildJsonObject { put("search", JsonPrimitive(clean)) })
            }.toString()
            val req = okhttp3.Request.Builder()
                .url("https://graphql.anilist.co")
                .post(body.toRequestBody("application/json".toMediaType()))
                .build()
            val res = anilistOkHttp.newCall(req).execute()
            val text = res.body?.string().orEmpty()
            val root = runCatching { json.parseToJsonElement(text).jsonObject }.getOrNull()
            val media = root?.get("data")?.jsonObject?.get("Media")?.jsonObject
            val banner = (media?.get("bannerImage") as? JsonPrimitive)?.content
            if (!banner.isNullOrBlank()) banner
            else (media?.get("coverImage")?.jsonObject?.get("extraLarge") as? JsonPrimitive)?.content
                ?.takeIf { it.isNotBlank() }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun fetchHome(): HomeData = withContext(Dispatchers.IO) {
        val el = api.home()
        parseHome(el.jsonObject)
    }

    suspend fun fetchRecommendations(): List<AnimeItem> = withContext(Dispatchers.IO) {
        val el = api.recommendations()
        (el as? JsonObject)?.get("animeList")
            ?.takeIf { it is JsonArray }
            ?.let { arr -> arr as JsonArray }
            ?.mapNotNull { runCatching { json.decodeFromJsonElement<AnimeItem>(it) }.getOrNull() }
            ?: emptyList()
    }

    suspend fun fetchGenres(): List<Genre> = withContext(Dispatchers.IO) {
        api.genres().let { el ->
            (el as? JsonArray)?.mapNotNull {
                runCatching { json.decodeFromJsonElement<Genre>(it) }.getOrNull()
            } ?: emptyList()
        }
    }

    suspend fun fetchByGenre(slug: String): List<AnimeItem> = withContext(Dispatchers.IO) {
        api.genre(slug).let { el ->
            (el as? JsonArray)?.mapNotNull {
                runCatching { json.decodeFromJsonElement<AnimeItem>(it) }.getOrNull()
            } ?: emptyList()
        }
    }

    suspend fun fetchList(type: String): List<AnimeItem> = withContext(Dispatchers.IO) {
        api.list(type).let { el ->
            (el as? JsonArray)?.mapNotNull {
                runCatching { json.decodeFromJsonElement<AnimeItem>(it) }.getOrNull()
            } ?: emptyList()
        }
    }

    suspend fun fetchSearch(query: String): List<AnimeItem> = withContext(Dispatchers.IO) {
        api.search(query).let { el ->
            (el as? JsonArray)?.mapNotNull {
                runCatching { json.decodeFromJsonElement<AnimeItem>(it) }.getOrNull()
            } ?: emptyList()
        }
    }

    suspend fun fetchAnimeDetail(slug: String): AnimeItem? = withContext(Dispatchers.IO) {
        runCatching { json.decodeFromJsonElement<AnimeItem>(api.animeDetail(slug)) }.getOrNull()
    }

    suspend fun fetchEpisode(episodeId: String): EpisodeDetail? = withContext(Dispatchers.IO) {
        runCatching { json.decodeFromJsonElement<EpisodeDetail>(api.episode(episodeId)) }.getOrNull()
    }

    suspend fun fetchSchedule(): List<ScheduleDay> = withContext(Dispatchers.IO) {
        api.schedule().let { el ->
            (el as? JsonArray)?.mapNotNull {
                runCatching { json.decodeFromJsonElement<ScheduleDay>(it) }.getOrNull()
            } ?: emptyList()
        }
    }
}