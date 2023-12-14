import android.annotation.SuppressLint
import android.os.Build
import android.preference.PreferenceManager
import android.util.Log
import android.widget.CalendarView
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.compose.rememberNavController
import com.example.balancify.R
import com.example.balancify.ui.theme.ButtonColor
import com.example.balancify.ui.theme.DarkBlue
import com.example.balancify.ui.theme.GreyTime
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@SuppressLint("NewApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(navController: NavController) {
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var newTimeState : TimePickerState = rememberTimePickerState(11, 30, false)
    var timeState: TimePickerState = rememberTimePickerState(11, 30, false)

    val context = LocalContext.current
    val sharedPreferences = remember { PreferenceManager.getDefaultSharedPreferences(context) }

    val firebaseAuth = FirebaseAuth.getInstance()
    val currentUser = firebaseAuth.currentUser
    val userUid = currentUser?.uid

    // Map to store items for each date
    val calendarItems = remember { mutableStateMapOf<LocalDate, MutableList<CalendarItem>>() }

    fun deleteItem(date: LocalDate, item: CalendarItem) {
        calendarItems[date]?.remove(item)
        calendarItems[date] = calendarItems[date].orEmpty().toMutableList()
    }

    DisposableEffect(context) {
        try {
            val savedItems = sharedPreferences.getString("calendar_items_$userUid", null)
            Log.d("CalendarScreen", "Saved Items: $savedItems")

            if (!savedItems.isNullOrEmpty()) {
                val outerType = object : TypeToken<Map<String, List<Map<String, String>>>>() {}.type
                val outerMap: Map<String, List<Map<String, String>>> = Gson().fromJson(savedItems, outerType)

                // Extract the date string and the inner list
                for ((dateString, innerList) in outerMap) {
                    val date = LocalDate.parse(dateString)
                    val itemList = innerList.map {
                        // Convert the inner map to a CalendarItem
                        CalendarItem(
                            title = it["title"] ?: "",
                            description = it["description"] ?: "",
                            chosenTime = it["chosenTime"] ?: ""
                        )
                    }
                    calendarItems[date] = itemList.toMutableList()
                }
            }
        } catch (e: Exception) {
            // Log the exception to help diagnose the issue
            Log.e("CalendarScreen", "Error loading calendar items", e)
        }

        onDispose {
            try {
                // Save calendar items to SharedPreferences when the component is disposed
                val json = Gson().toJson(calendarItems)
                sharedPreferences.edit().putString("calendar_items_$userUid", json).apply()
            } catch (e: Exception) {
                // Log the exception to help diagnose the issue
                Log.e("CalendarScreen", "Error saving calendar items", e)
            }
        }
    }


    Column (
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(modifier = Modifier.height(60.dp))

        AndroidView(factory = { CalendarView(it) }, update = {
            it.setOnDateChangeListener { calendarView, year, month, day ->
                selectedDate = LocalDate.ofYearDay(year, day)
            }
        })

        selectedDate?.let {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color = colorResource(id = R.color.main_blue), shape = CircleShape)
                    .clickable {
                        showDialog = true
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display the list of items for the selected date
        selectedDate?.let { date ->
            val itemsForSelectedDate by rememberUpdatedState(calendarItems[date].orEmpty())

            Text("$date")

            LazyColumn {
                itemsIndexed(itemsForSelectedDate) { _, item ->

                    // description + title
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = ButtonColor)
                            .padding(8.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            )
                            {
                                Text(
                                    text = "Time: ${item.chosenTime}",
                                    color = DarkBlue
                                )

                                IconButton(
                                    onClick = {
                                        deleteItem(date, item)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                }
                            }

                            Text(
                                text = "Title: ${item.title}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Description: ${item.description}",
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            Spacer(modifier = Modifier.height(60.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (showDialog) {
            AlertDialog(
                onDismissRequest = {
                    showDialog = false
                },
                title = {
                    Text(text = "Add entry")
                },
                text = {
                    Column {
                        Text(text = "Enter title:",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold)
                        BasicTextField(
                            value = title,
                            onValueChange = {
                                title = it
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Done
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(color = colorResource(id = R.color.main_blue)),
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(text = "Enter description:", fontSize = 16.sp,
                            fontWeight = FontWeight.Bold)
                        BasicTextField(
                            value = description,
                            onValueChange = {
                                description = it
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Done,
                                keyboardType = KeyboardType.Text
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(color = colorResource(id = R.color.main_blue))
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Selected Date: ${selectedDate?.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))}")

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Select Time: ")

                        TimePicker(state = newTimeState,
                            colors = TimePickerDefaults.colors(
                                selectorColor = ButtonColor, 
                                timeSelectorSelectedContainerColor = colorResource(id = R.color.main_blue),
                                periodSelectorUnselectedContainerColor = GreyTime,
                                periodSelectorSelectedContainerColor =  colorResource(id = R.color.main_blue),
                                clockDialColor = GreyTime,
                                timeSelectorUnselectedContainerColor = GreyTime
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(text = "Time is ${newTimeState.hour} : ${newTimeState.minute}")
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDialog = false

                            val newItem = CalendarItem(title, description, "${newTimeState.hour} : ${newTimeState.minute}")
                            val items = calendarItems.getOrDefault(selectedDate, mutableListOf())
                            calendarItems[selectedDate!!] = items.apply { add(newItem) }

                            description = ""
                            title = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ButtonColor)
                    ) {
                        Text("OK")
                    }
                }
            )
        }
        Spacer(modifier = Modifier.height(60.dp))
    }
}

data class CalendarItem constructor(val title: String, val description: String, val chosenTime: String)

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun CalendarScreenPreview() {
    val navController = rememberNavController()
    CalendarScreen(navController)
}
