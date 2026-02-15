package com.example.projekatfaza23.UI.secretary

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.projekatfaza23.UI.dean.extractNameFromEmail
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

    val filteredRequests = processedRequests.filter { request ->
        val matchesSearch = if (uiState.historySearchQuery.isBlank()) {
            true
        } else {
            val name = extractNameFromEmail(request.userEmail)
            name.contains(uiState.historySearchQuery, ignoreCase = true)
        }

        val matchesStatus = when (uiState.historyFilter) {
            HistoryFilter.ALL -> true
            HistoryFilter.APPROVED -> request.status == RequestSatus.Approved
            HistoryFilter.DENIED -> request.status == RequestSatus.Denied
        }

        matchesSearch && matchesStatus
    }

    BackHandler {
        viewModel.resetHistoryFilters()
        onNavigateToHome()
    }

    SecretaryHistoryScreenContent(
        isLoading = uiState.isLoading,
        processedRequests = filteredRequests,
        searchQuery = uiState.historySearchQuery,
        onSearchQueryChange = { newValue ->
            viewModel.updateHistorySearchQuery(newValue)
        },
        selectedFilter =uiState.historyFilter,
        onFilterSelected  =  { newFilter ->
            viewModel.updateHistoryFilter(newFilter)
        },
        onNavigateToHome = {
            viewModel.resetHistoryFilters()
            onNavigateToHome()},
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
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedFilter: HistoryFilter,
    onFilterSelected: (HistoryFilter) -> Unit,    onNavigateToHome: () -> Unit,
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

                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        placeholder = { Text("Pretraži ...", color = Color.Gray) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(50),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF5F7FA),
                            unfocusedContainerColor = Color(0xFFF5F7FA),
                            disabledContainerColor = Color(0xFFF5F7FA),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        HistoryFilterChip(
                            text = "Svi",
                            selected = selectedFilter == HistoryFilter.ALL,
                            onClick = { onFilterSelected(HistoryFilter.ALL) }
                        )
                        HistoryFilterChip(
                            text = "Odobreni",
                            selected = selectedFilter == HistoryFilter.APPROVED,
                            onClick = { onFilterSelected(HistoryFilter.APPROVED) }
                        )
                        HistoryFilterChip(
                            text = "Odbijeni",
                            selected = selectedFilter == HistoryFilter.DENIED,
                            onClick = { onFilterSelected(HistoryFilter.DENIED) }
                        )
                    }
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

@Composable
fun HistoryFilterChip(text: String, selected:  Boolean, onClick : ()-> Unit){
    Surface(
        onClick = onClick,
        color = if (selected) primaryColor else Color.White,
        shape = RoundedCornerShape(20.dp),
        border = if (selected) null else BorderStroke(1.dp, Color.LightGray),
        shadowElevation = if (selected) 2.dp else 0.dp
    ){
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = if (selected) Color.White else Color.Gray,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
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
        searchQuery = "",
        onSearchQueryChange = {},
        selectedFilter = HistoryFilter.ALL,
        onFilterSelected = {},
        onNavigateToHome = {},
        onRequestClick = {}
    )
}