package com.fit2081.yeeun34752110.insights

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.fit2081.yeeun34752110.AppViewModelFactory
import com.fit2081.yeeun34752110.ui.theme.NutriTrackTheme

class InsightsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NutriTrackTheme {
            }
        }
    }
}

@Composable
fun InsightsPage(
    userId: Int,
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val context = LocalContext.current
    val viewModel: InsightsViewModel = viewModel(factory = AppViewModelFactory(context))

    LaunchedEffect(userId) {
        viewModel.loadPatientScoresById(userId)
    }

    val patient by viewModel.patient.collectAsState()
    val fruitsScore by viewModel.fruitsScore.collectAsState()

    patient?.let { data ->
        val categories = listOf(
            "Discretionary Foods" to Pair(data.discretionaryFoods, 10),
            "Vegetables" to Pair(data.vegetables, 10),
            "Fruits" to Pair(data.fruits, 10),
            "Grains & Cereals" to Pair(data.grainsAndCereals, 5),
            "Whole Grains" to Pair(data.wholeGrains, 5),
            "Meat & Alternatives" to Pair(data.meatAndAlternatives, 10),
            "Dairy & Alternatives" to Pair(data.dairyAndAlternatives, 10),
            "Water" to Pair(data.water, 5),
            "Saturated Fat" to Pair(data.saturatedFats, 5),
            "Unsaturated Fats" to Pair(data.unsaturatedFats, 5),
            "Sodium" to Pair(data.sodium, 10),
            "Sugars" to Pair(data.sugars, 10),
            "Alcohol" to Pair(data.alcohol, 5)
        )

        val totalScore = categories.sumOf { it.second.first.toDouble() }.toFloat()
        val totalMaxScore = categories.sumOf { it.second.second }

        // Main content: scrollable
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .widthIn(max = 800.dp)
                        .padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Insights: Food Score",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // List of score items
                    categories.forEach { (category, pair) ->
                        FoodScoreItem(name = category, score = pair.first, maxScore = pair.second)
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Total score summary
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Food Quality Score", fontWeight = FontWeight.Bold)
                        Text(
                            String.format("%.1f/%d", totalScore, totalMaxScore),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Total score slider (disabled)
                    Slider(
                        value = totalScore,
                        onValueChange = {},
                        enabled = false,
                        valueRange = 0f..totalMaxScore.toFloat(),
                        colors = SliderDefaults.colors(
                            disabledThumbColor = Color(0xFF203A84),
                            disabledActiveTrackColor = Color(0xFF203A84)
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Update Fruits Score
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Give motivation to users for fruits optimal score
                            Text(
                                text = "Reach 10 to unlock a surprise from your NutriCoach!",
                                fontSize = 14.sp,
                                color = Color(0xFF056207),
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 4.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Improve My Fruits Score",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = String.format("%.1f", fruitsScore),
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Slider(
                                value = fruitsScore,
                                onValueChange = { viewModel.onFruitsScoreChange(it) },
                                valueRange = 0f..10f,
                                steps = 9,
                                modifier = Modifier.fillMaxWidth(),
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFF203A84),
                                    activeTrackColor = Color(0xFF203A84)
                                )
                            )

                            Text(
                                text = "Eating at least 2 servings of 2 diverse fruits daily helps you reach the optimal score (10/10) — try it out!",
                                fontSize = 14.sp,
                                color = Color(0xFF056207),
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 4.dp)
                            )

                            Button(
                                onClick = {
                                    viewModel.updateFruitsScore(data.patientId, viewModel.fruitsScore.value)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RectangleShape,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF056207))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Save Icon",
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Try It Out!", color = Color.White)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Share Button
                        Button(
                            onClick = {
                                val userScores = categories.associate { it.first to it.second.first }
                                viewModel.sharingInsights(
                                    context,
                                    userScores,
                                    totalScore,
                                    totalMaxScore.toFloat()
                                )
                            },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RectangleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF056207))
                        ) {
                            Text("Share with someone", color = Color.White)
                        }

                        // Navigate Button
                        Button(
                            onClick = {
                                navController.navigate("nutricoach") {
                                    popUpTo(navController.graph.findStartDestination().route ?: "home") {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RectangleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF056207))
                        ) {
                            Text("Improve my diet!", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FoodScoreItem(name: String, score: Float, maxScore: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(name, modifier = Modifier.weight(2f), fontSize = 12.sp, fontWeight = FontWeight.Bold)

        Slider(
            value = score,
            onValueChange = {},
            valueRange = 0f..maxScore.toFloat(),
            enabled = false,
            modifier = Modifier.weight(3f),
            colors = SliderDefaults.colors(
                disabledThumbColor = Color(0xFF203A84),
                disabledActiveTrackColor = Color(0xFF203A84)
            )
        )

        Text(
            text = String.format("%.1f/%d", score, maxScore),
            fontSize = 12.sp,
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Bold
        )
    }
}