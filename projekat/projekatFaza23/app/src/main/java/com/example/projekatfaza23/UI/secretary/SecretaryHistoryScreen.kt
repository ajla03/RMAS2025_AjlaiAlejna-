package com.example.projekatfaza23.UI.secretary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.projekatfaza23.UI.dean.primaryColor
import com.example.projekatfaza23.UI.home.TopAppBarSection
import com.example.projekatfaza23.model.LeaveRequest
import com.example.projekatfaza23.model.RequestSatus


@Composable
fun SecretaryHistoryScreen(
    viewModel: SecretaryViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToRequest: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val processedRequests = uiState.allRequests.filter{ it.status!= RequestSatus.Pending }

    SecretaryHistoryScreenContent(
        isLoading = uiState.isLoading,
        processedRequests = processedRequests,
        onNavigateToHome = onNavigateToHome,
        onRequestClick = { request ->
            viewModel.selectRequest(request)
            onNavigateToRequest()
        }
    )

}

@Composable
fun SecretaryHistoryScreenContent(
    isLoading: Boolean,
    processedRequests: List<LeaveRequest>,
    onNavigateToHome: () -> Unit,
    onRequestClick: (LeaveRequest) -> Unit
) {
    Scaffold(
        containerColor = Color(0xFFF5F7FA),
        topBar = { TopAppBarSection() },
        bottomBar = {
            SecretaryBottomNavigationBar(
                currentRoute = "history",
                onNavigateToHome = onNavigateToHome,
                onNavigateToHistory = {  }
            )
        }
    ){  paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.padding(paddingValues).fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = primaryColor)
            }
        } else {
            Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
                Column(
                    modifier = Modifier.fillMaxWidth().background(Color.White).padding(20.dp)
                ) {

                    Text(
                        text = "Historija zahtjeva",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF004D61)
                    )
                    Text(
                        text = "Pregled svih obrađenih zahtjeva",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )

                }

                LazyColumn(
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (processedRequests.isEmpty()) {
                        item { EmptyStateMessage(isHistory = true) }
                    } else {
                        items(processedRequests) { request ->
                            SecretaryRequestItem(
                                request = request,
                                onClick = { onRequestClick(request) })
                        }
                    }
                }

            }

        }

    }

}




@Preview(showBackground = true)
@Composable
fun SecretaryHistoryScreenPreview() {
        SecretaryHistoryScreenContent(
            isLoading = false,
            processedRequests = listOf(
                LeaveRequest(userEmail = "odobreno@fit.ba", status = RequestSatus.Approved),
                LeaveRequest(userEmail = "odbijeno@fit.ba", status = RequestSatus.Denied)
            ),
            onNavigateToHome = {},
            onRequestClick = {}
        )
}