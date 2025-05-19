package com.fit2081.yeeun34752110.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.fit2081.yeeun34752110.R
import com.fit2081.yeeun34752110.AppViewModelFactory
import com.fit2081.yeeun34752110.MainActivity
import com.fit2081.yeeun34752110.genai.chatbot.ChatBotFABWithModal

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(userId: Int, navController: NavHostController, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val viewModel: HomeViewModel = viewModel(factory = AppViewModelFactory(context))

    // Load patient data when the screen is first launched
    LaunchedEffect(Unit) {
        viewModel.loadPatientDataById(userId)
    }

    val patient by viewModel.patient.collectAsState()

    patient?.let { patientData ->
        val foodQualityScore = patientData.totalScore
        val name = patientData.patientName

        Box(modifier = Modifier.fillMaxSize()) {
            // Main content scrollable area
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .widthIn(max = 800.dp)
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Spacer(modifier = Modifier.height(40.dp))

                        Text(
                            text = "Hello, $name",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF006400)
                        )
                        Text(
                            text = "User ID: $userId",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Normal
                        )

                        Spacer(Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "You've already filled in your Food intake Questionnaire, but you can change details here:",
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                onClick = {
                                    navController.navigate("questionnaire")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006400)),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(0.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Icon(Icons.Filled.Edit, contentDescription = "Edit", modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Edit", fontSize = 14.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Show food quality image
                        val imageRes = when {
                            foodQualityScore.toInt() >= 80 -> R.drawable.high_quality
                            foodQualityScore.toInt() >= 40 -> R.drawable.medium_quality
                            else -> R.drawable.low_quality
                        }

                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = imageRes),
                                contentDescription = "Score Image",
                                modifier = Modifier.size(200.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "My Score",
                                fontSize = 25.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable {
                                    navController.navigate("insights") {
                                        popUpTo(navController.graph.findStartDestination().route ?: "home") {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            ) {
                                Text("See all scores", fontSize = 15.sp, color = Color.Gray)
                                Icon(Icons.Filled.PlayArrow, contentDescription = "Arrow", modifier = Modifier.size(20.dp))
                            }
                        }

                        Divider(color = Color.LightGray, thickness = 1.dp)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(25.dp)
                                    .background(Color(0xFF006400), shape = CircleShape)
                            ) {
                                Icon(
                                    Icons.Filled.KeyboardArrowUp,
                                    contentDescription = "Arrow Up",
                                    tint = Color.White,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "Your Food Quality Score",
                                fontSize = 15.sp,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                "$foodQualityScore/100",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF006400)
                            )
                        }

                        Divider(color = Color.LightGray, thickness = 1.dp)

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            "What is the Food Quality Score?",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = "Your Food Quality Score provides a snapshot of how well your eating patterns align with established food guidelines, helping you identify both strengths and opportunities for improvement in your diet.\n\n" +
                                    "The personalised measurement considers various food groups including vegetables, fruits, whole grains, and proteins to give you practical insights for making healthier food choices.",
                            fontSize = 13.sp,
                            textAlign = TextAlign.Start
                        )
                    }
                }
            }

            // Floating chat bot button and dialog modal
            ChatBotFABWithModal(
                patientId = userId,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 30.dp, bottom = 120.dp)
            )
        }
    }
}
