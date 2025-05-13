package com.fit2081.yeeun34752110

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fit2081.yeeun34752110.navigation.AppNavHost
import com.fit2081.yeeun34752110.navigation.BottomNavBar
import com.fit2081.yeeun34752110.ui.theme.NutriTrackTheme


class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.loadAndInsertFromCSV(this)

        setContent {
            NutriTrackTheme {
                val navController = rememberNavController()

                Scaffold(
                    bottomBar = {
                        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                        if (currentRoute in listOf("home", "insights", "settings", "nutricoach", "clinician login", "clinician")) {
                            BottomNavBar(navController)
                        }
                    }
                ) { innerPadding ->
                    AppNavHost(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun LandingPage(modifier: Modifier = Modifier, navController: NavController) {
    val context = LocalContext.current
    val screenHeightDp = LocalConfiguration.current.screenHeightDp
    val topSpacerHeight = (screenHeightDp * 0.08f).dp

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
                    text = "NutriTrack",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Image(
                    painter = painterResource(id = R.drawable.nutritrack_logo),
                    contentDescription = "NutriTrack Logo",
                    modifier = Modifier.size(140.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Info",
                            tint = Color(0xFF006400),
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "This app provides general health and nutrition information for educational purposes only. It is not intended as medical advice, diagnosis, or treatment. Always consult a qualified healthcare professional before making any changes to your diet, exercise, or health regimen. Use this app at your own risk.\n\nIf you’d like to see an Accredited Practicing Dietitian (APD), please visit the Monash Nutrition/Dietetics Clinic (discounted rates for students):",
                            fontSize = 13.sp,
                            fontStyle = FontStyle.Italic,
                            textAlign = TextAlign.Justify,
                            color = Color.DarkGray
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { openMonashClinic(context) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF203A84)),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Icon",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Visit Monash Nutrition Clinic",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { navController.navigate("login") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF006400),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 400.dp)
                        .height(48.dp),
                    shape = RectangleShape
                ) {
                    Text("Continue", fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Designed by, Yeeun (Evie) Lee (34752110)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}

// Opens Monash Nutrition Clinic URL in a browser
private fun openMonashClinic(context: Context) {
    val url = "https://www.monash.edu/medicine/scs/nutrition-clinic"
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}
