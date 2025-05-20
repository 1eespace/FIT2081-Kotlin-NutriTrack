package com.fit2081.yeeun34752110.auth

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.fit2081.yeeun34752110.AppViewModelFactory
import com.fit2081.yeeun34752110.ui.theme.NutriTrackTheme


class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NutriTrackTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RegisterPage(
                        modifier = Modifier.padding(innerPadding),
                        navController = rememberNavController()
                    )
                }
            }
        }
    }
}

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterPage(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val context = LocalContext.current
    val viewModel: AuthViewModel = viewModel(factory = AppViewModelFactory(context))

    val selectedUserId = viewModel.selectedUserId
    val name = viewModel.name
    val phone = viewModel.phone
    val password = viewModel.password
    val confirmPassword = viewModel.confirmPassword
    val expanded = viewModel.registerDropdownExpanded
    // Just showing unregistered patient ids
    val userIds by viewModel.unregisteredIds.collectAsState(initial = emptyList())
    val message = viewModel.registrationMessage.value

    // Dynamic top padding based on screen height
    val screenHeightDp = LocalConfiguration.current.screenHeightDp
    val topSpacerHeight = (screenHeightDp * 0.1f).dp

    // Confirm Password (with visibility toggle)
    var showConfirmPassword by remember { mutableStateOf(false) }

    // Toast on registration result
    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.registrationMessage.value = null
        }
    }

    if (viewModel.registrationSuccessful.value) {
        LaunchedEffect(Unit) {
            navController.navigate("login") {
                popUpTo("register") { inclusive = true }
            }
        }
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

                Text(
                    text = "Register",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(24.dp))

                // User ID dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { viewModel.toggleRegisterDropdown() }
                ) {
                    OutlinedTextField(
                        value = selectedUserId,
                        onValueChange = {},
                        label = { Text("My ID") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { viewModel.dismissRegisterDropdown() }
                    ) {
                        userIds.forEach { userId ->
                            DropdownMenuItem(
                                text = { Text(userId.toString()) },
                                onClick = {
                                    viewModel.typeUserId(userId.toString())
                                    viewModel.dismissRegisterDropdown()
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Name
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        if (it.all { c -> c.isLetter() }) {
                            viewModel.typeName(it)
                        }
                    },
                    label = { Text("Name") },
                    placeholder = { Text("Maximum 8 characters (A-Z, a-z)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Phone: Just DIGITS
                OutlinedTextField(
                    value = phone,
                    onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() }) {
                            viewModel.typePhone(newValue)
                        }
                    },
                    label = { Text("Phone Number") },
                    placeholder = { Text("Enter your phone number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        if (it.length <= 8 && it.matches(Regex("^[A-Za-z0-9]*$"))) {
                            viewModel.updatePassword(it)
                        }
                    },
                    label = { Text("Password") },
                    placeholder = { Text("4 to 8 only letters and digits") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Confirm Password
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        if (it.length <= 8 && it.matches(Regex("^[A-Za-z0-9]*$"))) {
                            viewModel.typeConfirmPassword(it)
                        }
                    },
                    label = { Text("Confirm Password") },
                    placeholder = { Text("Re-enter your password") },
                    visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val visibilityIcon = if (showConfirmPassword) {
                            Icons.Filled.Visibility
                        } else {
                            Icons.Filled.VisibilityOff
                        }
                        IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                            Icon(
                                imageVector = visibilityIcon,
                                contentDescription = if (showConfirmPassword) "Hide password" else "Show password"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "This app is only for pre-registered users. Please enter your ID, Phone number, Name and Password to claim your account.",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        viewModel.registerFun(selectedUserId, name, phone, password, confirmPassword)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF006400),
                        contentColor = Color.White
                    ),
                    shape = RectangleShape
                ) {
                    Text("Register")
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        navController.navigate("login") {
                            popUpTo("register") { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF006400),
                        contentColor = Color.White
                    ),
                    shape = RectangleShape
                ) {
                    Text("Login")
                }
            }
        }
    }
}
