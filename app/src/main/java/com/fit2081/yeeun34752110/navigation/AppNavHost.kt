package com.fit2081.yeeun34752110.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fit2081.yeeun34752110.LandingPage
import com.fit2081.yeeun34752110.auth.LoginPage
import com.fit2081.yeeun34752110.auth.RegisterPage
import com.fit2081.yeeun34752110.clinician.ClinicianLogin
import com.fit2081.yeeun34752110.clinician.ClinicianPage
import com.fit2081.yeeun34752110.databases.AuthManager
import com.fit2081.yeeun34752110.home.HomePage
import com.fit2081.yeeun34752110.insights.InsightsPage
import com.fit2081.yeeun34752110.nutricoach.NutriCoachPage
import com.fit2081.yeeun34752110.questionnaire.QuestionnairePage
import com.fit2081.yeeun34752110.settings.SettingsPage

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val userId = AuthManager.getPatientId()?.toIntOrNull() ?: -1

    val startDestination = if (userId != -1) {
        "home"
    } else {
        "landing"
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("landing") {
            LandingPage(modifier, navController)
        }

        composable("login") {
            LoginPage(modifier, navController)
        }

        composable("register") {
            RegisterPage(modifier, navController)
        }

        composable("home") {
            HomePage(userId, navController, modifier)
        }

        composable("questionnaire") {
            QuestionnairePage(userId, navController)
        }

        composable("settings") {
            SettingsPage(userId, modifier, navController)
        }

        composable("insights") {
            InsightsPage(userId, modifier, navController)
        }

        composable("nutricoach") {
            NutriCoachPage(userId, modifier)
        }

        composable("clinician login") { ClinicianLogin(navController) }

        composable("clinician") {
            ClinicianPage(modifier, navController)
        }

    }
}
