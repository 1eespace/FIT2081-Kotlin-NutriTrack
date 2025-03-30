package com.fit2081.nutritrack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.core.content.edit
import com.fit2081.nutritrack.ui.theme.NutriTrackTheme
import java.io.BufferedReader
import java.io.InputStreamReader

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enables edge-to-edge layout
        enableEdgeToEdge()
        setContent {
            NutriTrackTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginPage(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun LoginPage(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    // Stores the selected user ID
    var selectedUserId by remember { mutableStateOf("") }
    // Controls dropdown menu expansion
    var expanded by remember { mutableStateOf(false) }
    // User ID options
    var userIdOptions by remember { mutableStateOf(listOf<String>()) }

    // Stores the entered phone number
    var phoneNumber by remember { mutableStateOf("") }
    // Tracks phone number validation error
    var phoneNumberError by remember { mutableStateOf(false) }

    // Load user IDs from CSV when the page is launched
    LaunchedEffect(Unit) {
        userIdOptions = loadUserIdsFromCSV(context)
    }
    // Surface: Main background container
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF5F5DC)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Login page title
            Text(
                text = "User Login",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Dropdown menu for selecting user ID
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    label = { Text("My ID") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Person Icon") },
                    value = selectedUserId,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Select User ID") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    userIdOptions.forEach { user ->
                        DropdownMenuItem(
                            text = { Text(user) },
                            onClick = {
                                selectedUserId = user
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Phone Number Input Field
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = {
                    phoneNumber = it
                    // Validate phone number
                    phoneNumberError = !isValidNumber(it)
                },
                label = { Text("Phone Number") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone Icon") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = phoneNumberError,
                singleLine = true
            )

            // Error message for invalid phone number
            if (phoneNumberError) {
                Text(
                    text = "Invalid Phone Number",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Info message about the login page
            Text(
                text = "This app is only for pre-registered users. Please have your ID and phone number handy before continuing",
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Login Button
            Button(
                onClick = {
                    isLoginValid(context, selectedUserId, phoneNumber)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF006400),
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RectangleShape
            ) {
                Text("Continue")
            }
        }
    }
}

// Loads user IDs
fun loadUserIdsFromCSV(context: Context): List<String> {
    return try {
        val assetManager = context.assets
        val inputStream = assetManager.open("user_data.csv")
        val reader = BufferedReader(InputStreamReader(inputStream))

        reader.useLines { lines ->
            lines.drop(1) // Skip the header row
                // Extract the second column: User_ID
                .map { it.split(",")[1].trim() }
                .toList()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}

// Regex pattern: validates the phone number format
fun isValidNumber(number: String): Boolean {
    val pattern = Regex("^61(4365|4333)[0-9]{5}$")
    return pattern.matches(number)
}

// Checks if the provided user ID and phone number match
fun isLoginValid(context: Context, inputUserId: String, inputPhoneNumber: String) {
    try {
        val assetManager = context.assets
        val fileExists = assetManager.list("")?.contains("user_data.csv") ?: false

        if (!fileExists) {
            Toast.makeText(context, "User data file not found.", Toast.LENGTH_SHORT).show()
            return
        }

        val inputStream = assetManager.open("user_data.csv")
        val reader = BufferedReader(InputStreamReader(inputStream))

        val loginSuccess = reader.useLines { lines ->
            lines.drop(1).any { line ->
                val values = line.split(",").map { it.trim() }
                values.size >= 2 && values[0] == inputPhoneNumber && values[1] == inputUserId
            }
        }

        if (loginSuccess) {
            // Store the user ID and phone number in SharedPreferences
            context.getSharedPreferences("a1_nutriTrack", Context.MODE_PRIVATE).edit {
                putString("user_id", inputUserId)
                putString("phone_number", inputPhoneNumber)
            }

            // Success message and navigate to Questionnaire
            Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
            context.startActivity(Intent(context, FoodIntakeQuestionaireActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            })
        } else { // Error message
            Toast.makeText(context, "Invalid credentials.", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Login error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
    }
}
