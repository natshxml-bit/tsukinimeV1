package com.tsukinimev1.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tsukinimev1.app.data.LocalStore
import com.tsukinimev1.app.theme.TsukiNimeTheme
import com.tsukinimev1.app.ui.nav.BottomBar
import com.tsukinimev1.app.ui.detail.DetailScreen
import com.tsukinimev1.app.ui.detail.DetailViewModelFactory
import com.tsukinimev1.app.ui.home.HomeScreen
import com.tsukinimev1.app.ui.library.LibraryScreen
import com.tsukinimev1.app.ui.list.ListScreen
import com.tsukinimev1.app.ui.nobar.NobarScreen
import com.tsukinimev1.app.ui.player.PlayerScreen
import com.tsukinimev1.app.ui.profile.ProfileScreen
import com.tsukinimev1.app.ui.schedule.ScheduleScreen
import com.tsukinimev1.app.ui.search.SearchScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TsukiNimeTheme {
                AppNav()
            }
        }
    }
}

object Routes {
    const val HOME = "home"
    const val SCHEDULE = "schedule"
    const val ALL = "all"
    const val NOBAR = "nobar"
    const val LIBRARY = "library"
    const val PROFILE = "profile"

    const val SEARCH = "search"
    const val LIST = "list/{type}/{title}?genre={genre}"
    const val DETAIL = "detail/{slug}"
    const val PLAYER = "player/{animeId}/{episodeId}?title={title}"

    const val LIST_ARG = "list/{type}/{title}"
}

@Composable
fun AppNav() {
    val context = LocalContext.current
    val store = remember { LocalStore(context) }
    val navController = rememberNavController()

    val bottomBarRoutes = setOf(
        Routes.HOME, Routes.SCHEDULE, Routes.ALL, Routes.NOBAR, Routes.LIBRARY, Routes.PROFILE
    )
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = currentRoute in bottomBarRoutes

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            if (showBottomBar) {
                BottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Routes.HOME) {
                val vm: com.tsukinimev1.app.ui.home.HomeViewModel = viewModel()
                HomeScreen(
                    viewModel = vm,
                    store = store,
                    navController = navController,
                )
            }
            composable(Routes.SCHEDULE) {
                ScheduleScreen(navController = navController)
            }
            composable(Routes.ALL) {
                ListScreen(
                    type = "ongoing-anime",
                    title = "Semua Anime",
                    store = store,
                    navController = navController,
                )
            }
            composable(Routes.NOBAR) {
                NobarScreen()
            }
            composable(Routes.LIBRARY) {
                LibraryScreen(store = store, navController = navController)
            }
            composable(Routes.PROFILE) {
                ProfileScreen(store = store)
            }
            composable(Routes.SEARCH) {
                SearchScreen(navController = navController)
            }
            composable(
                route = "list/{type}/{title}?genre={genre}",
                arguments = listOf(
                    navArgument("type") { defaultValue = "" },
                    navArgument("title") { defaultValue = "" },
                    navArgument("genre") { defaultValue = "" },
                ),
            ) { entry ->
                val type = entry.arguments?.getString("type") ?: ""
                val title = entry.arguments?.getString("title") ?: ""
                val genre = entry.arguments?.getString("genre") ?: ""
                ListScreen(
                    type = type,
                    title = title,
                    genreSlug = genre.ifEmpty { null },
                    store = store,
                    navController = navController,
                )
            }
            composable(
                route = "detail/{slug}",
                arguments = listOf(navArgument("slug") { defaultValue = "" }),
            ) { entry ->
                val slug = entry.arguments?.getString("slug") ?: ""
                val detailVm: com.tsukinimev1.app.ui.detail.DetailViewModel = viewModel(
                    factory = DetailViewModelFactory(store),
                )
                DetailScreen(
                    slug = slug,
                    viewModel = detailVm,
                    store = store,
                    navController = navController,
                )
            }
            composable(
                route = "player/{animeId}/{episodeId}?title={title}",
                arguments = listOf(
                    navArgument("animeId") { defaultValue = "" },
                    navArgument("episodeId") { defaultValue = "" },
                    navArgument("title") { defaultValue = "" },
                ),
            ) { entry ->
                PlayerScreen(
                    animeId = entry.arguments?.getString("animeId") ?: "",
                    episodeId = entry.arguments?.getString("episodeId") ?: "",
                    title = entry.arguments?.getString("title") ?: "",
                    store = store,
                    navController = navController,
                )
            }
        }
    }
}
