package com.example.balancify
import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.balancify.components.BottomNavItem
import com.example.balancify.ui.theme.BalancifyTheme
import java.util.Locale

class MainActivity : ComponentActivity() {

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppLocale(Locale.ENGLISH)

        setContent {
            BalancifyTheme {
                val navController = rememberNavController()
                val currentDestination = navController.currentBackStackEntryAsState().value?.destination?.route

                if (currentDestination != "login" && currentDestination != "register"){
                    Scaffold(
                        topBar = {
                            TopBar(screenName = currentDestination ?: "Unknown Screen")
                        },
                        bottomBar = {
                            BottomNavigationBar(
                                items = listOf(
                                    BottomNavItem(
                                        name = "Home",
                                        route = "home",
                                        icon = Icons.Default.Home
                                    ),
                                    BottomNavItem(
                                        name = "Map",
                                        route = "map",
                                        icon = Icons.Default.Place
                                    ),
                                    BottomNavItem(
                                        name = "Calendar",
                                        route = "calender",
                                        icon = Icons.Default.DateRange
                                    ),
                                    BottomNavItem(
                                        name = "Progress",
                                        route = "progress",
                                        icon = Icons.Default.Refresh
                                    ),
                                    BottomNavItem(
                                        name = "Profile",
                                        route = "profile",
                                        icon = Icons.Default.Person
                                    ),
                                ),
                                navController = navController,
                                onItemClick = {
                                    navController.navigate(it.route)
                                }
                            )
                        }
                    ) {
                        Navigation(navController = navController)
                    }
                } else{
                    Navigation(navController = navController)
                }
            }


        }
    }

    private fun setAppLocale(locale: Locale) {
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TopBar(screenName: String) {
        TopAppBar(
            title = {
                Text(text = screenName, style = LocalTextStyle.current.copy(fontSize = 20.sp))
            },
            colors = TopAppBarDefaults.mediumTopAppBarColors(
                containerColor = colorResource(R.color.main_blue),
                actionIconContentColor = colorResource(R.color.main_blue),
                navigationIconContentColor = colorResource(R.color.main_blue),
                scrolledContainerColor = colorResource(R.color.main_blue),
                titleContentColor = Color.Black
            ),
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding() // Adjust top edge to avoid overlapping with the status bar
        )
    }
}
