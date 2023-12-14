package com.example.balancify

import android.content.Context
import android.preference.PreferenceManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PartyMode
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.balancify.ui.theme.DarkBlue
import com.example.balancify.ui.theme.LightBlue
import com.example.balancify.ui.theme.MiddleBlue
import com.example.balancify.ui.theme.UniBlue
//import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

enum class GoalCategory(val title: String, var color: Color) {
    UNI("Uni", UniBlue),
    SPORT("Sport", MiddleBlue),
    PARTY("Party", LightBlue)
}

@Composable
fun ToDoScreen(navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }
    var goals by remember { mutableStateOf(listOf(Goal("Default Goal", "Description", GoalCategory.UNI))) }

    // load goals from SharedPreferences when the screen is first created
    val context = LocalContext.current
    val sharedPreferences = remember { PreferenceManager.getDefaultSharedPreferences(context) }
    val savedGoalsJson = sharedPreferences.getString("goals", null)

    var showDoneTasks by remember{ mutableStateOf(true) }

    if (savedGoalsJson != null) {
        goals = Gson().fromJson(savedGoalsJson, object : TypeToken<List<Goal>>() {}.type)
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(text = "Add item:")
            IconButton(
                onClick = { showDialog = true },
                modifier = Modifier
                    .size(48.dp)
                    .background(color = colorResource(id = R.color.main_blue), shape = CircleShape)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add item")
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
        ) {
            items(goals.filter { showDoneTasks || !it.isCompleted }) { goal ->
                GoalItem (
                    goal = goal,
                    onDeleteClick = {
                        // Remove the goal from the list when the delete button is clicked
                        goals = goals.toMutableList().apply { remove(goal) }

                        // Update goals in SharedPreferences
                        saveGoalsToSharedPreferences(context, goals)
                    },
                )
            }
        }
        Spacer(modifier = Modifier.height(60.dp))

        if (showDialog) {
            AddGoalDialog(
                onDismiss = { showDialog = false },
                onGoalCreated = { newGoal ->
                    goals = goals + newGoal
                    showDialog = false

                    // Save goals to SharedPreferences when a new goal is created
                    saveGoalsToSharedPreferences(context, goals)
                }
            )
        }
    }

}

private fun saveGoalsToSharedPreferences(context: Context, goals: List<Goal>) {
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    val editor = sharedPreferences.edit()
    editor.putString("goals", Gson().toJson(goals))
    editor.apply()
}

@Composable
fun AddGoalDialog(
    onDismiss: () -> Unit,
    onGoalCreated: (Goal) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(GoalCategory.UNI) }
    var expanded by remember { mutableStateOf(false)}

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text("Add Goal")
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {

                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                }

                item {
                    TextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        singleLine = false,
                        maxLines = Int.MAX_VALUE,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .background(color = Color.White)
                    )
                }
                item{
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .height(IntrinsicSize.Max)
                    ) {
                        OutlinedButton(
                            onClick = { expanded = true },
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text("Select Category: ${category.title}")
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            for (cat in GoalCategory.values()) {
                                DropdownMenuItem(
                                    text = {
                                        Text(text = cat.title, color = Color.Black)
                                           },
                                    onClick = {
                                        category = cat
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            IconButton(
                onClick = {
                    onGoalCreated(Goal(title, description, category))
                }
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = "Create Goal")
            }
        },
        dismissButton = {
            IconButton(
                onClick = { onDismiss() }
            ) {
                Icon(imageVector = Icons.Default.Clear, contentDescription = "Dismiss")
            }
        }
    )
}

@Composable
fun GoalItem(goal: Goal, onDeleteClick: () -> Unit) {
    var isChecked by remember { mutableStateOf(false) }

    // Update the color based on the checkbox state
    val itemColor = if (isChecked) Color.Gray else goal.category.color

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(MaterialTheme.shapes.medium),
        color = itemColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = goal.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)

                Icon(
                    imageVector = getCategoryIcon(goal.category),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .padding(start = 8.dp)
                )
            }

            Text(text = goal.description)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = {
                        isChecked = !isChecked
                        goal.isCompleted = isChecked
                    },
                    modifier = Modifier.padding(end = 8.dp)
                )

                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
fun getCategoryIcon(category: GoalCategory): ImageVector {
    return when (category) {
        GoalCategory.UNI -> Icons.Default.School
        GoalCategory.SPORT -> Icons.Default.SportsSoccer
        GoalCategory.PARTY -> Icons.Default.Celebration
    }
}

data class Goal(val title: String, val description: String, val category: GoalCategory, var isCompleted: Boolean = false)