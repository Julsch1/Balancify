package com.example.balancify
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.preference.PreferenceManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.GolfCourse
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TimerOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.balancify.components.RoundButton
import com.example.balancify.components.SeparationBox
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextField
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.balancify.ui.theme.ButtonColor
import com.example.balancify.ui.theme.ProgressGreen
import com.example.balancify.ui.theme.ProgressOrange
import com.example.balancify.ui.theme.ProgressRed
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun ProgressScreen(navController: NavController) {

    val firebaseAuth = FirebaseAuth.getInstance()
    val currentUser = firebaseAuth.currentUser
    val userUid = currentUser?.uid
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val context = LocalContext.current
    val sharedPreferences = remember { PreferenceManager.getDefaultSharedPreferences(context) }
//    sharedPreferences.edit().clear().apply()

    // ----------------- for tasks daily
    var showDialogForTasksDaily by remember { mutableStateOf(false) }
    var showDialogForTasksListDaily by remember { mutableStateOf(false) }
    var newTaskTitle by remember { mutableStateOf("") }
    var existingTasks = sharedPreferences.getStringSet("tasks_$userUid", setOf()) ?: setOf()
    var displayedTasks by remember { mutableStateOf(existingTasks.toMutableSet()) }
    var tasksDoneDaily by remember { mutableStateOf(0)}
    var taskIsCheckedDaily by remember { mutableStateOf(mapOf<String, Boolean>()) }
    // ------------------ end tasks daily

    // ----------------- for productive hours daily
    var showDialogForAddingProductiveHoursDaily by remember { mutableStateOf(false)}
    var newProductiveHoursDaily by remember{ mutableStateOf("")}
    var setProductiveHoursDaily by remember{ mutableStateOf("")}
    var timerStartedProductiveHoursDaily by remember { mutableStateOf(false)}
    var doneProductiveHoursDaily by remember { mutableStateOf(0L) }
    var elapsedMillis by remember { mutableStateOf(0L) }
    var timerStartTime by remember{ mutableStateOf(0L)}

    LaunchedEffect(timerStartedProductiveHoursDaily) {
        while (timerStartedProductiveHoursDaily) {
            delay(1000)
            elapsedMillis = doneProductiveHoursDaily + (System.currentTimeMillis() - timerStartTime)

            sharedPreferences.edit().putLong("doneProductiveHoursDaily_$userUid", doneProductiveHoursDaily).apply()
        }
    }
    // -------------- end productive hours

    // ----------------- for drink limit  daily
    var showDialogForAddingDrinkLimitDaily by remember { mutableStateOf(false)}
    var newDrinkLimitDaily by remember{ mutableStateOf("")}
    var setDrinkLimitDaily by remember { mutableStateOf("")}
    var doneDrinksDaily by remember { mutableStateOf(0)}
    // ---------------- end drink limit

    // ------------------ for gym session daily
    var showDialogForAddingGymSessionDaily by remember { mutableStateOf(false)}
    var showDialogForGymSessionListDaily by remember { mutableStateOf(false) }
    var newGymSessionDaily by remember { mutableStateOf("")}
    val existingGymSessions = sharedPreferences.getStringSet("gymSessions_$userUid", setOf()) ?: setOf()
    var displayedGymSessions by remember { mutableStateOf(existingGymSessions.toMutableSet()) }
    var gymSessionsDoneDaily by remember { mutableStateOf(0)}
    var gymSessionsCheckedDaily by remember { mutableStateOf(mapOf<String, Boolean>()) }
    // ------------------ end gym session daily

    // --------------- start km
    var showDialogForAddingKmDaily by remember{ mutableStateOf(false)}
    var showDialogForRanKmDaily by remember { mutableStateOf(false)}
    var newKmGoalDaily by remember { mutableStateOf("")}
    var newRanKmDaily by remember { mutableStateOf("")}
    var setKmGoalDaily by remember { mutableStateOf("")}
    var doneKmDaily by remember { mutableStateOf("")}
    // -------------- end km

    // -------------- start party hours
    var showDialogForAddPartyHours by remember { mutableStateOf(false)}
    var newPartyTimeDaily by remember { mutableStateOf("")}
    var setPartyTimeDaily by remember { mutableStateOf("")}

    var timerStartedPartyHoursDaily by remember { mutableStateOf(false)}
    var donePartyHoursDaily by remember { mutableStateOf(0L) }
    var elapsedMillisParty by remember { mutableStateOf(0L) }
    var timerStartTimeParty by remember{ mutableStateOf(0L)}


    LaunchedEffect(timerStartedPartyHoursDaily) {
        while (timerStartedPartyHoursDaily) {
            delay(1000)
            elapsedMillisParty = donePartyHoursDaily + (System.currentTimeMillis() - timerStartTimeParty)
            sharedPreferences.edit().putLong("donePartyHoursDaily_$userUid", donePartyHoursDaily).apply()
        }
    }

    // ------------------- end party hours

    DisposableEffect(Unit) {
        val savedTasksDone = sharedPreferences.getInt("tasksDoneDaily_$userUid", 0)
        val savedCheckedTasks = sharedPreferences.getStringSet("checkedTasks_$userUid", setOf()) ?: setOf()

        tasksDoneDaily = savedTasksDone
        taskIsCheckedDaily = existingTasks.associateWith { savedCheckedTasks.contains(it) }

        val savedProductiveHours = sharedPreferences.getString("setProductiveHoursDaily_$userUid", "") ?: ""
        setProductiveHoursDaily = savedProductiveHours

        doneProductiveHoursDaily = sharedPreferences.getLong("doneProductiveHoursDaily_$userUid", 0L)
        elapsedMillis = doneProductiveHoursDaily

        val savedDrinkLimitDaily = sharedPreferences.getString("setDrinkLimitDaily_$userUid", "") ?: ""
        setDrinkLimitDaily = savedDrinkLimitDaily

        val doneDrinksDailySave = sharedPreferences.getInt("doneDrinksDaily_$userUid", 0)
        doneDrinksDaily = doneDrinksDailySave

        val savedGymSessionsDone = sharedPreferences.getInt("gymSessionsDoneDaily_$userUid", 0)
        val savedCheckedGymSessions = sharedPreferences.getStringSet("checkedGymSessions_$userUid", setOf()) ?: setOf()

        gymSessionsDoneDaily = savedGymSessionsDone
        gymSessionsCheckedDaily = existingGymSessions.associateWith { savedCheckedGymSessions.contains(it) }

        val savedKmGoalDaily =  sharedPreferences.getString("setKmGoalDaily_$userUid", "") ?: ""
        setKmGoalDaily = savedKmGoalDaily

        val savedDoneKmDaily = sharedPreferences.getString("doneKmDaily_$userUid", "") ?: ""
        doneKmDaily = savedDoneKmDaily


        val savedSetPartyTimeDaily = sharedPreferences.getString("setPartyTimeDaily_$userUid", "") ?: ""
        setPartyTimeDaily = savedSetPartyTimeDaily

        donePartyHoursDaily = sharedPreferences.getLong("donePartyHoursDaily_$userUid", 0L)
        elapsedMillisParty = donePartyHoursDaily

        onDispose {
            // Save tasksDoneDaily and taskIsCheckedDaily to SharedPreferences
            sharedPreferences.edit()
                .putInt("tasksDoneDaily_$userUid", tasksDoneDaily)
                .putStringSet("checkedTasks_$userUid", taskIsCheckedDaily.filterValues { it }.keys)
                .putString("setProductiveHoursDaily_$userUid", setProductiveHoursDaily)
                .putLong("doneProductiveHoursDaily_$userUid", doneProductiveHoursDaily)
                .putString("setDrinkLimitDaily_$userUid", setDrinkLimitDaily)
                .putInt("doneDrinksDaily_$userUid", doneDrinksDaily)
                .putInt("gymSessionsDoneDaily_$userUid", gymSessionsDoneDaily)
                .putStringSet("checkedGymSessions_$userUid", gymSessionsCheckedDaily.filterValues { it }.keys )
                .putString("setKmGoalDaily_$userUid", setKmGoalDaily)
                .putString("doneKmDaily_$userUid", doneKmDaily)
                .putString("setPartyTimeDaily_$userUid", setPartyTimeDaily)
                .putLong("donePartyHoursDaily_$userUid", donePartyHoursDaily)
                .apply()
        }
    }

    Column {
        // Top bar with tabs
        Spacer(modifier = Modifier.height(60.dp))
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = colorResource(id = R.color.middle_blue),
            contentColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    color = colorResource(id = R.color.main_blue),
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[selectedTabIndex])
                )
            }
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 }
            ) {
                Text(text = "Day")
            }

            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 }
            ) {
                Text(text = "Week")
            }
        }

        // Buttons for "Awards" and ToDos
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            RoundButton(icon = Icons.Default.BookmarkAdded, "awards", navController)
            RoundButton(icon = Icons.Default.GolfCourse, "todo", navController)
        }

        if (selectedTabIndex == 0){
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            ) {
                item{
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(text = "DAILY GOALS", fontWeight = FontWeight.Thin, fontSize = 24.sp)
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(30.dp))
                    SeparationBox("Study section")
                }
                item {
                    Row {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp)
                        ) {
                            Text("Tasks to be done", fontWeight = FontWeight.Bold)

                            IconButton(
                                onClick = {
                                    showDialogForTasksDaily = true;
                                },
                                modifier = Modifier
                                    .widthIn(10.dp)
                                    .heightIn(10.dp)
                                    .padding(4.dp)
                                    .background(colorResource(id = R.color.dark_blue), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add",
                                    tint = Color.White
                                )
                            }
                            Canvas(
                                modifier = Modifier
                                    .size(90.dp)
                                    .padding(8.dp)
                                    .clickable {
                                        showDialogForTasksListDaily = true
                                    }
                            ) {

                                val existingTasks =
                                    sharedPreferences.getStringSet("tasks_$userUid", setOf()) ?: setOf()

                                val taskCount = existingTasks.size

                                val completionPercentage =
                                    if (taskCount > 0) tasksDoneDaily.toFloat() / taskCount.toFloat() else 0f

                                val circleColor = when {
                                    taskCount == 0 -> ButtonColor
                                    (taskCount > 0 && completionPercentage > 1.0f || taskCount.toFloat() == tasksDoneDaily.toFloat() ) -> ProgressGreen
                                    (taskCount > 0 && completionPercentage < 0.2f) -> ProgressRed
                                    else -> ProgressOrange
                                }

                                drawCircle(
                                    color = circleColor,
                                    style = Stroke(8.dp.toPx())
                                )

                                val text = "$tasksDoneDaily | $taskCount"
                                val textPaint = Paint().asFrameworkPaint().apply {
                                    color = circleColor.toArgb()
                                    textSize = 24.sp.toPx()
                                }

                                val textWidth = textPaint.measureText(text)
                                val textX = (size.width - textWidth) / 2
                                val textY = size.height / 2 + 12.dp.toPx()

                                drawContext.canvas.nativeCanvas.drawText(
                                    text,
                                    textX,
                                    textY,
                                    textPaint
                                )
                            }
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp)
                        ) {
                            Text("Productive hours", fontWeight = FontWeight.Bold)

                            Row {
                                IconButton(
                                    onClick = {
                                        showDialogForAddingProductiveHoursDaily = true;
                                    },
                                    modifier = Modifier
                                        .widthIn(10.dp)
                                        .heightIn(10.dp)
                                        .padding(4.dp)
                                        .background(
                                            colorResource(id = R.color.dark_blue),
                                            CircleShape
                                        )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add",
                                        tint = Color.White
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        timerStartedProductiveHoursDaily = !timerStartedProductiveHoursDaily

                                        if (timerStartedProductiveHoursDaily) {
                                            if (timerStartTime == 0L) {
                                                // timer has never been started
                                                timerStartTime = System.currentTimeMillis()
                                            }
                                        } else {
                                            // stop timer
                                            doneProductiveHoursDaily += elapsedMillis
                                            timerStartedProductiveHoursDaily = false
                                            timerStartTime = 0L

                                            sharedPreferences.edit().putLong("doneProductiveHoursDaily_$userUid", doneProductiveHoursDaily).apply()
                                        }
                                    },
                                    modifier = Modifier
                                        .widthIn(10.dp)
                                        .heightIn(10.dp)
                                        .padding(4.dp)
                                        .background(
                                            colorResource(id = R.color.dark_blue),
                                            CircleShape
                                        )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Timer,
                                        contentDescription = "Timer",
                                        tint = Color.White
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        timerStartTime = 0L
                                        elapsedMillis = 0L
                                        doneProductiveHoursDaily = 0L

                                    },
                                    modifier = Modifier
                                        .widthIn(10.dp)
                                        .heightIn(10.dp)
                                        .padding(4.dp)
                                        .background(
                                            colorResource(id = R.color.dark_blue),
                                            CircleShape
                                        )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.TimerOff,
                                        contentDescription = "Reset",
                                        tint = Color.White
                                    )
                                }
                            }

                            Canvas(
                                modifier = Modifier
                                    .size(90.dp)
                                    .padding(8.dp)
                            ) {

                                drawCircle(
                                    color = Color.Black,
                                    style = Stroke(8.dp.toPx())
                                )

                                var doneProductiveHoursToShow = formatMillisToMinutesSeconds(elapsedMillis)

                                val setProductiveHoursFloat = setProductiveHoursDaily.toFloatOrNull() ?: 0f

                                val donePercentage =
                                    if (elapsedMillis > 0L) elapsedMillis.toFloat() / (setProductiveHoursFloat * 60 * 1000) else 0f

                                if (setProductiveHoursDaily == ""){
                                    setProductiveHoursDaily = "0"
                                }

                                val circleColor = when {
                                    (setProductiveHoursDaily == "0" || setProductiveHoursDaily == "") -> ButtonColor
                                    donePercentage > 1.0f -> ProgressGreen
                                    donePercentage < 0.2f -> ProgressRed
                                    else -> ProgressOrange
                                }

                                drawCircle(
                                    color = circleColor,
                                    style = Stroke(8.dp.toPx())
                                )

                                val text = "$doneProductiveHoursToShow | $setProductiveHoursDaily"
                                val textPaint = Paint().asFrameworkPaint().apply {
                                    color = circleColor.toArgb()
                                    textSize = 12.sp.toPx()
                                }

                                val textWidth = textPaint.measureText(text)
                                val textX = (size.width - textWidth) / 2
                                val textY = size.height / 2 + 12.dp.toPx()

                                drawContext.canvas.nativeCanvas.drawText(
                                    text,
                                    textX,
                                    textY,
                                    textPaint
                                )
                            }
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(30.dp))
                    SeparationBox("Party section")
                }
                item {
                    Row {
                        // Left side
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp)
                        ) {
                            Text("Drink Limit", fontWeight = FontWeight.Bold)
                            Row{
                                IconButton(
                                    onClick = {
                                        showDialogForAddingDrinkLimitDaily = true;
                                    },
                                    modifier = Modifier
                                        .widthIn(0.05.dp)
                                        .heightIn(0.05.dp)
                                        .padding(2.dp)
                                        .background(
                                            colorResource(id = R.color.dark_blue),
                                            CircleShape
                                        )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Set",
                                        tint = Color.White
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        doneDrinksDaily += 1
                                        sharedPreferences.edit().putInt("doneDrinksDaily_$userUid", doneDrinksDaily).apply()
                                    },
                                    modifier = Modifier
                                        .widthIn(0.5.dp)
                                        .heightIn(0.5.dp)
                                        .padding(2.dp)
                                        .background(
                                            ProgressGreen,
                                            CircleShape
                                        )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add",
                                        tint = Color.White
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        if(doneDrinksDaily -1 >= 0){
                                            doneDrinksDaily -= 1
                                        }

                                        sharedPreferences.edit().putInt("doneDrinksDaily_$userUid", doneDrinksDaily).apply()
                                    },
                                    modifier = Modifier
                                        .widthIn(1.dp)
                                        .heightIn(1.dp)
                                        .padding(2.dp)
                                        .background(
                                            ProgressRed,
                                            CircleShape
                                        )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Remove,
                                        contentDescription = "Remove",
                                        tint = Color.White
                                    )
                                }

                            }
                            Canvas(
                                modifier = Modifier
                                    .size(90.dp)
                                    .padding(8.dp)
                            ) {

                                val circleColor = when {
                                    (setDrinkLimitDaily == "" || setDrinkLimitDaily == "0") -> ButtonColor
                                    (doneDrinksDaily > (setDrinkLimitDaily.toInt() - 3)  &&  doneDrinksDaily < setDrinkLimitDaily.toInt())-> ProgressOrange
                                    doneDrinksDaily >= setDrinkLimitDaily.toInt() -> ProgressRed
                                    else -> ProgressGreen
                                }

                                drawCircle(
                                    color = circleColor,
                                    style = Stroke(8.dp.toPx())
                                )

                                var text = ""
                                if(setDrinkLimitDaily == ""){
                                    text = "$doneDrinksDaily | 0"
                                } else{
                                    text = "$doneDrinksDaily | $setDrinkLimitDaily"
                                }

                                val textPaint = Paint().asFrameworkPaint().apply {
                                    color = circleColor.toArgb()
                                    textSize = 24.sp.toPx()
                                }

                                val textWidth = textPaint.measureText(text)
                                val textX = (size.width - textWidth) / 2
                                val textY = size.height / 2 + 12.dp.toPx()

                                drawContext.canvas.nativeCanvas.drawText(
                                    text,
                                    textX,
                                    textY,
                                    textPaint
                                )
                            }
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp)
                        ){
                            Text("Time left", fontWeight = FontWeight.Bold)

                            Row{
                                IconButton(
                                    onClick = {
                                        showDialogForAddPartyHours = true
                                    },
                                    modifier = Modifier
                                        .widthIn(0.5.dp)
                                        .heightIn(0.5.dp)
                                        .padding(2.dp)
                                        .background(
                                            ButtonColor,
                                            CircleShape
                                        )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add",
                                        tint = Color.White
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        timerStartedPartyHoursDaily = !timerStartedPartyHoursDaily

                                        if (timerStartedPartyHoursDaily) {
                                            if (timerStartTimeParty == 0L) {
                                                // timer has never been started
                                                timerStartTimeParty = System.currentTimeMillis()
                                            }
                                        } else {
                                            // stop timer
                                            donePartyHoursDaily += elapsedMillisParty
                                            timerStartedPartyHoursDaily = false
                                            timerStartTimeParty = 0L

                                            sharedPreferences.edit().putLong("donePartyHoursDaily_$userUid", donePartyHoursDaily).apply()
                                        }
                                    },
                                    modifier = Modifier
                                        .widthIn(10.dp)
                                        .heightIn(10.dp)
                                        .padding(4.dp)
                                        .background(
                                            colorResource(id = R.color.dark_blue),
                                            CircleShape
                                        )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Timer,
                                        contentDescription = "Timer",
                                        tint = Color.White
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        timerStartTimeParty = 0L
                                        elapsedMillisParty = 0L
                                        donePartyHoursDaily = 0L
                                    },
                                    modifier = Modifier
                                        .widthIn(10.dp)
                                        .heightIn(10.dp)
                                        .padding(4.dp)
                                        .background(
                                            colorResource(id = R.color.dark_blue),
                                            CircleShape
                                        )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.TimerOff,
                                        contentDescription = "Reset",
                                        tint = Color.White
                                    )
                                }


                            }

                            Canvas(
                                modifier = Modifier
                                    .size(90.dp)
                                    .padding(8.dp)
                            ) {

                                drawCircle(
                                    color = Color.Black,
                                    style = Stroke(8.dp.toPx())
                                )

                                var donePartyHoursToShow = formatMillisToMinutesSeconds(elapsedMillisParty)

                                val setPartyHoursFloat = setPartyTimeDaily.toFloatOrNull() ?: 0f

                                val donePercentage =
                                    if (elapsedMillisParty > 0L) elapsedMillisParty.toFloat() / (setPartyHoursFloat * 60 * 1000) else 0f

                                if (setPartyTimeDaily == ""){
                                    setPartyTimeDaily = "0"
                                }

                                val circleColor = when {
                                    (setPartyTimeDaily == "0" || setPartyTimeDaily == "") -> ButtonColor
                                    donePercentage > 1.0f -> ProgressGreen
                                    donePercentage < 0.2f -> ProgressRed
                                    else -> ProgressOrange
                                }

                                drawCircle(
                                    color = circleColor,
                                    style = Stroke(8.dp.toPx())
                                )

                                val text = "$donePartyHoursToShow | $setPartyTimeDaily"
                                val textPaint = Paint().asFrameworkPaint().apply {
                                    color = circleColor.toArgb()
                                    textSize = 12.sp.toPx()
                                }

                                val textWidth = textPaint.measureText(text)
                                val textX = (size.width - textWidth) / 2
                                val textY = size.height / 2 + 12.dp.toPx()

                                drawContext.canvas.nativeCanvas.drawText(
                                    text,
                                    textX,
                                    textY,
                                    textPaint
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(30.dp))
                    SeparationBox("Fitness section")
                }
                item {
                    Row {
                        // Left side
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp)
                        ) {
                            Text("Gym sessions", fontWeight = FontWeight.Bold)
                            IconButton(
                                onClick = {
                                    showDialogForAddingGymSessionDaily = true;
                                },
                                modifier = Modifier
                                    .widthIn(10.dp)
                                    .heightIn(10.dp)
                                    .padding(4.dp)
                                    .background(colorResource(id = R.color.dark_blue), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add",
                                    tint = Color.White
                                )
                            }

                            Canvas(
                                modifier = Modifier
                                    .size(90.dp)
                                    .padding(8.dp)
                                    .clickable {
                                        showDialogForGymSessionListDaily = true
                                    }
                            ) {

                                // Retrieve the existing tasks from SharedPreferences

                                val existingGymSessions =
                                    sharedPreferences.getStringSet("gymSessions_$userUid", setOf()) ?: setOf()

                                // Calculate the count of tasks
                                val gymSessionCount = existingGymSessions.size

                                // Calculate the percentage of tasksDoneDaily
                                val completionPercentage =
                                    if (gymSessionCount > 0) gymSessionsDoneDaily.toFloat() / gymSessionCount.toFloat() else 0f

                                // Define colors based on completionPercentage
                                val circleColor = when {
                                    gymSessionCount == 0 -> ButtonColor
                                    completionPercentage >= 1.0f -> ProgressGreen
                                    completionPercentage < 0.2f -> ProgressRed
                                    else -> ProgressOrange
                                }

                                // Draw the circle with the calculated color
                                drawCircle(
                                    color = circleColor,
                                    style = Stroke(8.dp.toPx())
                                )

                                // Draw the text centered inside the circle
                                val text = "$gymSessionsDoneDaily | $gymSessionCount"
                                val textPaint = Paint().asFrameworkPaint().apply {
                                    color = circleColor.toArgb()
                                    textSize = 24.sp.toPx()
                                }

                                val textWidth = textPaint.measureText(text)
                                val textX = (size.width - textWidth) / 2
                                val textY = size.height / 2 + 12.dp.toPx()

                                drawContext.canvas.nativeCanvas.drawText(
                                    text,
                                    textX,
                                    textY,
                                    textPaint
                                )
                            }
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp)
                        ){
                            Text("Km to run", fontWeight = FontWeight.Bold)

                            Row{
                                IconButton(
                                    onClick = {
                                        showDialogForAddingKmDaily = true
                                    },
                                    modifier = Modifier
                                        .widthIn(10.dp)
                                        .heightIn(10.dp)
                                        .padding(4.dp)
                                        .background(colorResource(id = R.color.dark_blue), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add",
                                        tint = Color.White
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        showDialogForRanKmDaily = true
                                    },
                                    modifier = Modifier
                                        .widthIn(10.dp)
                                        .heightIn(10.dp)
                                        .padding(4.dp)
                                        .background(colorResource(id = R.color.dark_blue), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Done,
                                        contentDescription = "Done",
                                        tint = Color.White
                                    )
                                }
                            }

                            Canvas(
                                modifier = Modifier
                                    .size(90.dp)
                                    .padding(8.dp)
                            ) {

                                val circleColor = when {
                                    (setKmGoalDaily == "" || setKmGoalDaily == "0") -> ButtonColor
                                    (doneKmDaily.toInt() > (setKmGoalDaily.toInt() - 3)  &&  doneKmDaily.toInt() < setKmGoalDaily.toInt())-> ProgressOrange
                                    doneKmDaily.toInt() >= setKmGoalDaily.toInt() -> ProgressGreen
                                    else -> ProgressRed
                                }

                                drawCircle(
                                    color = circleColor,
                                    style = Stroke(8.dp.toPx())
                                )

                                var text = ""
                                if(setKmGoalDaily == "" && doneKmDaily == ""){
                                    text = "0 | 0"
                                } else if (setKmGoalDaily == "" && doneKmDaily != ""){
                                    text = "$doneKmDaily | 0"
                                } else if(setKmGoalDaily != "" && doneKmDaily == ""){
                                    text = "0 | $setKmGoalDaily"
                                }
                                else{
                                    text = "$doneKmDaily | $setKmGoalDaily"
                                }

                                val textPaint = Paint().asFrameworkPaint().apply {
                                    color = circleColor.toArgb()
                                    textSize = 24.sp.toPx()
                                }

                                val textWidth = textPaint.measureText(text)
                                val textX = (size.width - textWidth) / 2
                                val textY = size.height / 2 + 12.dp.toPx()

                                drawContext.canvas.nativeCanvas.drawText(
                                    text,
                                    textX,
                                    textY,
                                    textPaint
                                )
                            }

//                                drawCircle(
//                                    color = Color.Black,
//                                    style = Stroke(8.dp.toPx())
//                                )
//
//                                drawContext.canvas.nativeCanvas.drawText(
//                                    "$setKmGoalDaily | $doneKmDaily",
//                                    size.width / 2 - 12.dp.toPx(),
//                                    size.height / 2 + 12.dp.toPx(),
//                                    Paint().asFrameworkPaint().apply {
//                                        color = Color.Black.toArgb()
//                                        textSize = 24.sp.toPx()
//                                    }
//                               )

                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(60.dp))
        }
        else{
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            ) {
                item{
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(text = "WEEKLY GOALS", fontWeight = FontWeight.Thin, fontSize = 24.sp)
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(30.dp))
                    SeparationBox("Study section")
                }
                item {
                    Row {
                        // Left side
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp)
                        ) {
                            Text("Tasks to be done", fontWeight = FontWeight.Bold)
                            AddButtonProgress("weekly", "tasks_$userUid")
                            Canvas(
                                modifier = Modifier
                                    .size(90.dp)
                                    .padding(8.dp)
                            ) {
                                drawCircle(
                                    color = Color.Black,
                                    style = Stroke(8.dp.toPx())
                                )
                                drawContext.canvas.nativeCanvas.drawText(
                                    "3",
                                    size.width / 2 - 12.dp.toPx(),
                                    size.height / 2 + 12.dp.toPx(),
                                    Paint().asFrameworkPaint().apply {
                                        color = Color.Black.toArgb()
                                        textSize = 24.sp.toPx()
                                    }
                                )
                            }
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp)
                        ){
                            Text("Productive hours", fontWeight = FontWeight.Bold)
                            AddButtonProgress("weekly", "productive_hours")
                            Canvas(
                                modifier = Modifier
                                    .size(90.dp)
                                    .padding(8.dp)
                            ) {
                                drawCircle(
                                    color = Color.Black,
                                    style = Stroke(8.dp.toPx())
                                )
                                drawContext.canvas.nativeCanvas.drawText(
                                    "3",
                                    size.width / 2 - 12.dp.toPx(),
                                    size.height / 2 + 12.dp.toPx(),
                                    Paint().asFrameworkPaint().apply {
                                        color = Color.Black.toArgb()
                                        textSize = 24.sp.toPx()
                                    }
                                )
                            }
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(30.dp))
                    SeparationBox("Party section")
                }
                item {
                    Row {
                        // Left side
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp)
                        ) {
                            Text("Drink Limit", fontWeight = FontWeight.Bold)
                            AddButtonProgress("weekly", "drink_limit")
                            Canvas(
                                modifier = Modifier
                                    .size(90.dp)
                                    .padding(8.dp)
                            ) {
                                drawCircle(
                                    color = Color.Black,
                                    style = Stroke(8.dp.toPx())
                                )
                                drawContext.canvas.nativeCanvas.drawText(
                                    "3",
                                    size.width / 2 - 12.dp.toPx(),
                                    size.height / 2 + 12.dp.toPx(),
                                    Paint().asFrameworkPaint().apply {
                                        color = Color.Black.toArgb()
                                        textSize = 24.sp.toPx()
                                    }
                                )
                            }
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp)
                        ){
                            Text("Party days", fontWeight = FontWeight.Bold)
                            AddButtonProgress("weekly", "party_days")
                            Canvas(
                                modifier = Modifier
                                    .size(90.dp)
                                    .padding(8.dp)
                            ) {
                                drawCircle(
                                    color = Color.Black,
                                    style = Stroke(8.dp.toPx())
                                )
                                drawContext.canvas.nativeCanvas.drawText(
                                    "3",
                                    size.width / 2 - 12.dp.toPx(),
                                    size.height / 2 + 12.dp.toPx(),
                                    Paint().asFrameworkPaint().apply {
                                        color = Color.Black.toArgb()
                                        textSize = 24.sp.toPx()
                                    }
                                )
                            }
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(30.dp))
                    SeparationBox("Fitness section")
                }
                item {
                    Row {
                        // Left side
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp)
                        ) {
                            Text("Gym sessions", fontWeight = FontWeight.Bold)
                            AddButtonProgress("weekly", "gym")
                            Canvas(
                                modifier = Modifier
                                    .size(90.dp)
                                    .padding(8.dp)
                            ) {
                                drawCircle(
                                    color = Color.Black,
                                    style = Stroke(8.dp.toPx())
                                )
                                drawContext.canvas.nativeCanvas.drawText(
                                    "3",
                                    size.width / 2 - 12.dp.toPx(),
                                    size.height / 2 + 12.dp.toPx(),
                                    Paint().asFrameworkPaint().apply {
                                        color = Color.Black.toArgb()
                                        textSize = 24.sp.toPx()
                                    }
                                )
                            }
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp)
                        ){
                            Text("Km to run", fontWeight = FontWeight.Bold)
                            AddButtonProgress("weekly", "km")
                            Canvas(
                                modifier = Modifier
                                    .size(90.dp)
                                    .padding(8.dp)
                            ) {
                                drawCircle(
                                    color = Color.Black,
                                    style = Stroke(8.dp.toPx())
                                )
                                drawContext.canvas.nativeCanvas.drawText(
                                    "3",
                                    size.width / 2 - 12.dp.toPx(),
                                    size.height / 2 + 12.dp.toPx(),
                                    Paint().asFrameworkPaint().apply {
                                        color = Color.Black.toArgb()
                                        textSize = 24.sp.toPx()
                                    }
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(60.dp))
        }

        if (showDialogForTasksDaily) {
            // show dialog to add
            AlertDialog(
                onDismissRequest = { showDialogForTasksDaily = false },
                title = {
                    Text("Add Task")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newTaskTitle.isNotBlank()) {
                                // Retrieve the existing tasks from SharedPreferences
                                val existingTasks = sharedPreferences.getStringSet("tasks_$userUid", setOf())?.toMutableSet() ?: mutableSetOf()

                                // Add the new task to the set
                                existingTasks.add(newTaskTitle)

                                displayedTasks = existingTasks

                                // Save the updated tasks set to SharedPreferences
                                sharedPreferences.edit().putStringSet("tasks_$userUid", existingTasks).apply()

                                newTaskTitle = ""

                                // Close the dialog
                                showDialogForTasksDaily = false
                            }
                        }
                    ) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showDialogForTasksDaily = false }
                    ) {
                        Text("Cancel")
                    }
                },
                text = {
                    OutlinedTextField(
                        value = newTaskTitle,
                        onValueChange = { newTaskTitle = it },
                        label = { Text("Task Title") }
                    )
                }
            )
        }

        if (showDialogForTasksListDaily) {
            AlertDialog(
                onDismissRequest = {
                    showDialogForTasksListDaily = false
                },
                title = {
                    Text("Task List")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDialogForTasksListDaily = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                text = {
                    LazyColumn {
                        items(displayedTasks.toList()) { taskTitle ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .background(
                                        if (taskIsCheckedDaily[taskTitle] == true) Color.Gray else ButtonColor,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = taskTitle,
                                        modifier = Modifier.weight(1f)
                                    )

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Checkbox(
                                            checked = taskIsCheckedDaily[taskTitle] ?: false,
                                            onCheckedChange = {
                                                taskIsCheckedDaily = taskIsCheckedDaily.toMutableMap().apply {
                                                    put(taskTitle, it)
                                                }

                                                if (it) {
                                                    tasksDoneDaily += 1
                                                } else {
                                                    tasksDoneDaily -= 1
                                                }
                                            }
                                        )

                                        IconButton(
                                            onClick = {
                                                if(taskIsCheckedDaily[taskTitle] == true){
                                                    tasksDoneDaily -= 1
                                                }
//                                                if(tasksDoneDaily >= existingTasks.size ){
//                                                    tasksDoneDaily -= 1
//                                                }

                                                val updatedTasks = displayedTasks.toMutableSet()
                                                updatedTasks.remove(taskTitle)

                                                displayedTasks = updatedTasks
                                                existingTasks = displayedTasks

                                                // Update tasks in SharedPreferences
                                                sharedPreferences.edit().putStringSet("tasks_$userUid", updatedTasks).apply()

                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = null,
                                                tint = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            )
        }

        if (showDialogForAddingProductiveHoursDaily) {
            AlertDialog(
                onDismissRequest = { showDialogForAddingProductiveHoursDaily = false },
                title = {
                    Text("Set productive hours")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            // update set value
                            setProductiveHoursDaily = newProductiveHoursDaily
                            showDialogForAddingProductiveHoursDaily = false
                        }
                    ) {
                        Text("Update")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showDialogForAddingProductiveHoursDaily = false }
                    ) {
                        Text("Cancel")
                    }
                },
                text = {
                    TextField(
                        value = "$newProductiveHoursDaily",
                        onValueChange = {
                            // Only allow numeric input
                            newProductiveHoursDaily = it.filter { it.isDigit() }
                        },
                        label = { Text("Enter") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                    )
                }
            )
        }

        if (showDialogForAddingDrinkLimitDaily) {
            AlertDialog(
                onDismissRequest = { showDialogForAddingDrinkLimitDaily = false },
                title = {
                    Text("Set drink limit")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            // update set value
                            setDrinkLimitDaily = newDrinkLimitDaily
                            showDialogForAddingDrinkLimitDaily = false
                        }
                    ) {
                        Text("Update")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showDialogForAddingDrinkLimitDaily = false }
                    ) {
                        Text("Cancel")
                    }
                },
                text = {
                    TextField(
                        value = "$newDrinkLimitDaily",
                        onValueChange = {
                            // Only allow numeric input
                            newDrinkLimitDaily = it.filter { it.isDigit() }
                        },
                        label = { Text("Enter") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                    )
                }
            )
        }

        if (showDialogForAddingGymSessionDaily) {
            // show dialog to add
            AlertDialog(
                onDismissRequest = { showDialogForAddingGymSessionDaily = false },
                title = {
                    Text("Add gym session")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newGymSessionDaily.isNotBlank()) {

                                val existingGymSessions = sharedPreferences.getStringSet("gymSessions_$userUid", setOf())?.toMutableSet() ?: mutableSetOf()

                                existingGymSessions.add(newGymSessionDaily)

                                displayedGymSessions = existingGymSessions

                                sharedPreferences.edit().putStringSet("gymSessions_$userUid", existingGymSessions).apply()

                                newGymSessionDaily = ""

                                // Close the dialog
                                showDialogForAddingGymSessionDaily = false
                            }
                        }
                    ) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showDialogForAddingGymSessionDaily = false }
                    ) {
                        Text("Cancel")
                    }
                },
                text = {
                    OutlinedTextField(
                        value = newGymSessionDaily,
                        onValueChange = { newGymSessionDaily = it },
                        label = { Text("Gym Session") }
                    )
                }
            )
        }

        if (showDialogForGymSessionListDaily) {
            AlertDialog(
                onDismissRequest = {
                    showDialogForGymSessionListDaily = false
                },
                title = {
                    Text("Gym Session List")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDialogForGymSessionListDaily = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                text = {
                    LazyColumn {
                        items(displayedGymSessions.toList()) { gymSessionTitle ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .background(
                                        if (gymSessionsCheckedDaily[gymSessionTitle] == true) Color.Gray else ButtonColor,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = gymSessionTitle,
                                        modifier = Modifier.weight(1f)
                                    )

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Checkbox(
                                            checked = gymSessionsCheckedDaily[gymSessionTitle] ?: false,
                                            onCheckedChange = {
                                                gymSessionsCheckedDaily = gymSessionsCheckedDaily.toMutableMap().apply {
                                                    put(gymSessionTitle, it)
                                                }

                                                if (it) {
                                                   gymSessionsDoneDaily += 1
                                                } else {
                                                    gymSessionsDoneDaily -= 1
                                                }
                                            }
                                        )

                                        IconButton(
                                            onClick = {

                                                if(gymSessionsCheckedDaily[gymSessionTitle] == true){
                                                    gymSessionsDoneDaily -= 1
                                                }

                                                val updatedGymSessions = displayedGymSessions.toMutableSet()
                                                updatedGymSessions.remove(gymSessionTitle)
                                                displayedGymSessions = updatedGymSessions

                                                sharedPreferences.edit().putStringSet("gymSessions_$userUid", updatedGymSessions).apply()
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = null,
                                                tint = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            )
        }

        if(showDialogForAddingKmDaily){
            AlertDialog(
                onDismissRequest = { showDialogForAddingKmDaily = false },
                title = {
                    Text("Add km goal")
                },
                confirmButton = {
                    Button(
                        onClick = {

                            if (newKmGoalDaily.isNotBlank()) {
                                setKmGoalDaily = newKmGoalDaily

                                showDialogForAddingKmDaily = false
                            }
                        }
                    ) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showDialogForAddingKmDaily = false }
                    ) {
                        Text("Cancel")
                    }
                },
                text = {
                    TextField(
                        value = "$newKmGoalDaily",
                        onValueChange = {
                            newKmGoalDaily = it.filter { it.isDigit() }
                        },
                        label = { Text("Enter") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                    )
                }
            )
        }

        if(showDialogForRanKmDaily){
            AlertDialog(
                onDismissRequest = { showDialogForRanKmDaily = false },
                title = {
                    Text("Ran km")
                },
                confirmButton = {
                    Button(
                        onClick = {

                            if (newRanKmDaily.isNotBlank()) {
                                doneKmDaily = newRanKmDaily

                                showDialogForRanKmDaily = false
                            }
                        }
                    ) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showDialogForRanKmDaily = false }
                    ) {
                        Text("Cancel")
                    }
                },
                text = {
                    TextField(
                        value = "$newRanKmDaily",
                        onValueChange = {
                            newRanKmDaily = it.filter { it.isDigit() }
                        },
                        label = { Text("Enter") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                    )
                }
            )
        }

        if(showDialogForAddPartyHours){
            AlertDialog(
                onDismissRequest = { showDialogForAddPartyHours = false },
                title = {
                    Text("Add party hours")
                },
                confirmButton = {
                    Button(
                        onClick = {

                            if (newPartyTimeDaily.isNotBlank()) {
                                setPartyTimeDaily = newPartyTimeDaily

                                showDialogForAddPartyHours = false
                            }
                        }
                    ) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showDialogForAddPartyHours = false }
                    ) {
                        Text("Cancel")
                    }
                },
                text = {
                    TextField(
                        value = "$newPartyTimeDaily",
                        onValueChange = {
                            newPartyTimeDaily = it.filter { it.isDigit() }
                        },
                        label = { Text("Enter") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                    )
                }
            )
        }
    }
}

@Composable
fun AddButtonProgress(section : String, type: String) {
    IconButton(
        onClick = {
        },
        modifier = Modifier
            .widthIn(10.dp)
            .heightIn(10.dp)

            .padding(4.dp)
            .background(colorResource(id = R.color.dark_blue), CircleShape) // Set the background color to blue
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add",
            tint = Color.White
        )
    }
}

fun formatMillisToMinutesSeconds(millis: Long): String {
    val seconds = millis / 1000
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}

data class Task(val title: String, val isDone: Boolean)

private fun sendNotification(context: Context, message: String) {
    val channelId = "channel_id"

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "Channel Name",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(0)
        .setContentTitle("Notification Title")
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    val notificationManager = NotificationManagerCompat.from(context)

    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        return
    }
    notificationManager.notify(1, builder.build())
}