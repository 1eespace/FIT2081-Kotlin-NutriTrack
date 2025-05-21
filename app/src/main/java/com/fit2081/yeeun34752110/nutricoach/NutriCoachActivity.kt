package com.fit2081.yeeun34752110.nutricoach

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.fit2081.yeeun34752110.AppViewModelFactory
import com.fit2081.yeeun34752110.databases.patientdb.Patient
import com.fit2081.yeeun34752110.nutricoach.fruitapi.FruitsRepository
import com.fit2081.yeeun34752110.genai.GenAiViewModel
import com.fit2081.yeeun34752110.genai.UiState
import com.fit2081.yeeun34752110.ui.theme.NutriTrackTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class NutriCoachActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val userId = intent.getIntExtra("userId", -1)

            setContent {
                NutriTrackTheme {
                    NutriCoachPage(userId = userId)
                }
            }
        }
    }
}

@Composable
fun NutriCoachPage(userId: Int, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val viewModel: NutriCoachViewModel = viewModel(factory = AppViewModelFactory(context))
    val genAiViewModel: GenAiViewModel = viewModel(factory = AppViewModelFactory(context))
    val coroutineScope = rememberCoroutineScope()
    val repository = remember { FruitsRepository() }

    val patient by viewModel.patient.collectAsState()
    val fruitName = viewModel.fruitName
    val fruitDetails = viewModel.fruitDetailsMap

    LaunchedEffect(userId) {
        viewModel.loadPatientScoresById(userId)
    }

    val aiUiState by genAiViewModel.uiState.collectAsState()
    val showDialog = viewModel.showDialog
    val tipList = viewModel.tipList

    LaunchedEffect(showDialog) {
        val currentPatient = patient
        if (showDialog && currentPatient != null) {
            viewModel.loadTipsIfNeeded(currentPatient.patientId)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .widthIn(max = 800.dp)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "NutriCoach",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp),
                    textAlign = TextAlign.Center
                )

                // For optimalFruit score condition logic
                val fruitScore = (patient?.fruits ?: 0f).toDouble()
                if (fruitScore < 10.0) {
                    FruitInputSection(viewModel, coroutineScope, repository, fruitName, fruitDetails)
                } else {
                    OptimalScoreSection()
                }

                Spacer(modifier = Modifier.height(24.dp))

                MotivationalAiSection(patient, genAiViewModel, aiUiState)

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.toggleDialog(true) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF056207))
                ) {
                    Text("Show All Tips")
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.toggleDialog(false) },
            confirmButton = {
                Button(
                    onClick = { viewModel.toggleDialog(false) },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF056207))
                ) {
                    Text("Done", color = Color.White)
                }
            },
            title = { Text("AI Tips", fontWeight = FontWeight.Bold) },
            text = {
                val allTips = tipList.sortedByDescending { it.tipsId }
                LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                    allTips.forEach { tip ->
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp, horizontal = 4.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F1F1)),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Text(
                                    text = "\u2022 ${tip.message}",
                                    modifier = Modifier.padding(12.dp),
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        )
    }
}

// Fruit name input
@Composable
fun FruitInputSection(
    viewModel: NutriCoachViewModel,
    coroutineScope: CoroutineScope,
    repository: FruitsRepository,
    fruitName: String,
    fruitDetails: Map<String, String>
) {
    var showError by remember { mutableStateOf(false) }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = fruitName,
                onValueChange = {
                    viewModel.updateFruitName(it)
                    showError = false
                },
                label = { Text("Fruit Name") },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )
            Button(
                onClick = {
                    coroutineScope.launch {
                        val trimmed = fruitName.trim().lowercase()
                        if (trimmed.isNotBlank()) {
                            viewModel.fetchFruitDetails(trimmed, DecimalFormat("0.#"), repository)
                            showError = viewModel.fruitDetailsMap.isEmpty()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF056207)),
                modifier = Modifier.height(56.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Details")
            }
            IconButton(
                onClick = {
                    viewModel.clearFruitDetails()
                    showError = false
                },
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Clear", tint = Color.Gray)
            }
        }

        if (showError && fruitName.isNotBlank()) {
            Text(
                text = "No data found for \"$fruitName\"",
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Fruit Nutrition Details", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                if (fruitDetails.isEmpty()) {
                    Text("No nutrition data yet: Enter the fruit name", fontStyle = FontStyle.Italic)
                } else {
                    fruitDetails.forEach { (label, value) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(label, fontWeight = FontWeight.Medium)
                            Text(value)
                        }
                    }
                }
            }
        }
    }
}

// Showing Random Image to optimalFruit score patient/user
@Composable
fun OptimalScoreSection() {
    var imageLoadFailed by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "\u2705 You have an optimal fruit score!",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        val randomImageUrl = remember { "https://picsum.photos/300/200" }

        AsyncImage(
            model = randomImageUrl,
            contentDescription = "Random Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(top = 8.dp),
            onError = {
                imageLoadFailed = true
            }
        )

        if (imageLoadFailed) {
            Text(
                text = "Failed to load image. Please check your internet connection.",
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

// Motivational Messages
@Composable
fun MotivationalAiSection(
    patient: Patient?,
    genAiViewModel: GenAiViewModel,
    aiUiState: UiState
) {
    Button(
        onClick = {
            patient?.let { data ->
                val prompt = NutriCoachPromptTemplates.getRandomPrompt(data)
                genAiViewModel.sendPrompt(prompt, data.patientId)
            }
        },
        modifier = Modifier.fillMaxWidth().height(48.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF056207))
    ) {
        Icon(Icons.Default.Star, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text("Motivational Message (AI)")
    }

    Spacer(modifier = Modifier.height(8.dp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Text(
            text = when (aiUiState) {
                is UiState.Success -> aiUiState.outputText
                is UiState.Error -> "Error: ${aiUiState.errorMessage}"
                is UiState.Loading -> "Generating message..."
                else -> "No motivational message yet"
            },
            modifier = Modifier.padding(12.dp),
            fontStyle = FontStyle.Italic
        )
    }
}