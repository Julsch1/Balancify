package com.example.balancify

import CalendarScreen
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.balancify.components.BottomNavItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

@Composable
fun Navigation(navController: NavHostController){
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    NavHost(navController = navController, startDestination = determineStartDestination(currentUser) ){
        composable("login"){
            LoginScreen(navController)
        }
        composable("register"){
            RegisterScreen(navController)
        }
        composable("home"){
            HomeScreen(navController)
        }
        composable("map"){
            MapScreen(navController)
        }
        composable("calender"){
            CalendarScreen(navController)
        }
        composable("progress"){
            ProgressScreen(navController)
        }
        composable("profile"){
            ProfileScreen(navController)
        }
        composable("awards"){
            AwardsScreen(navController)
        }
        composable("todo"){
            ToDoScreen(navController)
        }
    }
}

private fun determineStartDestination(currentUser: FirebaseUser?): String {
    return if (currentUser != null) {
        "home"
    } else {
        "login"
    }
}

@Composable
fun BottomNavigationBar(
    items: List<BottomNavItem>,
    navController: NavController,
    modifier: Modifier = Modifier,
    onItemClick: (BottomNavItem) -> Unit
) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    NavigationBar(
        modifier = modifier,
        containerColor = colorResource(id = R.color.main_blue),
        tonalElevation = 5.dp
    ) {
        items.forEach{ item ->
            val selected = item.route == backStackEntry.value?.destination?.route

            NavigationBarItem(
                selected = selected,
                onClick = { onItemClick(item) },
                icon = {
                    Column(horizontalAlignment = CenterHorizontally){
                        Icon(imageVector = item.icon,
                            contentDescription = item.name)

                        if(selected){
                            Text(
                                text = item.name,
                                textAlign = TextAlign.Center,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            )
        }
    }
}






