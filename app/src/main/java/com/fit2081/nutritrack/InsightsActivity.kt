package com.fit2081.nutritrack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit2081.nutritrack.ui.theme.NutriTrackTheme
import java.io.BufferedReader
import java.io.InputStreamReader

class InsightsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NutriTrackTheme {
                InsightsPage(modifier = Modifier)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InsightsPage(modifier: Modifier = Modifier, innerPadding: PaddingValues = PaddingValues(16.dp)) {

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("a1_nutriTrack", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("user_id", null)?.toIntOrNull()

    // Load user scores from CSV based on the user ID
    val userScores = remember { userId?.let { getUserScores(context, it) } } ?: emptyMap()

    // Define food categories' max score (Based on expected output pdf)
    val categories = listOf(
        "Discretionary Foods" to 10,
        "Vegetables" to 5,
        "Fruits" to 5,
        "Grains & Cereals" to 5,
        "Whole Grains" to 10,
        "Meat & Alternatives" to 10,
        "Dairy & Alternatives" to 10,
        "Water" to 5,
        "Unsaturated Fats" to 10,
        "Sodium" to 10,
        "Sugars" to 10,
        "Alcohol" to 5
    )

    // Calculate total score
    val rawTotalScore = categories.sumOf { (category, _) -> (userScores[category] ?: 0f).toDouble() }
    val rawMaxScore = categories.sumOf { it.second.toDouble() }
    val totalScore = (rawTotalScore / rawMaxScore) * 100

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF5F5DC)
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    Text(
                        // Page title
                        text = "Insights: Food Score",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                }

                // Display food category scores using slider
                items(categories) { (categoryName, maximumScore) ->
                    val score = userScores[categoryName] ?: 0
                    FoodScoreItem(categoryName, score, maximumScore)
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total Food Quality Score",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )

                        // Total score displayed on the right
                        Text(
                            text = String.format("%.2f/100", totalScore?.toFloat() ?: 0f),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Slider( // Slider for total score
                        value = totalScore.toFloat(),
                        onValueChange = {},
                        valueRange = 0f..100f,
                        enabled = false,
                        colors = SliderDefaults.colors(
                            disabledThumbColor = Color(0xFF203A84),
                            disabledActiveTrackColor = Color(0xFF203A84)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Two buttons: Share and Improve diet
                    Button(
                        onClick = { shareScore(context, totalScore) },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RectangleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF056207))
                    ) {
                        Text(text = "Share with someone")
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            Toast.makeText(context, "NutriCoach in Development", Toast.LENGTH_LONG).show()
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RectangleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF056207))
                    ) {
                        Text(text = "Improve my diet!")
                    }
                }
            }
        }
    }
}

// Food score
@Composable
fun FoodScoreItem(name: String, score: Number, maxScore: Int) {
    Row(
        modifier = Modifier.fillMaxWidth()
        .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Category Name
        Text(
            text = name,
            modifier = Modifier.weight(2f),
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.width(2.dp))

        // Category score slider
        Slider(
            value = score.toFloat(),
            onValueChange = {},
            // Standardising all categories
            valueRange = 0f..maxScore.toFloat(),
            enabled = false,
            modifier = Modifier
                .weight(3f) // Consistent slider width
                .align(Alignment.CenterVertically),
            colors = SliderDefaults.colors(
                disabledThumbColor = Color(0xFF203A84),
                disabledActiveTrackColor = Color(0xFF203A84)
            )
        )

        Spacer(modifier = Modifier.width(2.dp))

        // Display the actual score
        Text(
            // 2 decimal
            text = String.format("%.2f/%d", score.toFloat(), maxScore),
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        )
    }
}

fun shareScore(context: Context, totalScore: Double) {
    val sharedPref = context.getSharedPreferences("a1_nutriTrack", Context.MODE_PRIVATE)
    val storedUserId = sharedPref.getString("user_id", "Unknown User")

    val shareText = """
        🌱 **My Nutrition Insights** 🌱
        -----------------------------------
        User ID: $storedUserId

        **Total Score:** ${totalScore.toInt()} / 100
        -----------------------------------
        📌 **About NutriTrack App**
        NutriTrack helps you monitor and improve your dietary habits by tracking food quality scores.
        It provides insights based on nutritional recommendations to help you make healthier choices.
        
        🌿 Stay mindful of your nutrition and live a healthier life! 🍏
        
        Download NutriTrack now and start your journey to better eating habits!
    """.trimIndent()

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
    }

    val chooserIntent = Intent.createChooser(shareIntent, "Share your nutrition score via")

    // Add flag to bring app back to the insight screen after sharing
    chooserIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
    context.startActivity(chooserIntent)
}

fun getUserScores(context: Context, userId: Int): MutableMap<String, Float>? {
    try {
        val inputStream = context.assets.open("user_data.csv")
        val reader = BufferedReader(InputStreamReader(inputStream))
        val lines = reader.readLines()

        if (lines.isEmpty()) return null

        val headers = lines.first().split(",").map { it.trim() }
        val dataRows = lines.drop(1)

        // Locate the user data based on User ID
        val userRow = dataRows
            .firstOrNull { it.split(",").getOrNull(headers.indexOf("User_ID")) == userId.toString() }
            ?.split(",")?.map { it.trim() } ?: return null

        val isFemale = userRow.getOrNull(headers.indexOf("Sex"))?.lowercase() == "female"

        // Mapping CSV columns to food categories each
        val scoreMapping = mapOf(
            "Discretionary Foods" to "DiscretionaryHEIFAscore",
            "Vegetables" to "VegetablesHEIFAscore",
            "Fruits" to "FruitHEIFAscore",
            "Grains & Cereals" to "GrainsandcerealsHEIFAscore",
            "Whole Grains" to "WholegrainsHEIFAscore",
            "Meat & Alternatives" to "MeatandalternativesHEIFAscore",
            "Dairy & Alternatives" to "DairyandalternativesHEIFAscore",
            "Water" to "WaterHEIFAscore",
            "Unsaturated Fats" to "UnsaturatedFatHEIFAscore",
            "Sodium" to "SodiumHEIFAscore",
            "Sugars" to "SugarHEIFAscore",
            "Alcohol" to "AlcoholHEIFAscore"
        )

        val scores = mutableMapOf<String, Float>()
        // Select appropriate colum based on gender (Female/Male)
        for ((category, baseColumn) in scoreMapping) {
            val columnName = if (isFemale) "${baseColumn}Female" else "${baseColumn}Male"
            val columnIndex = headers.indexOf(columnName).takeIf { it != -1 } ?: headers.indexOf(baseColumn)

            scores[category] = userRow.getOrNull(columnIndex)?.toFloatOrNull() ?: 0f
        }

        reader.close()
        return scores
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}
