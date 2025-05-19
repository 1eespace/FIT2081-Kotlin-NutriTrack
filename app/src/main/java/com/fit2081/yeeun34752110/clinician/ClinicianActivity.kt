package com.fit2081.yeeun34752110.clinician

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.fit2081.yeeun34752110.AppViewModelFactory
import com.fit2081.yeeun34752110.genai.GenAiViewModel
import com.fit2081.yeeun34752110.genai.UiState
import com.fit2081.yeeun34752110.ui.theme.NutriTrackTheme
import kotlinx.coroutines.launch

class ClinicianActivity : ComponentActivity() {
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
fun ClinicianPage(
    userId: Int,
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val context = LocalContext.current
    val viewModel: ClinicianViewModel = viewModel(factory = AppViewModelFactory(context))
    val genAiViewModel: GenAiViewModel = viewModel(factory = AppViewModelFactory(context))
    val coroutineScope = rememberCoroutineScope()

    val uiState by genAiViewModel.uiState.collectAsState()
    // GenAi dataPatterns
    val dataPatterns by viewModel.dataPatterns.collectAsState()

    // Male/Female Avg HEIFA Score
    val avgMaleScore by viewModel.avgMaleScore.collectAsState()
    val avgFemaleScore by viewModel.avgFemaleScore.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.generateAvgScores()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 32.dp),
        contentPadding = PaddingValues(vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Column(
                modifier = Modifier
                    .widthIn(max = 800.dp)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Clinician Dashboard", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "This dashboard shows average HEIFA scores by gender and allows AI-powered data analysis.",
                    fontSize = 14.sp,
                    color = Color(0xFF056207),
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    textAlign = TextAlign.Start
                )

                // HEIFA Score Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Average HEIFA (Male):", fontWeight = FontWeight.Medium)
                            Text("${"%.2f".format(avgMaleScore)}")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Average HEIFA (Female):", fontWeight = FontWeight.Medium)
                            Text("${"%.2f".format(avgFemaleScore)}")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Tap 'Find Data Pattern' to uncover key dietary insights",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    textAlign = TextAlign.Start
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                viewModel.generateAiPatterns(genAiViewModel)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RectangleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF056207))
                    ) {
                        Text("Find Data Pattern", color = Color.White)
                    }

                    IconButton(
                        onClick = {
                            viewModel.clearDataPatterns()
                            // Refresh the dataPattern
                            genAiViewModel.clearUiState()
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Clear",
                            tint = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Pattern Results
                when (uiState) {
                    is UiState.Loading -> {
                        Text("Analysing data patterns...", fontStyle = FontStyle.Italic)
                    }

                    is UiState.Success -> {
                        val result = (uiState as UiState.Success).outputText
                        viewModel.handleAiResponse(result)

                        if (dataPatterns.isEmpty()) {
                            Text(
                                text = "No data patterns yet.",
                                fontStyle = FontStyle.Italic,
                                color = Color.Gray
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                dataPatterns.forEachIndexed { index, pattern ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp),
                                        elevation = CardDefaults.cardElevation(2.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color(0xFFF7F7F7)
                                        )
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(
                                                text = "Insight ${index + 1}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = Color(0xFF333333)
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = pattern.trim(),
                                                fontSize = 14.sp,
                                                color = Color.DarkGray
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    is UiState.Error -> {
                        val error = (uiState as UiState.Error).errorMessage
                        Text("Error: $error", color = Color.Red, fontSize = 14.sp)
                    }

                    is UiState.Initial -> {
                        Text(
                            text = "No data patterns yet.",
                            fontStyle = FontStyle.Italic,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Done Button
                Row(
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            navController.navigate("settings") {
                                popUpTo("settings") { inclusive = true }
                            }
                        },
                        modifier = Modifier
                            .height(48.dp)
                            .widthIn(min = 80.dp),
                        shape = RectangleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF056207))
                    ) {
                        // Logout from Clinician Dashboard
                        Text("End Session", color = Color.White)
                    }
                }
            }
        }
    }
}