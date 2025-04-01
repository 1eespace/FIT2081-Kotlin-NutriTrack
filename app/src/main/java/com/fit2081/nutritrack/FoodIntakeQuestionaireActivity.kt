package com.fit2081.nutritrack

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar
import com.fit2081.nutritrack.ui.theme.NutriTrackTheme

class FoodIntakeQuestionaireActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NutriTrackTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    QuestionnairePage(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
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

@Preview(showBackground = true)
@Composable
fun QuestionnairePage(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("a1_nutriTrack", Context.MODE_PRIVATE)
    val userId = sharedPref.getString("user_id", "default_user") ?: "default_user"

    val selectedPersona = remember { mutableStateOf(personas[0]) }
    val biggestMealTime = remember { mutableStateOf("00:00") }
    val sleepTime = remember { mutableStateOf("00:00") }
    val wakeTime = remember { mutableStateOf("00:00") }

    val selectedCategories = remember {
        mutableStateMapOf<String, Boolean>().apply {
            listOf(
                "Fruits", "Vegetables", "Grains", "Red Meat", "Seafood",
                "Poultry", "Fish", "Eggs", "Nuts/Seeds"
            ).forEach { put(it, false) }
        }
    }

    LaunchedEffect(Unit) {
        loadSavedData(context, userId, selectedPersona, biggestMealTime, sleepTime, wakeTime, selectedCategories)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF5F5DC)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            HeaderTopAppBar(title = "Food Intake Questionnaire", context = context)
            Divider(
                color = Color.Gray,
                thickness = 2.dp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(5.dp))

            // Food categories selection section
            FoodCategorySelection(selectedCategories)

            // Persona selection section
            PersonaSelection(selectedPersona)

            // Make space between Dropdown and Meal Timings
            Spacer(modifier = Modifier.height(8.dp))

            // Meal timings section
            Text("Meal Timings:", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            // Biggest Meal Time
            MealTimePicker(label = "What time of day approx. do you normally eat your biggest meal?", timeState = biggestMealTime)
            Spacer(modifier = Modifier.height(3.dp))
            // Sleep Time
            MealTimePicker(label = "What time of day approx. do you go to sleep at night?", timeState = sleepTime)
            Spacer(modifier = Modifier.height(3.dp))
            // Wake-up Time
            MealTimePicker(label = "What time of day approx. do you wake up in the morning?", timeState = wakeTime)

            // Make space between Timing and Save button
            Spacer(modifier = Modifier.height(16.dp))

            // Save Button
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(
                    onClick = {
                        if (selectedCategories.values.any { it }) { // Checking for users choose at least one food category
                            val sharedPref = context.getSharedPreferences("a1_nutriTrack", Context.MODE_PRIVATE).edit()

                            sharedPref.putString("selectedPersona_$userId", selectedPersona.value)
                            sharedPref.putString("biggestMealTime_$userId", biggestMealTime.value)
                            sharedPref.putString("sleepTime_$userId", sleepTime.value)
                            sharedPref.putString("wakeTime_$userId", wakeTime.value)

                            selectedCategories.forEach { (category, isChecked) ->
                                sharedPref.putBoolean("category_${userId}_$category", isChecked)
                            }

                            sharedPref.apply()
                            navigateToHome(context)
                        } else {
                            // Show Toast error message
                            Toast.makeText(context, "⚠️ At least one food category must be selected!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF006400),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RectangleShape,
                ) {
                    Text("Save")
                }
            }
        }
    }
}

// Header
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderTopAppBar(
    title: String,
    context: Context
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color(0xFFF5F5DC),
        ),
        title = {
            Text(
                title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = { navigateToHome(context) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"

                )
            }
        }
    )
}

// Food Category Selection
@Composable
fun FoodCategorySelection(
    selectedCategories: MutableMap<String, Boolean>
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Tick all the food categories you can eat:",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 2.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(selectedCategories.keys.toList()) { category ->
                Row(verticalAlignment = Alignment.CenterVertically) {
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

// Persona selection
@Composable
fun PersonaSelection(selectedPersona: MutableState<String>) {
    var expanded by remember { mutableStateOf(false) }
    var selectedModalPersona by remember { mutableStateOf<String?>(null) }
    var showModal by remember { mutableStateOf(false) }

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

        // Description Text
        Text(
            "People can be broadly classified into 6 different types based on their eating preferences.",
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        // 6 buttons for persona selection
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(personas) { persona ->
                Button(
                    onClick = {
                        selectedModalPersona = persona
                        showModal = true
                    },
                    modifier = Modifier
                        .height(60.dp)
                        .padding(4.dp),
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

        // Column for Button and Dropdown
        Column(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006400))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        // Default text
                        text = if (selectedPersona.value.isEmpty()) "Select option" else selectedPersona.value,
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
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                personas.forEach { persona ->
                    DropdownMenuItem(
                        text = {
                            Text(persona, fontSize = 14.sp) },
                        onClick = {
                            selectedPersona.value = persona
                            // Close
                            expanded = false
                        }
                    )
                }
            }
        }

        // Show the Modal
        if (showModal && selectedModalPersona != null) {
            ShowPersonaModal(
                personaName = selectedModalPersona!!,
                personaDescription = personaDescriptions[selectedModalPersona!!] ?: "No description available.",
                personaImage = painterResource(id = personaImages[selectedModalPersona!!] ?: R.drawable.default_image),
                onDismiss = { showModal = false }
            )
        }
    }
}

// Show modal function
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
            Column(
            ) {
                // Image at the top
                Image(
                    painter = personaImage,
                    contentDescription = personaName,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Persona name
                Text(
                    text = personaName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = personaDescription,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Start
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
            ) {
                Text("Dismiss")
            }
        }
    )
}

// TimePicker
@Composable
fun MealTimePicker(label: String, timeState: MutableState<String>) {
    val context = LocalContext.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Text(text = label, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = { showTimePicker(context, timeState) },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            modifier = Modifier.width(110.dp)
        ) {
            Text(timeState.value, fontSize = 15.sp, color = Color.Black)
            Spacer(modifier = Modifier.width(5.dp))
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Search Icon",
                modifier = Modifier.size(18.dp),
                tint = Color.Black
            )
        }
    }
}

// TimePickerDialog
fun showTimePicker(context: Context, timeState: MutableState<String>) {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    TimePickerDialog(
        context,
        { _, selectedHour, selectedMinute ->
            val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            timeState.value = formattedTime
        },
        hour,
        minute,
        true
    ).show()
}

// load saved data
fun loadSavedData(
    context: Context,
    // User ID: Different data load depends on ID
    userId: String,
    selectedPersona: MutableState<String>,
    biggestMealTime: MutableState<String>,
    sleepTime: MutableState<String>,
    wakeTime: MutableState<String>,
    selectedCategories: MutableMap<String, Boolean>
) {
    val sharedPref = context.getSharedPreferences("a1_nutriTrack", Context.MODE_PRIVATE)

    selectedPersona.value = sharedPref.getString("selectedPersona_$userId", "") ?: ""
    biggestMealTime.value = sharedPref.getString("biggestMealTime_$userId", "00:00") ?: "00:00"
    sleepTime.value = sharedPref.getString("sleepTime_$userId", "00:00") ?: "00:00"
    wakeTime.value = sharedPref.getString("wakeTime_$userId", "00:00") ?: "00:00"

    selectedCategories.keys.forEach { category ->
        selectedCategories[category] = sharedPref.getBoolean("category_${userId}_$category", false)
    }
}


fun navigateToHome(context: Context) {
    context.startActivity(Intent(context, HomeActivity::class.java).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    })
}

