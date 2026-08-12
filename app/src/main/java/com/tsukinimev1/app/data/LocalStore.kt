package com.tsukinimev1.app.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import java.time.LocalDate

private val Context.dataStore by preferencesDataStore(name = "tsukinime")

data class UserProfile(
    val name: String = "Guest",
    val level: Int = 1,
    val rank: String = "BASIC",
)

data class CheckInStatus(
    val checkedToday: Boolean = false,
    val streak: Int = 0,
)

class LocalStore(private val context: Context) {

    private object Keys {
        val name = stringPreferencesKey("user_name")
        val level = intPreferencesKey("user_level")
        val rank = stringPreferencesKey("user_rank")

        val watchlist = stringSetPreferencesKey("watchlist")
        val history = stringPreferencesKey("history")
        val historyProgress = stringPreferencesKey("history_progress")

        val lastCheckIn = stringPreferencesKey("last_checkin")
        val checkInStreak = intPreferencesKey("checkin_streak")
    }

    // ---------- Profile ----------
    val profile: Flow<UserProfile> = context.dataStore.data.map { p ->
        UserProfile(
            name = p[Keys.name] ?: "Guest",
            level = p[Keys.level] ?: 1,
            rank = p[Keys.rank] ?: "BASIC",
        )
    }

    suspend fun saveProfile(p: UserProfile) {
        context.dataStore.edit { prefs ->
            prefs[Keys.name] = p.name
            prefs[Keys.level] = p.level
            prefs[Keys.rank] = p.rank
        }
    }

    // ---------- Watchlist ----------
    val watchlist: Flow<Set<String>> = context.dataStore.data.map { p ->
        p[Keys.watchlist] ?: emptySet()
    }

    suspend fun toggleWatchlist(animeId: String): Boolean {
        var saved = false
        context.dataStore.edit { p ->
            val current = p[Keys.watchlist] ?: emptySet()
            saved = if (animeId in current) {
                p[Keys.watchlist] = current - animeId
                false
            } else {
                p[Keys.watchlist] = current + animeId
                true
            }
        }
        return saved
    }

    suspend fun isInWatchlist(animeId: String): Boolean =
        context.dataStore.data.map { p -> animeId in (p[Keys.watchlist] ?: emptySet()) }.first()

    // ---------- History + progress ----------
    val history: Flow<List<AnimeItem>> = context.dataStore.data.map { p ->
        val raw = p[Keys.history] ?: "[]"
        runCatching { json.decodeFromString<List<AnimeItem>>(raw) }.getOrDefault(emptyList())
    }

    suspend fun pushHistory(item: AnimeItem) {        context.dataStore.edit { p ->
            val raw = p[Keys.history] ?: "[]"
            val list = runCatching { json.decodeFromString<List<AnimeItem>>(raw) }
                .getOrDefault(emptyList())
                .filterNot { it.animeId == item.animeId }
                .toMutableList()
            list.add(0, item)
            p[Keys.history] = json.encodeToString(ListSerializer(AnimeItem.serializer()), list.take(50))
        }
    }

    suspend fun clearHistory() {
        context.dataStore.edit { p ->
            p.remove(Keys.history)
            p.remove(Keys.historyProgress)
        }
    }

    suspend fun saveProgress(animeId: String, progressPercent: Int) {        context.dataStore.edit { p ->
            val raw = p[Keys.historyProgress] ?: "{}"
            val map = runCatching {
                json.decodeFromString<Map<String, Int>>(raw)
            }.getOrDefault(emptyMap()).toMutableMap()
            map[animeId] = progressPercent
            p[Keys.historyProgress] = json.encodeToString(
                MapSerializer(String.serializer(), Int.serializer()), map
            )
        }
    }

    // ---------- Check-in ----------
    val checkIn: Flow<CheckInStatus> = context.dataStore.data.map { p ->
        val last = p[Keys.lastCheckIn]
        val today = LocalDate.now().toString()
        CheckInStatus(
            checkedToday = last == today,
            streak = p[Keys.checkInStreak] ?: 0,
        )
    }

    suspend fun claimCheckIn(): CheckInStatus {
        val today = LocalDate.now().toString()
        context.dataStore.edit { p ->
            val last = p[Keys.lastCheckIn]
            val yesterday = LocalDate.now().minusDays(1).toString()
            val newStreak = when (last) {
                today -> p[Keys.checkInStreak] ?: 0
                yesterday -> (p[Keys.checkInStreak] ?: 0) + 1
                else -> 1
            }
            p[Keys.lastCheckIn] = today
            p[Keys.checkInStreak] = newStreak
        }
        return checkIn.first()
    }
}
