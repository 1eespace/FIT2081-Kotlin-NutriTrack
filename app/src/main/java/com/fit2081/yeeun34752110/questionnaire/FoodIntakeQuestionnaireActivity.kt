package com.fit2081.yeeun34752110.questionnaire

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.foundation.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.fit2081.yeeun34752110.R
import com.fit2081.yeeun34752110.AppViewModelFactory
import com.fit2081.yeeun34752110.databases.AuthManager
import com.fit2081.yeeun34752110.databases.foodintakedb.FoodIntakeRepository
import com.fit2081.yeeun34752110.home.HomeActivity
import java.util.Calendar
import com.fit2081.yeeun34752110.ui.theme.NutriTrackTheme

class FoodIntakeQuestionnaireActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NutriTrackTheme {
                val navController = rememberNavController()
                val patientId = AuthManager.getPatientId()?.toIntOrNull() ?: -1

                QuestionnairePage(
                    patientId = patientId,
                    navController = navController
                )
            }
        }
    }
}

// Persona Data
val personas = listOf(
    "Health Devotee", "Mindful Eater", "Wellness Striver",
    "Balance Seeker", "Health Procrastinator", "Food Carefree"
)

val personaDescriptions = mapOf(
    "Health Devotee" to "I’m passionate about healthy eating & health plays a big part in my life. I use social media to follow active lifestyle personalities or get new recipes/exercise ideas. I may even buy superfoods or follow a particular type of diet. I like to think I am super healthy.",
    "Mindful Eater" to "I’m health-conscious and being healthy and eating healthy is important to me. Although health means different things to different people, I make conscious lifestyle decisions about eating based on what I believe healthy means. I look for new recipes and healthy eating information on social media.",
    "Wellness Striver" to "I aspire to be healthy (but struggle sometimes). Healthy eating is hard work! I’ve tried to improve my diet, but always find things that make it difficult to stick with the changes. Sometimes I notice recipe ideas or healthy eating hacks, and if it seems easy enough, I’ll give it a go.",
    "Balance Seeker" to "I try and live a balanced lifestyle, and I think that all foods are okay in moderation. I shouldn’t have to feel guilty about eating a piece of cake now and again. I get all sorts of inspiration from social media like finding out about new restaurants, fun recipes and sometimes healthy eating tips.",
    "Health Procrastinator" to "I’m contemplating healthy eating but it’s not a priority for me right now. I know the basics about what it means to be healthy, but it doesn’t seem relevant to me right now. I have taken a few steps to be healthier but I am not motivated to make it a high priority because I have too many other things going on in my life.",
    "Food Carefree" to "I’m not bothered about healthy eating. I don’t really see the point and I don’t think about it. I don’t really notice healthy eating tips or recipes and I don’t care what I eat."
)

val personaImages = mapOf(
    "Health Devotee" to R.drawable.health_devotee,
    "Mindful Eater" to R.drawable.mindful_eater,
    "Wellness Striver" to R.drawable.wellness_striver,
    "Balance Seeker" to R.drawable.balance_seeker,
    "Health Procrastinator" to R.drawable.health_procrastinator,
    "Food Carefree" to R.drawable.food_carefree
)

@Composable
fun QuestionnairePage(
    patientId: Int,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModel: FoodIntakeQuestionnaireViewModel = viewModel(
        factory = AppViewModelFactory(context)
    )

    // Load saved data so user can edit
    LaunchedEffect(patientId) {
        viewModel.loadExistingFoodIntake(patientId)
    }

    Scaffold(
        topBar = {
            HeaderTopAppBar(
                title = "Food Intake Questionnaire",
                patientId = patientId,
                repository = viewModel.repository,
                onBack = { navController.popBackStack() }
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .widthIn(max = 800.dp)
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Food categories
                    FoodCategorySelection(viewModel)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Persona selection
                    PersonaSelection(viewModel)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Meal timings
                    Text(
                        text = "Meal Timings:",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    MealTimePicker("Time you sleep?", "sleep", viewModel)
                    MealTimePicker("Time you wake up?", "wake", viewModel)
                    MealTimePicker("Time of your biggest meal?", "meal", viewModel)

                    Spacer(modifier = Modifier.height(24.dp))

                    // Save button
                    Button(
                        onClick = {
                            val wake = viewModel.wakeTime
                            val meal = viewModel.biggestMealTime
                            val sleep = viewModel.sleepTime

                            if (!isTimeOrderValid(wake, meal, sleep)) {
                                Toast.makeText(
                                    context,
                                    "Your biggest meal time must be between wake up time and sleep time.",
                                    Toast.LENGTH_LONG
                                ).show()
                                return@Button
                            }

                            viewModel.saveFoodIntake(
                                userId = patientId,
                                selectedCategories = viewModel.selectedCategories,
                                biggestMealTime = meal,
                                sleepTime = sleep,
                                wakeTime = wake,
                                selectedPersona = viewModel.selectedPersona,
                                onSuccess = {
                                    context.startActivity(Intent(context, HomeActivity::class.java).apply {
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    })
                                },
                                onError = { errorMsg ->
                                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                                }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006400)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RectangleShape
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderTopAppBar(
    title: String,
    patientId: Int,
    repository: FoodIntakeRepository,
    // optional callback for navigation
    onBack: (() -> Unit)? = null
) {
    var hasCompleted by remember { mutableStateOf(false) }

    LaunchedEffect(patientId) {
        runCatching {
            repository.getFoodIntakeByPatientId(patientId)
        }.onSuccess {
            hasCompleted = it != null
        }.onFailure {
            hasCompleted = false
        }
    }

    CenterAlignedTopAppBar(
        title = {
            Text(title, maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black)
        },
        navigationIcon = {
            if (hasCompleted && onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        }
    )
}

@Composable
fun FoodCategorySelection(viewModel: FoodIntakeQuestionnaireViewModel) {
    val selectedCategories = viewModel.selectedCategories

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Tick all the food categories you can eat:",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 2.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 200.dp)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(selectedCategories.keys.toList()) { category ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Checkbox(
                            checked = selectedCategories[category] ?: false,
                            onCheckedChange = { isChecked ->
                                selectedCategories[category] = isChecked
                            }
                        )
                        Text(text = category, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}


// Persona selection
@Composable
fun PersonaSelection(viewModel: FoodIntakeQuestionnaireViewModel) {
    val selectedPersona = viewModel.selectedPersona
    val showModal = viewModel.showPersonaModal
    val selectedModalPersona = viewModel.selectedModalPersona
    val expanded = viewModel.personaDropdownExpanded

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Select Your Persona",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(bottom = 5.dp)
                .align(Alignment.Start)
        )

        Text(
            "People can be broadly classified into 6 different types based on their eating preferences.",
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        val rows = personas.chunked(3)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            rows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    row.forEach { persona ->
                        Button(
                            onClick = {
                                viewModel.togglePersonaModal(true, persona)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(60.dp),
                            shape = RectangleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF203A84))
                        ) {
                            Text(
                                text = persona,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    if (row.size < 3) {
                        repeat(3 - row.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Which persona best fits you?",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(bottom = 5.dp)
                .align(Alignment.Start)
        )

        Column(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = { viewModel.toggleDropdown(!expanded) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006400))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (selectedPersona.isEmpty()) "Select option" else selectedPersona,
                        fontSize = 14.sp,
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Expand",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { viewModel.toggleDropdown(false) },
                modifier = Modifier.fillMaxWidth()
            ) {
                personas.forEach { persona ->
                    DropdownMenuItem(
                        text = { Text(persona, fontSize = 14.sp) },
                        onClick = {
                            viewModel.updateSelectedPersona(persona)
                            viewModel.toggleDropdown(false)
                        }
                    )
                }
            }
        }

        if (showModal && selectedModalPersona != null) {
            ShowPersonaModal(
                personaName = selectedModalPersona,
                personaDescription = personaDescriptions[selectedModalPersona] ?: "No description available.",
                personaImage = painterResource(id = personaImages[selectedModalPersona] ?: R.drawable.default_image),
                onDismiss = { viewModel.togglePersonaModal(false) }
            )
        }
    }
}

@Composable
fun ShowPersonaModal(
    personaName: String,
    personaDescription: String,
    personaImage: Painter,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = personaName,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                textAlign = TextAlign.Start
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp)
            ) {
                item {
                    Image(
                        painter = personaImage,
                        contentDescription = personaName,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(10.dp))

                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    Text(
                        text = personaDescription,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Start
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF203A84))
            ) {
                Text("Dismiss")
            }
        }
    )
}


@Composable
fun MealTimePicker(label: String, key: String, viewModel: FoodIntakeQuestionnaireViewModel) {
    val time = when (key) {
        "meal" -> viewModel.biggestMealTime
        "sleep" -> viewModel.sleepTime
        "wake" -> viewModel.wakeTime
        else -> ""
    }

    val context = LocalContext.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, fontSize = 14.sp, modifier = Modifier.weight(1f))

        Spacer(modifier = Modifier.width(8.dp))

        Button(
            onClick = {
                showTimePicker(context, time) {
                    viewModel.updateMealTime(key, it)
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            modifier = Modifier.width(120.dp)
        ) {
            Text(formatTimeToDisplay(time), fontSize = 15.sp, color = Color.Black)
            Spacer(modifier = Modifier.width(5.dp))
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Icon",
                modifier = Modifier.size(18.dp),
                tint = Color.Black
            )
        }
    }
}

fun formatTimeToDisplay(time24: String): String {
    return try {
        val parts = time24.split(":")
        val hour = parts[0].toInt()
        val minute = parts[1].toInt()
        val amPm = if (hour < 12) "AM" else "PM"
        val hour12 = if (hour % 12 == 0) 12 else hour % 12
        String.format("%02d:%02d %s", hour12, minute, amPm)
    } catch (e: Exception) {
        time24 // fallback
    }
}

fun showTimePicker(
    context: Context,
    currentTime: String,
    onTimeSelected: (String) -> Unit
) {
    val calendar = Calendar.getInstance()
    val hour = currentTime.split(":").getOrNull(0)?.toIntOrNull() ?: calendar.get(Calendar.HOUR_OF_DAY)
    val minute = currentTime.split(":").getOrNull(1)?.toIntOrNull() ?: calendar.get(Calendar.MINUTE)

    TimePickerDialog(
        context,
        { _, selectedHour, selectedMinute ->
            val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            onTimeSelected(formattedTime)
        },
        hour,
        minute,
        true
    ).show()
}

fun hasDuplicateTimes(vararg times: String): Boolean {
    return times.toSet().size != times.size
}

// wakeup -> meal -> sleep
fun isTimeOrderValid(wake: String, meal: String, sleep: String): Boolean {
    return try {
        val wakeMins = convertToMinutes(wake)
        val mealMins = convertToMinutes(meal)
        val sleepMins = convertToMinutes(sleep)

        // Time order: wake < meal < sleep
        wakeMins < mealMins && mealMins < sleepMins
    } catch (e: Exception) {
        false
    }
}

fun convertToMinutes(time: String): Int {
    val (h, m) = time.split(":").map { it.toInt() }
    return h * 60 + m
}
