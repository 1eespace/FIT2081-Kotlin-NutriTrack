package com.fit2081.yeeun34752110.clinician

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.fit2081.yeeun34752110.AppViewModelFactory
import com.fit2081.yeeun34752110.genai.GenAiViewModel
import com.fit2081.yeeun34752110.genai.UiState
import kotlinx.coroutines.launch

@Composable
fun ClinicianPage(
    userId: Int,
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val context = LocalContext.current
    val viewModel: ClinicianViewModel = viewModel(factory = AppViewModelFactory(context))
    val genAiViewModel: GenAiViewModel = viewModel(factory = AppViewModelFactory(context))
    val screenHeightDp = LocalConfiguration.current.screenHeightDp
    val topSpacerHeight = (screenHeightDp * 0.08f).dp

    val uiState by genAiViewModel.uiState.collectAsState()
    val dataPatterns by viewModel.dataPatterns.collectAsState()

    var avgMaleScore by remember { mutableStateOf(0f) }
    var avgFemaleScore by remember { mutableStateOf(0f) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val (male, female) = viewModel.generateAvgScores()
        avgMaleScore = male
        avgFemaleScore = female
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 32.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .widthIn(max = 800.dp)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(topSpacerHeight))
                // Title
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Clinician Dashboard", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

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

                // Pattern Button
                Button(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.generateAiPatterns(genAiViewModel)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RectangleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF056207))
                ) {
                    Text("Find Data Pattern", color = Color.White)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Pattern Results
                when (uiState) {
                    is UiState.Loading -> {
                        Text("Analyzing data patterns...", fontStyle = FontStyle.Italic)
                    }

                    is UiState.Success -> {
                        val result = (uiState as UiState.Success).outputText
                        viewModel.handleAiResponse(result)

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            dataPatterns.forEachIndexed { index, pattern ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    elevation = CardDefaults.cardElevation(2.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(
                                            0xFFF7F7F7
                                        )
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

                    is UiState.Error -> {
                        val error = (uiState as UiState.Error).errorMessage
                        Text("Error: $error", color = Color.Red, fontSize = 14.sp)
                    }

                    else -> {}
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Done Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            navController.navigate("settings") {
                                popUpTo("settings") { inclusive = true }
                            }
                        },
                        modifier = Modifier
                            .height(40.dp)
                            .widthIn(min = 80.dp),
                        shape = RectangleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF056207))
                    ) {
                        Text("Done", color = Color.White)
                    }
                }
            }
        }
    }
}