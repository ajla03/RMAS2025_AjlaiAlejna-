package com.example.projekatfaza23.UI.dean

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.projekatfaza23.UI.home.TopAppBarSection
import com.example.projekatfaza23.UI.request.RequestType
import com.example.projekatfaza23.UI.request.RequestTypeSelector
import com.example.projekatfaza23.model.LeaveRequest
import com.example.projekatfaza23.model.RequestSatus


@Composable
fun DeanHistoryScreen(
    viewModel: DeanViewModel,
    navigateRequest: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToDirectory: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BackHandler {
        viewModel.resetFilters()
        onNavigateToHome()
    }

    val historyRequests = uiState.displayRequests.filter { it.status != RequestSatus.PendingDean }.sortedByDescending { it.createdAt }

    DeanHistoryScreenContent(
        isLoading = uiState.isLoading,
        historyRequests = historyRequests,
        isActiveFilter = uiState.isActiveFilter,
        filterStatus = uiState.filterStatus,
        searchQuery = uiState.searchQuery,
        dateRangeStart = uiState.dateRange.first,
        dateRangeEnd = uiState.dateRange.second,
        navigateRequest = navigateRequest,
        onResetFilters = { viewModel.resetFilters() },
        onSetStatusFilter = { viewModel.setStatusFilter(it) },
        onSetSelectedRequest = { viewModel.setSelectedRequest(it) },
        onUpdateNameFilter = { viewModel.updateNameFilter(it) },
        onUpdateDateRangeFilter = { start, end -> viewModel.updateDateRangeFilter(start, end) },
        onNavigateToHome = {
            viewModel.resetFilters()
            onNavigateToHome()
        },
        onNavigateToDirectory = onNavigateToDirectory,
        currentRequestType = uiState.currentRequestType,
        onTypeChange = {viewModel.onTypeChange(it)}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeanHistoryScreenContent(
    isLoading: Boolean,
    historyRequests: List<LeaveRequest>,
    isActiveFilter: Boolean,
    filterStatus: String,
    searchQuery: String,
    dateRangeStart: Long?,
    dateRangeEnd: Long?,
    navigateRequest: () -> Unit,
    onResetFilters: () -> Unit,
    onSetStatusFilter: (String) -> Unit,
    onSetSelectedRequest: (LeaveRequest) -> Unit,
    onUpdateNameFilter: (String) -> Unit,
    onUpdateDateRangeFilter: (Long?, Long?) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToDirectory: () -> Unit,
    currentRequestType: String,
    onTypeChange: (String) -> Unit
) {
    var showFilterSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var isMenuOpen by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBarSection() },
        containerColor = Color(0xFFF5F7FA),
        bottomBar = {
            DeanBottomNavigationBar(
                currentRoute = "history",
                onNavigateToHome = onNavigateToHome,
                onNavigateToHistory = {},
                onNavigateToDirectory = onNavigateToDirectory
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(20.dp)
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

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item { FilterButton(onClick = { showFilterSheet = true }) }

                    if (isActiveFilter) {
                        item { ResetButton(onClick = onResetFilters) }
                    }

                    item {
                        Box(modifier = Modifier.height(24.dp).width(1.dp).background(Color.LightGray))
                    }

                    val filters = listOf("Svi", "Odobreni", "Odbijeni")
                    items(filters) { filter ->
                        FilterChipItem(
                            text = filter,
                            selected = (filterStatus) == filterMap[filter]!!,
                            onClick = { onSetStatusFilter(filterMap[filter]!!) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Lista filtriranih zahtjeva
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp)
                ) {
                    if (historyRequests.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                                Text("Nema obrađenih zahtjeva.", color = Color.Gray)
                            }
                        }
                    } else {
                        items(historyRequests) { request ->
                            RequestCardDean(request, navigateRequest, { onSetSelectedRequest(it) })
                        }
                    }
                }
            }
        }

        if (showFilterSheet) {
            ModalBottomSheet(
                onDismissRequest = { showFilterSheet = false },
                sheetState = sheetState,
            ) {
                FilterBottomSheetContent(
                    currentName = searchQuery,
                    currentStartMillis = dateRangeStart,
                    currentEndMillis = dateRangeEnd,
                    onNameChange = { onUpdateNameFilter(it) },
                    onDateRangeChange = { start, end -> onUpdateDateRangeFilter(start, end) },
                    onApply = { showFilterSheet = false },
                    currentRequestType = currentRequestType,
                    onTypeChange = onTypeChange
                )
            }
        }
    }
}


@Composable
fun ResetButton(onClick: () -> Unit){
    Surface(
        onClick = onClick,
        color = Color(0xFFFFEBEE),
        shape = CircleShape,
        modifier = Modifier.size(32.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Poništi filtere",
                tint = Color(0xFFC62828),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}


@Composable
fun FilterBottomSheetContent(
    currentName: String,
    currentStartMillis: Long?,
    currentEndMillis: Long?,
    onNameChange: (String) -> Unit,
    onDateRangeChange: (Long?, Long?) -> Unit,
    onApply: () -> Unit,
    currentRequestType: String,
    onTypeChange: (String) -> Unit
) {

    var showTypeMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 48.dp)
    ) {
        Text(
            text = "Filtriraj po",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "Datum",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))

        DateRangeFilterField(
            startMillis = currentStartMillis,
            endMillis = currentEndMillis,
            onDateSelected = onDateRangeChange
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Ime", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = currentName,
            onValueChange = onNameChange,
            placeholder = { Text("Ime Prezime") },
            singleLine = true,
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                focusedBorderColor = Color(0xFF1E2A47),
                unfocusedBorderColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        RequestTypeSelectorDean(
            selectedType = currentRequestType,
            isExpanded = showTypeMenu,
            onExpandChange = {showTypeMenu = it},
            onTypeSelected = {onTypeChange(it)}
        )
        Spacer(modifier = Modifier.height(32.dp))


        Button(
            onClick = onApply,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2A47)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Primijeni", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }


    }
}

@Composable
// function for filtering by request type
fun RequestTypeSelectorDean(
    selectedType: String,
    isExpanded : Boolean,
    onExpandChange: (Boolean) -> Unit,
    onTypeSelected: (String) -> Unit
){
    val textToShow = if (selectedType.isNotEmpty()) selectedType else "Type of Request"
    Box(modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center){
        OutlinedCard (onClick = {onExpandChange(true)},
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)){
            Row(modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically){

                Text(textToShow, color = if(textToShow.contains("Type")) Color.Gray else Color.Black)
                Icon(Icons.Default.KeyboardArrowDown,null)

            }
        }
        DropdownMenu(expanded = isExpanded,
            onDismissRequest = {onExpandChange(false)},
            modifier = Modifier.fillMaxWidth(0.8f).height(280.dp)) {

            DropdownMenuItem (text = {Text("Svi")},
                onClick = {
                    onTypeSelected("")
                    onExpandChange(false)
                })

            RequestType.allOptions.forEach { type ->
                DropdownMenuItem(text = {Text(type)},
                    onClick = {
                        onTypeSelected(type)
                        onExpandChange(false)
                    })

            }
        }
    }
}

@Composable
fun DateRangeFilterField(
    startMillis: Long?,
    endMillis: Long?,
    onDateSelected : (Long?, Long?) -> Unit
){
    var showDatePicker by remember {mutableStateOf(false)}

    val displayText = if(startMillis!=null && endMillis!=null){
        "${convertMillisToDate(startMillis)} - ${convertMillisToDate(endMillis)}"
    }else{
        ""
    }

    OutlinedTextField(
        value = displayText,
        onValueChange = {},
        placeholder = { Text("Datum od - do") },
        label = {Text("Raspon datuma")},
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = {showDatePicker = true}) {
                Icon(Icons.Default.DateRange, contentDescription = "Odaberi datum")
            }
        },
        modifier = Modifier.fillMaxWidth().clickable{showDatePicker=true},
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        enabled = false
    )

    if(showDatePicker){
        val dateRangePickerState = rememberDateRangePickerState(
            initialSelectedStartDateMillis  = startMillis,
            initialSelectedEndDateMillis = endMillis
        )

        DatePickerDialog(
            onDismissRequest = {showDatePicker = false},
            confirmButton = {
                TextButton(
                    onClick = {
                        onDateSelected(
                            dateRangePickerState.selectedStartDateMillis,
                            dateRangePickerState.selectedEndDateMillis
                        )
                        showDatePicker = false
                    }
                ){
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = {showDatePicker = false}){
                    Text("Otkaži")
                }
            }
        ) {
            DateRangePicker(state = dateRangePickerState,
                title = null,
                headline = {
                    Row(modifier = Modifier.padding(16.dp)) {
                        val startText = dateRangePickerState.selectedStartDateMillis?.let { convertMillisToDate(it) } ?: "Početak"
                        val endText = dateRangePickerState.selectedEndDateMillis?.let { convertMillisToDate(it) } ?: "Kraj"
                        Box(Modifier.weight(1f)) {
                            Text(text = startText)
                        }
                        Box(Modifier.weight(1f)) {
                            Text(text = endText)
                        }
                    }
                },
                showModeToggle = false,
                modifier = Modifier.height(400.dp))
        }
    }
}


@Composable
fun FilterButton(onClick:() -> Unit){
    Surface(
        onClick = onClick,
        color = Color(0xFF1E2A47),
        shape  = RoundedCornerShape(50),
        modifier = Modifier.height(32.dp),
    ) {

        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp))
        {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = "filter",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "Filter",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )

        }
    }
}


@Composable
fun FilterChipItem(text: String, selected : Boolean, onClick: () -> Unit) {
    val backgroundColor = if (selected) Color(0xFF1976D2) else Color.White
    val contentColor = if(selected) Color.White else Color.Gray
    val borderColor = if(selected) Color.Transparent else Color.LightGray

    Surface(
        onClick = onClick,
        color = backgroundColor,
        contentColor = contentColor,
        shape = RoundedCornerShape(50),
        border = if(!selected) BorderStroke(1.dp, borderColor) else null,
        modifier = Modifier.height(32.dp)

    ){
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        ){
            Text(
                text= text,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if(selected) FontWeight.SemiBold else FontWeight.Normal
            )
        }

    }

}


@Preview(showBackground = true)
@Composable
fun DeanHistoryScreenPreview() {
        DeanHistoryScreenContent(
            isLoading = false,
            historyRequests = getMockRequests().filter { it.status != RequestSatus.PendingDean && it.status!= RequestSatus.Pending},
            isActiveFilter = false,
            filterStatus = "All",
            searchQuery = "",
            dateRangeStart = null,
            dateRangeEnd = null,
            navigateRequest = {},
            onResetFilters = {},
            onSetStatusFilter = {},
            onSetSelectedRequest = {},
            onUpdateNameFilter = {},
            onUpdateDateRangeFilter = { _, _ -> },
            onNavigateToHome = {},
            onNavigateToDirectory = {},
            currentRequestType =  " ",
            onTypeChange = { _  -> }
        )
}