package com.fit2081.yeeun34752110.settings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.fit2081.yeeun34752110.AppViewModelFactory
import com.fit2081.yeeun34752110.databases.AuthManager
import com.fit2081.yeeun34752110.ui.theme.NutriTrackTheme

class SettingsActivity : ComponentActivity() {
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
fun SettingsPage(
    userId: Int,
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val context = LocalContext.current
    val viewModel: SettingsViewModel = viewModel(factory = AppViewModelFactory(context))

    val patient by viewModel.patient.collectAsState()

    LaunchedEffect(userId) {
        viewModel.loadPatientScoresById(userId)
    }

    val phoneNumber = patient?.patientPhoneNumber ?: "Loading..."
    val userName = patient?.patientName ?: "Loading..."


    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(vertical = 32.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .widthIn(max = 800.dp)
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = "Settings",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(16.dp))

                Text("ACCOUNT", fontWeight = FontWeight.SemiBold, color = Color.DarkGray)

                Spacer(Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.DarkGray)
                    Spacer(Modifier.width(8.dp))
                    Text(userName)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = Color.DarkGray)
                    Spacer(Modifier.width(8.dp))
                    Text(phoneNumber)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = Color.DarkGray)
                    Spacer(Modifier.width(8.dp))
                    Text(userId.toString())
                }

                Spacer(Modifier.height(16.dp))
                Divider(color = Color.LightGray)
                Spacer(Modifier.height(16.dp))

                Text("OTHER SETTINGS", fontWeight = FontWeight.SemiBold, color = Color.DarkGray)

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("clinician login") },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccountBox, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Clinician Login")
                    }
                    Icon(Icons.Default.ArrowForward, contentDescription = "Go")
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            AuthManager.logout(context)
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Logout")
                    }
                    Icon(Icons.Default.ArrowForward, contentDescription = "Go")
                }
            }
        }
    }
}