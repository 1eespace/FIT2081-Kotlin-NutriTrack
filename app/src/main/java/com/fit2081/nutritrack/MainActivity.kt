package com.fit2081.nutritrack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import com.fit2081.nutritrack.ui.theme.NutriTrackTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NutriTrackTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LandingPage (
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LandingPage(modifier: Modifier = Modifier) {

    // Get the Android context to use for actions
    // for showing Toast messages or launching activities
    val context = LocalContext.current

    // Surface: Main background container
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF5F5DC)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Title
            Text(
                text = "NutriTrack",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            // Add spacing below the title
            Spacer(modifier = Modifier.height(30.dp))

            // NutriTrack Logo
            androidx.compose.foundation.Image(
                painter = painterResource(id = R.drawable.nutritrack_logo),
                contentDescription = "NutriTrack Logo",
                modifier = Modifier.size(160.dp)
            )
            // Add spacing below the logo
            Spacer(modifier = Modifier.height(30.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()) // Enables scrolling if the text is long
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Disclaimer Text
                Text(
                    text = "This app provides general health and nutrition information for educational purposes only. " +
                            "It is not intended as medical advice, diagnosis, or treatment. " +
                            "Always consult a qualified healthcare professional before making any changes to your diet, exercise, or health regimen. " +
                            "Use this app at your own risk. " + "\n" +
                            "If you’d like to see an Accredited Practicing Dietitian (APD), please visit the Monash Nutrition/Dietetics Clinic (discounted rates for students):",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Monash Nutrition Clinic Link (Clickable)
                Text(
                    text = "Monash Nutrition Clinic",
                    fontSize = 14.sp,
                    color = Color.Blue,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { MonashClinicUrl(context) }
                        .padding(8.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }

            // Add space between the disclaimer and login button
            Spacer(modifier = Modifier.height(30.dp))

            // Login Button
            Button(
                onClick = {
                    // Navigate to the Login page
                    context.startActivity(Intent(context, LoginActivity::class.java))
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF006400),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Login", fontSize = 15.sp)
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Author: Student Name and ID
            Text(
                text = "Designed by, Yeeun (Evie) Lee (34752110)",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
        }
    }
}

// URL: opens the Monash Nutrition Clinic website
private fun MonashClinicUrl(context: Context) {
    val url = "https://www.monash.edu/medicine/scs/nutrition-clinic"
    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
    context.startActivity(intent)
}