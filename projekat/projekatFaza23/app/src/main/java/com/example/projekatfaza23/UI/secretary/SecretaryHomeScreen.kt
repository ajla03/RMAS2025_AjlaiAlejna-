package com.example.projekatfaza23.UI.secretary

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.projekatfaza23.UI.dean.extractInitials
import com.example.projekatfaza23.UI.dean.extractNameFromEmail
import com.example.projekatfaza23.UI.dean.getAvatarColor
import com.example.projekatfaza23.UI.dean.primaryColor
import com.example.projekatfaza23.UI.dean.timeStampToString
import com.example.projekatfaza23.UI.home.TopAppBarSection
import com.example.projekatfaza23.model.LeaveRequest
import com.example.projekatfaza23.model.RequestSatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SecretaryHomeScreen(
    viewModel: SecretaryViewModel,
    onLogoutClicked: () -> Unit,
    onNavigateToValidate: () -> Unit,
    onNavigateToHistory: () ->  Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val pendingRequests = uiState.displayedRequests

    SecretaryHomeScreenContent(
        isLoading = uiState.isLoading,
        pendingRequests = pendingRequests,
        onRequestClick = { request ->
            viewModel.selectRequest(request)
            onNavigateToValidate()
        },
        onTodayLeaveCount = uiState.onTodayLeaveCount,
        onNavigateToHistory = onNavigateToHistory
    )
}

@Composable
fun SecretaryHomeScreenContent(
    isLoading: Boolean,
    onTodayLeaveCount : Int,
    pendingRequests: List<LeaveRequest>,
    onRequestClick: (LeaveRequest) -> Unit,
    onNavigateToHistory: () -> Unit
) {
    Scaffold(
        containerColor = Color(0xFFF5F7FA),
        topBar = { TopAppBarSection() },
        bottomBar = {
            SecretaryBottomNavigationBar(
                currentRoute = "home",
                onNavigateToHome = {},
                onNavigateToHistory = onNavigateToHistory
            )
        }
    ) { padding ->

        if (isLoading) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = primaryColor)
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                SecretaryDashboardHeader(
                    pendingCount = pendingRequests.size,
                    onLeaveCount = onTodayLeaveCount
                )

                Text(
                    text = "Zahtjevi koji čekaju obradu",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                )

                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (pendingRequests.isEmpty()) {
                        item {
                            EmptyStateMessage(isHistory = false)
                        }
                    } else {
                        items(pendingRequests) { request ->
                            SecretaryRequestItem(
                                request = request,
                                onClick = { onRequestClick(request) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SecretaryDashboardHeader(pendingCount: Int, onLeaveCount: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(20.dp)
    ) {
        Text(
            text = "Dobrodošli, Sekretar",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF004D61)
        )

        Text(
            text = SimpleDateFormat("EEEE, d. MMMM", Locale("ba", "BA")).format(Date()),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            DashboardStatCard(
                modifier = Modifier.weight(1f),
                count = pendingCount.toString(),
                label = "Zahtjeva na čekanju",
                color = Color(0xFFE65100),
                bgColor = Color(0xFFFFF3E0),
                icon = Icons.Default.Notifications
            )

            DashboardStatCard(
                modifier = Modifier.weight(1f),
                count = onLeaveCount.toString(),
                label = "Danas na odmoru",
                color = Color(0xFF004D61),
                bgColor = Color(0xFFE0F7FA),
                icon = Icons.Default.DateRange
            )
        }
    }
}

@Composable
fun DashboardStatCard(
    modifier: Modifier = Modifier,
    count: String,
    label: String,
    color: Color,
    bgColor: Color,
    icon: ImageVector
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = count,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = color.copy(alpha = 0.8f),
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
fun SecretaryRequestItem(request: LeaveRequest, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(getAvatarColor(request.userEmail).copy(alpha = 0.1f))
            ) {
                Text(
                    text = extractInitials(request.userEmail),
                    color = getAvatarColor(request.userEmail),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = extractNameFromEmail(request.userEmail),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusBadge(request.status)
                    Spacer(modifier = Modifier.width(8.dp))

                    val datesSize = request.leave_dates?.size ?: 0
                    val firstDateRange = request.leave_dates?.firstOrNull()
                    val dateText = if (firstDateRange != null) {
                        val startStr = timeStampToString(firstDateRange.start!!)
                        val endStr = timeStampToString(firstDateRange.end!!)

                        var text = if (startStr == endStr) startStr else "$startStr - $endStr"

                        if (datesSize > 1) {
                            text += " (+${datesSize - 1})"
                        }
                        text
                    } else {
                        "Nema odabranih datuma"
                    }
                    Text(
                        text = dateText,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}


@Composable
fun StatusBadge(status: RequestSatus) {
    val (color, text) = when (status) {
        RequestSatus.Pending -> Color(0xFFF57C00) to "Na čekanju"
        RequestSatus.PendingDean -> Color(0xFF1976D2) to "Čeka Dekana"
        RequestSatus.Approved -> Color(0xFF2E7D32) to "Odobreno"
        RequestSatus.Denied -> Color(0xFFC62828) to "Odbijeno"
    }

    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(4.dp),
    ) {
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}


@Composable
fun EmptyStateMessage(isHistory: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isHistory) "Nema prethodnih zahtjeva." else "Sve čisto! Nema novih zahtjeva.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )
    }
}


@Preview(showBackground = true)
@Composable
fun SecretaryHomeScreenPreview() {
        val mockPending = listOf(
            LeaveRequest(userEmail = "ahodzic@fit.ba", status = RequestSatus.Pending),
            LeaveRequest(userEmail = "abulic@fit.ba", status = RequestSatus.Pending)
        )

        SecretaryHomeScreenContent(
            isLoading = false,
            pendingRequests = mockPending,
            onTodayLeaveCount = 3,
            onRequestClick = {},
            onNavigateToHistory = {}
        )
}