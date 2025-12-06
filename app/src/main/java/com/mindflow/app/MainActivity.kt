package com.mindflow.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mindflow.app.ui.screens.*
import com.mindflow.app.ui.theme.*
import com.mindflow.app.ui.viewmodel.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MindFlowTheme {
                MindFlowApp()
            }
        }
    }
}

@Composable
fun MindFlowApp() {
    val context = LocalContext.current
    val application = context.applicationContext as MindFlowApplication
    val viewModelFactory = remember { ViewModelFactory(application.moodRepository) }
    
    val navController = rememberNavController()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        GradientTop,
                        GradientMiddle,
                        GradientBottom
                    )
                )
            ),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 428.dp)
                .fillMaxSize()
        ) {
            NavHost(
                navController = navController,
                startDestination = "home"
            ) {
                composable("home") {
                    val homeViewModel: HomeViewModel = viewModel(factory = viewModelFactory)
                    
                    // Refresh data when returning to home
                    LaunchedEffect(Unit) {
                        homeViewModel.refresh()
                    }
                    
                    HomeScreen(
                        viewModel = homeViewModel,
                        onNavigateToEntry = { navController.navigate("entry") },
                        onNavigateToCalendar = { navController.navigate("calendar") },
                        onNavigateToInsights = { navController.navigate("insights") }
                    )
                }
                
                composable("entry") {
                    val entryViewModel: DailyEntryViewModel = viewModel(factory = viewModelFactory)
                    
                    DailyEntryScreen(
                        viewModel = entryViewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                
                composable("calendar") {
                    val calendarViewModel: CalendarViewModel = viewModel(factory = viewModelFactory)
                    
                    MoodCalendarScreen(
                        viewModel = calendarViewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToInsights = { navController.navigate("insights") }
                    )
                }
                
                composable("insights") {
                    val insightsViewModel: InsightsViewModel = viewModel(factory = viewModelFactory)
                    
                    InsightsScreen(
                        viewModel = insightsViewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
