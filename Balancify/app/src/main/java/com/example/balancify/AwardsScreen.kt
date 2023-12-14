package com.example.balancify

import android.preference.PreferenceManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.balancify.ui.theme.ButtonColor
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AwardsScreen(navController: NavController){
    val context = LocalContext.current
    val sharedPreferences = remember { PreferenceManager.getDefaultSharedPreferences(context) }

    val firebaseAuth = FirebaseAuth.getInstance()
    val currentUser = firebaseAuth.currentUser
    val userUid = currentUser?.uid

    val tasksDone = sharedPreferences.getStringSet("checkedTasks_$userUid", setOf()) ?: setOf()
    val gymSessions = sharedPreferences.getStringSet("checkedGymSessions_$userUid", setOf()) ?: setOf()

    var hasFirstTaskDoneAward by remember { mutableStateOf(false) }
    var hasFirstGymSessionDoneAward by remember { mutableStateOf(false) }
    var hasFirst5kmDoneAward by remember { mutableStateOf(false) }

    if(tasksDone.isNotEmpty()){
        hasFirstTaskDoneAward = true
    }

    if(gymSessions.isNotEmpty()){
        hasFirstGymSessionDoneAward = true
    }


    val subsetTitles = mutableListOf<String>()

    if(hasFirstTaskDoneAward){
        subsetTitles.add("First Task Done!")
    }

    if(hasFirstGymSessionDoneAward){
        subsetTitles.add("First Gym Session Done!")
    }

    if(hasFirst5kmDoneAward){
        subsetTitles.add("First 5 km ran!")
    }

    val subsetAwards = awardsList.filter { it.title in subsetTitles }

    Spacer(modifier = Modifier.height(10.dp))

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(0.dp),
        contentPadding = PaddingValues(top = 20.dp, start = 16.dp, end = 16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(45.dp))
            Text(
                text = "Check out your Awards!",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )
        }

        items(subsetAwards) { award ->
            AwardItem(award = award)
        }
    }
}

@Composable
fun AwardItem(award: Award) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clip(MaterialTheme.shapes.medium),
        color = ButtonColor
    ){
        Column {
            Text(text = award.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(text = award.description)
        }
    }
}

data class Award(val title: String, val description: String)

val awardsList = listOf(
    Award("First Task Done!", "Well done, you finished your first task!"),
    Award("First Gym Session Done!", "Well done, you finished your first gym session!"),
    Award("First 5 km ran!", "Well done, you finished your first 5 km!")
)
