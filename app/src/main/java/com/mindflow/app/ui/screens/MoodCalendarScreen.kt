package com.mindflow.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindflow.app.ui.components.GradientButton
import com.mindflow.app.ui.theme.*
import com.mindflow.app.ui.viewmodel.CalendarViewModel
import java.time.format.DateTimeFormatter

@Composable
fun MoodCalendarScreen(
    viewModel: CalendarViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToInsights: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    val dayHeaders = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        // Header
        Spacer(modifier = Modifier.height(16.dp))
        
        // Back Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = TextSecondary
                )
            }
            Text(
                text = "Back",
                fontSize = 16.sp,
                color = TextSecondary
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = null,
                tint = Purple500,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Mood Calendar",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Legend Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LegendItem(color = MoodExcellent, label = "Great")
                LegendItem(color = MoodNeutral, label = "Okay")
                LegendItem(color = MoodStressed, label = "Stressed")
                LegendItem(color = MoodSad, label = "Sad")
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Calendar Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                // Month Navigation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.goToPreviousMonth() }) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Previous month",
                            tint = Purple500
                        )
                    }
                    
                    Text(
                        text = uiState.formattedMonth,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    
                    IconButton(
                        onClick = { viewModel.goToNextMonth() },
                        enabled = viewModel.canGoToNextMonth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Next month",
                            tint = if (viewModel.canGoToNextMonth()) Purple500 else TextTertiary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Day headers
                Row(modifier = Modifier.fillMaxWidth()) {
                    dayHeaders.forEach { day ->
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day,
                                fontSize = 12.sp,
                                color = TextTertiary
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Calendar grid
                val daysInMonth = viewModel.getDaysInMonth()
                val firstDayOfWeek = viewModel.getFirstDayOfWeek()
                var currentDayIndex = 0
                val totalCells = firstDayOfWeek + daysInMonth
                val rows = (totalCells + 6) / 7
                
                for (row in 0 until rows) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        for (col in 0..6) {
                            val cellIndex = row * 7 + col
                            if (cellIndex < firstDayOfWeek || currentDayIndex >= daysInMonth) {
                                // Empty cell
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                )
                            } else {
                                val day = currentDayIndex + 1
                                val entry = uiState.entriesMap[day]
                                val isSelected = uiState.selectedDay == day
                                
                                val scale by animateFloatAsState(
                                    targetValue = if (isSelected) 1.1f else 1f,
                                    label = "scale"
                                )
                                
                                val bgColor = entry?.let { 
                                    Color(it.moodCategory.colorHex) 
                                } ?: Color(0xFFF5F5F5)
                                
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .padding(2.dp)
                                        .scale(scale)
                                        .shadow(
                                            elevation = if (isSelected) 8.dp else 0.dp,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .background(
                                            color = bgColor,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable { viewModel.selectDay(day) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = day.toString(),
                                        fontSize = 14.sp,
                                        color = if (entry != null) TextPrimary else TextTertiary
                                    )
                                }
                                currentDayIndex++
                            }
                        }
                    }
                }
                
                // Selected day details
                uiState.selectedEntry?.let { entry ->
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Purple50,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = entry.emoji, fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = entry.date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "${entry.sentiment} • Score: ${entry.score}/100",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                            if (entry.insight.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = entry.insight,
                                    fontSize = 12.sp,
                                    color = TextTertiary,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
                
                // Empty state message
                if (uiState.entriesMap.isEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No entries this month. Start journaling to see your mood patterns!",
                        fontSize = 12.sp,
                        color = TextTertiary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // View Insights Button
        GradientButton(
            text = "View Insights",
            onClick = onNavigateToInsights
        )
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(color = color, shape = RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextSecondary
        )
    }
}