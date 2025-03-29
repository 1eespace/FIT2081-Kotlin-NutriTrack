package com.fit2081.nutritrack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit2081.nutritrack.ui.theme.NutriTrackTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.io.BufferedReader
import java.io.InputStreamReader

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NutriTrackTheme {
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { NutritrackBottomBar(navController) }
                ) { innerPadding ->
                    NutritrackNavHost(innerPadding, navController)
                }
            }
        }
    }
}

// UserData
data class UserFoodQualityScoreData(
    val userID: String,
    val sex: String,
    val heifaTotalScoreMale: Double?,
    val heifaTotalScoreFemale: Double?,

)

// ✅ CSV Data
object FoodQualityScoreDataLoader {
    // Function to parse user data from the CSV file
    fun parseUserData(context: Context, fileName: String = "user_data.csv"): List<UserFoodQualityScoreData> {
        val userDetailsList = mutableListOf<UserFoodQualityScoreData>()

        // Open the CSV file from assets
        val inputStream = context.assets.open(fileName)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))

        // Skip the first line (header)
        bufferedReader.readLine()

        // Process each line of the CSV to create a UserData object
        bufferedReader.forEachLine { line ->
            val values = line.split(",")

            // Ensure there are enough fields in the line to process
            if (values.size >= 10) {

                val userId = values[1].trim()
                val sex = values[2].trim()

                // Parse scores for male and female users
                val heifaMale = values.getOrNull(3)?.toDoubleOrNull()
                val heifaFemale = values.getOrNull(4)?.toDoubleOrNull()

                // Add the parsed user to the list
                userDetailsList.add(
                    UserFoodQualityScoreData(
                        userID = userId,
                        sex = sex,
                        heifaTotalScoreMale = heifaMale,
                        heifaTotalScoreFemale = heifaFemale,

                    )
                )
            }
        }

        // Close the buffered reader
        bufferedReader.close()
        return userDetailsList
    }
}

@Preview(showBackground = true)
@Composable
fun HomePage(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("a1_nutriTrack", Context.MODE_PRIVATE)
    val savedUserId = sharedPreferences.getString("user_id", null)

    // Get all user data from the CSV file
    val users = FoodQualityScoreDataLoader.parseUserData(context)
    // Find the user by the saved user ID
    val user = users.find { it.userID == savedUserId }

    // Calculate food score based on user's sex
    val foodScore = remember(user) {
        when {
            user?.sex?.lowercase() == "male" -> user.heifaTotalScoreMale ?: 0.0
            user?.sex?.lowercase() == "female" -> user.heifaTotalScoreFemale ?: 0.0
            else -> 0.0
        }
    }
    val scoreText = "$foodScore/100"
    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = Color(0xFFF5F5DC)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 70.dp)
                .padding(16.dp)
        ) {
            Column(

            ) {
                Spacer(modifier = Modifier.height(10.dp))
                Column(

                ) {
                    // Display personalized greeting
                    Text(
                        text = "Hello,",
                        fontSize = 20.sp,
                        color = Color.Gray,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "User ID: ${user?.userID ?: "Unknown"}",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )

                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                )  {
                    Text(
                        text = "You've already filled in your Food intake Questionnaire, but you can change details here: " ,
                        fontSize = 12.sp,
                        modifier = Modifier.weight(1f)
                    )
                    // Add Edit Button
                    Button(
                        onClick = {
                            val intent = Intent(context, FoodIntakeQuestionaireActivity::class.java)
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF006400),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(0.dp),
                        modifier = Modifier.align(Alignment.CenterVertically),
                        // Add padding to the left and right of the button for spacing
                        contentPadding = PaddingValues(start = 12.dp, end = 12.dp)
                    ) {
                        Row(
                        ) {
                            // Edit Icon
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Edit Icon",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Edit",
                                fontSize = 15.sp
                            )

                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    contentAlignment = Alignment.TopCenter
                )
                { // Display image based on food quality score
                    when {
                        foodScore >= 80 -> {
                            Image(
                                painter = painterResource(id = R.drawable.high_quality),
                                contentDescription = "High Food Quality Score",
                                modifier = Modifier.size(200.dp)
                            )
                        }
                        foodScore >= 40 -> {
                            Image(
                                painter = painterResource(id = R.drawable.medium_quality),
                                contentDescription = "Medium Food Quality Score",
                                modifier = Modifier.size(200.dp)
                            )
                        }
                        else -> {
                            Image(
                                painter = painterResource(id = R.drawable.low_quality),
                                contentDescription = "Low Food Quality Score",
                                modifier = Modifier.size(200.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // "My Score"
                    Text(
                        text = "My Score",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f) // Half of Row
                    )
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // "See all scores"
                        Text(
                            text = "See all scores",
                            fontSize = 15.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.End
                        )

                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Arrow icon",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable {
                                    /* Action */
                                }
                        )
                    }
                }

                // Divider
                Divider(
                    color = Color.LightGray,
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(25.dp)
                                .background(Color(0xFF006400), shape = CircleShape)
                                .clickable { /* Action */ }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowUp,
                                contentDescription = "Arrow Up",
                                tint = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "Your Food Quality Score",
                            fontSize = 15.sp,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Black
                        )
                    }
                    Text(
                        text = "$scoreText",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF006400)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                // Divider
                Divider(
                    color = Color.LightGray,
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Add a brief description of what the score means
                Text(
                    text = "What is the Food Quality Score?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "Your Food Quality Score provides a snapshot of how well your eating patterns align with established food guidelines, helping you identify both strengths and opportunities for improvement in your diet."
                            + "\n\n"
                            + "The personalised measurment considers various food groups including vegetables, fruits, whole grains, and proteins to give you practical insights for making healthier food choices.",
                    fontSize = 13.sp,
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

// Navigation Bottom Bar
@Composable
fun NutritrackNavHost(innerPadding: PaddingValues, navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomePage()
        }
        composable("insights") {
            InsightsPage(modifier = Modifier.padding(innerPadding))
        }

        composable("nutricoach") {
            NutriCoachPage(innerPadding)
        }
        composable("settings") {
            SettingsPage(innerPadding)
        }
    }
}

@Composable
fun NutritrackBottomBar(navController: NavHostController) {
    val items = listOf(
        "home",
        "insights",
        "nutricoach",
        "settings"
    )
    var selectedItem by remember { mutableStateOf(0) }
    NavigationBar {
        items.forEachIndexed { index, item  ->
            NavigationBarItem(
                icon = {
                    when(item) {
                        "home" -> Icon(Icons.Filled.Home, contentDescription = "Home")
                        "insights" -> Icon(Icons.Filled.Notifications, contentDescription = "Insights")
                        "nutricoach" -> Icon(Icons.Filled.Face, contentDescription = "Nutricoach")
                        "settings" -> Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                },
                label = {Text(item)},
                selected = selectedItem == index,
                onClick = {
                    selectedItem = index
                    navController.navigate(item)
                }
            )
        }
    }
}

@Composable
fun NutriCoachPage(innerPadding: PaddingValues) {

}

@Composable
fun SettingsPage(innerPadding: PaddingValues) {

}
