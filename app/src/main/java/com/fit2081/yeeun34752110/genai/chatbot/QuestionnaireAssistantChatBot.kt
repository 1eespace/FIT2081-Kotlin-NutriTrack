package com.fit2081.yeeun34752110.genai.chatbot

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fit2081.yeeun34752110.AppViewModelFactory
import com.fit2081.yeeun34752110.genai.GenAiViewModel
import com.fit2081.yeeun34752110.genai.UiState
import com.fit2081.yeeun34752110.questionnaire.FoodIntakeQuestionnaireViewModel

/**
 * Data class to represent a chat message
 */
data class ChatMessage(val isUser: Boolean, val text: String)

/**
 * Displays the chat interface with NutriBot assistant
 */
@Composable
fun ChatBot(
    patientId: Int,
    questionnaireViewModel: FoodIntakeQuestionnaireViewModel
) {
    val context = LocalContext.current
    val viewModel: GenAiViewModel = viewModel(factory = AppViewModelFactory(context))

    var userMessage by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    val messages = remember { mutableStateListOf<ChatMessage>() }

    // Append bot response
    LaunchedEffect(uiState) {
        if (uiState is UiState.Success) {
            messages.add(ChatMessage(false, (uiState as UiState.Success).outputText))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("🤖 Need help with the questionnaire?", fontWeight = FontWeight.Bold, fontSize = 20.sp)

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(messages) { msg ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (msg.isUser) Color(0xFF203A84) else Color(0xFFE0E0E0),
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(
                            text = msg.text,
                            color = if (msg.isUser) Color.White else Color.Black,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }

            if (uiState is UiState.Loading) {
                item {
                    CircularProgressIndicator(modifier = Modifier.padding(8.dp))
                }
            } else if (uiState is UiState.Error) {
                item {
                    Text(
                        text = "Oops: ${(uiState as UiState.Error).errorMessage}",
                        color = Color.Red,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }

        OutlinedTextField(
            value = userMessage,
            onValueChange = { userMessage = it },
            label = { Text("Still curious? Ask me anything.") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (userMessage.isNotBlank()) {
                    messages.add(ChatMessage(true, userMessage))
                    val prompt = generateChatPrompt(questionnaireViewModel, userMessage)
                    viewModel.sendPrompt(prompt, patientId, saveToDb = false)
                    userMessage = ""
                }
            },
            modifier = Modifier.align(Alignment.End),
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF203A84))
        ) {
            Text("Send", color = Color.White)
        }
    }
}

/**
 * Floating Action Button for opening ChatBot
 */
@Composable
fun ChatBotFAB(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp
) {
    Surface(
        shape = CircleShape,
        color = Color(0xFF203A84),
        shadowElevation = 12.dp,
        modifier = modifier
            .size(size)
            .clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.Chat,
                contentDescription = "Open NutriBot",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

/**
 * Combines FAB and ModalBottomSheet to show ChatBot
 */
@Composable
fun ChatBotFABWithModal(
    patientId: Int,
    questionnaireViewModel: FoodIntakeQuestionnaireViewModel,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp
) {
    var showChatBot by rememberSaveable { mutableStateOf(false) }
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    val dialogWidth = when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> screenWidth * 0.95f
        else -> screenWidth * 0.9f
    }

    val dialogHeight = when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> screenHeight * 0.9f
        else -> screenHeight * 0.7f
    }

    ChatBotFAB(
        onClick = { showChatBot = true },
        modifier = modifier,
        size = size
    )

    if (showChatBot) {
        Dialog(onDismissRequest = { showChatBot = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 10.dp,
                color = Color.White,
                modifier = Modifier
                    .width(dialogWidth)
                    .height(dialogHeight)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Close button
                    IconButton(
                        onClick = { showChatBot = false },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Gray
                        )
                    }

                    // ChatBot content
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
                    ) {
                        ChatBot(patientId = patientId, questionnaireViewModel = questionnaireViewModel)
                    }
                }
            }
        }
    }
}

fun isDietRelatedQuestion(message: String): Boolean {
    val keywords = listOf("meal", "diet", "food", "sleep", "wake", "health", "nutrition", "intake", "persona")
    return keywords.any { message.lowercase().contains(it) }
}
// User can ask about their questionnaire question or general diet question like BMI
fun generateChatPrompt(
    viewModel: FoodIntakeQuestionnaireViewModel,
    userMessage: String
): String {
    return if (isDietRelatedQuestion(userMessage)) {
        val persona = viewModel.selectedPersona.value
        val sleep = viewModel.sleepTime.value
        val wake = viewModel.wakeTime.value
        val meal = viewModel.biggestMealTime.value

        val selectedIntakes = viewModel.selectedCategories.value
            .filterValues { it }
            .keys.joinToString(", ")
            .ifEmpty { "None" }

        """
        The user has provided the following food intake information:

        - Persona: $persona
        - Wake-up time: $wake
        - Sleep time: $sleep
        - Biggest meal time: $meal
        - Food categories consumed: $selectedIntakes

        The user is now asking:
        "$userMessage"

        Please provide a relevant and helpful response based on the user's dietary profile.
        """.trimIndent()
    } else {
        """
        The user is asking:
        "$userMessage"

        Please provide a clear and helpful answer.
        """.trimIndent()
    }
}
