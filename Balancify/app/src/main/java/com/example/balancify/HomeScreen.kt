package com.example.balancify

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WavingHand
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.balancify.components.SeparationBox
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun Grid() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            BoxWithTextAndColor("Start study tracker", colorResource(id = R.color.study), true)
        }
        item {
            BoxWithTextAndColor("Start party tracker", colorResource(id = R.color.party), true)
        }
        item {
            BoxWithTextAndColor("Add gym session", colorResource(id = R.color.gym), true)
        }
        item {
            BoxWithTextAndColor("Add ran kilometers", colorResource(id = R.color.gym), true)
        }
    }
}

@Composable
fun BoxWithTextAndColor(text: String, color: Color, includeStartTimerButton: Boolean) {
    BoxWithConstraints {
        val boxHeight = this.maxHeight / 2 // Set the height to half of the screen height

        Column {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp) // Adjust padding if needed
                    .background(color = color, shape = RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.BottomStart
            ) {
                Text(
                    text = text,
                    color = Color.White,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }

            if (includeStartTimerButton && (text == "Start study tracker" || text == "Start party tracker")) {
                StartTimerButton(
                    modifier = Modifier
                        .size(32.dp)
                        .padding(8.dp)
                )
            } else {
                if(text == "Add gym session"){
                    AddButtonHome(
                        modifier = Modifier
                            .size(32.dp)
                            .padding(8.dp),
                        type = "gym"
                    )
                } else{
                    AddButtonHome(
                        modifier = Modifier
                            .size(32.dp)
                            .padding(8.dp),
                        type = "km"
                    )
                }
            }
        }
    }

}

@Composable
fun HomeScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val userName = currentUser?.displayName ?: "User"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        Icon(imageVector = Icons.Default.WavingHand, contentDescription = "greeting")
        Text(
            text = "Hello, $userName!",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(40.dp))

        SeparationBox("Track your actions")
        Spacer(modifier = Modifier.height(20.dp))
        Grid()

    }
}

@Composable
fun StartTimerButton(modifier: Modifier = Modifier) {
    var timerStarted by remember { mutableStateOf(false) }
    var startTime by remember { mutableStateOf(0L) }
    var elapsedTime by remember { mutableStateOf(0L) }

    // Launch a coroutine when the timer starts
    LaunchedEffect(timerStarted) {
        if (timerStarted) {
            while (true) {
                // Update elapsed time every second
                elapsedTime = System.currentTimeMillis() - startTime
                delay(1000) // Delay for 1 second
            }
        }
    }

    Column() {
        IconButton(
            onClick = {
                timerStarted != timerStarted // toggle

                if (timerStarted){
                    if(startTime == 0L){ // timer has never been started
                        startTime = System.currentTimeMillis()
                    }
                } else{
                    // stop timer
                    timerStarted = false
                }
            },
            modifier = modifier
                .background(colorResource(id = R.color.dark_blue), CircleShape) // Set the background color to blue
        ) {
            Icon(
                imageVector = Icons.Default.Timer,
                contentDescription = "Timer",
                tint = Color.White
            )
        }

        if (timerStarted) {
            val seconds = elapsedTime / 1000
            val minutes = seconds / 60
            val remainingSeconds = seconds % 60

            Text(
                text = String.format("%02d:%02d", minutes, remainingSeconds),
                color = Color.Black,
                modifier = Modifier.padding(4.dp).align(Alignment.End)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddButtonHome(modifier: Modifier = Modifier, type : String) {
    var enteredKm by remember { mutableStateOf(0f) }
    var showDialog by remember { mutableStateOf(false) }

    // Function to validate if the entered value is a valid number
    fun isValidNumber(value: String): Boolean {
        return try {
            value.toFloat()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }

    // Function to show the dialog
    fun showEnterKmDialog() {
        showDialog = true
    }

    // Function to dismiss the dialog
    fun dismissDialog() {
        showDialog = false
    }

    Column {
        IconButton(
            onClick = {
                if (type == "gym") {
                    // Handle gym type click
                } else {
                    showEnterKmDialog()
                }
            },
            modifier = modifier
                .background(colorResource(id = R.color.dark_blue), CircleShape) // Set the background color to blue
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                tint = Color.White
            )
        }

        // Dialog for entering kilometers
        if (showDialog) {
            AlertDialog(
                onDismissRequest = {
                    dismissDialog()
                },
                title = { Text("Enter km") },
                text = {
                    TextField(
                        value = enteredKm.toString(),
                        onValueChange = {
                            if (isValidNumber(it)) {
                                enteredKm = it.toFloat()
                            }
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        label = { Text("Kilometers") }
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        // Handle the entered kilometers, e.g., save it to a variable
                        dismissDialog()
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        // Dismiss the dialog
                        dismissDialog()
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}



@Preview
@Composable
fun DefaultPreviewHomeScreen(){
    val navController = rememberNavController()
    HomeScreen(navController)
}
