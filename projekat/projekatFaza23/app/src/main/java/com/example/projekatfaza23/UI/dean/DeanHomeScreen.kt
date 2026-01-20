package com.example.projekatfaza23.UI.dean

import android.R
import android.util.Log
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
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.projekatfaza23.model.FileInfo
import com.example.projekatfaza23.model.LeaveDates
import com.example.projekatfaza23.model.LeaveRequest
import com.example.projekatfaza23.model.RequestSatus
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.projekatfaza23.UI.home.RequestsCard
import com.example.projekatfaza23.UI.home.TopAppBarSection
import com.example.projekatfaza23.UI.request.RequestType
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.semantics.SemanticsProperties.ImeAction
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

//mock podaci za prikaz, da vidim kako ce preview izgledati
fun getMockRequests(): List<LeaveRequest> {
    val now = System.currentTimeMillis()
    val dayInMillis = 24 * 60 * 60 * 1000L

    return listOf(
        LeaveRequest(
            id = "101",
            status = RequestSatus.Pending,
            type = RequestType.SICK_LEAVE.displayName,
            explanation = "Imam jaku gripu i temperaturu.",
            userEmail = "marko.peric@firma.com",
            leave_dates = listOf(
                LeaveDates(
                    start = Timestamp(Date(now + dayInMillis)),
                    end = Timestamp(Date(now + (3 * dayInMillis)))
                )
            )
        ),
        LeaveRequest(
            id = "102",
            status = RequestSatus.Approved,
            type = RequestType.HOUSE_CONSTRUCTION.displayName,
            explanation = "Renoviranje kupatila, moram biti prisutan.",
            userEmail = "ana.anic@firma.com",
            leave_dates = listOf(
                LeaveDates(
                    start = Timestamp(Date(now - (2 * dayInMillis))),
                    end = Timestamp(Date(now))
                )
            )
        ),
        LeaveRequest(
            id = "103",
            status = RequestSatus.Denied,
            type = RequestType.ANNUAL_LEAVE.displayName,
            explanation = "Planirani ljetni godišnji odmor.",
            userEmail = "ivan.horvat@firma.com",
            leave_dates = listOf(
                LeaveDates(
                    start = Timestamp(Date(now + (10 * dayInMillis))),
                    end = Timestamp(Date(now + (15 * dayInMillis)))
                )
            )
        ),
        LeaveRequest(
            id = "104",
            status = RequestSatus.Pending,
            type = RequestType.RELOCATION.displayName,
            explanation = "Selidba u novi stan.",
            userEmail = "petra.petrovic@firma.com",
            leave_dates = listOf(
                LeaveDates(
                    start = Timestamp(Date(now + (5 * dayInMillis))),
                    end = Timestamp(Date(now + (7 * dayInMillis)))
                )
            ),
            file_info = FileInfo("ugovor_stan.pdf", "pdf", "http://fakeurl.com/doc")
        )
    )
}

val filterMap = mapOf(
    "Svi" to "All",
    "Odobreni" to "Approved",
    "Na čekanju" to "Pending",
    "Odbijeni" to "Denied"
)

//main screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeanHomeScreen(viewModel: DeanViewModel){
    val uiState by viewModel.uiState.collectAsState()

    var showFilterSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var selectedStatusChip by remember { mutableStateOf("Svi") }

    val requestToDisplay = viewModel.filteredRequests.collectAsState()

    Scaffold(
        topBar =  {TopAppBarSection()},
        containerColor = Color(0xFFF5F7FA),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Otvori direktorij  */ },
                containerColor = Color(0xFF1E2A47),
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Filled.Groups, contentDescription = "Directory")
            }
        }
    ){
        paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ){
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Lista zahtjeva",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )


            Spacer(modifier = Modifier.height(12.dp))

            //status filteri
            LazyRow (
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ){

                item {
                    FilterButton(onClick = { showFilterSheet = true })
                }

                if(uiState.isActiveFilter == true){
                    item{
                        ResetButton(onClick = {viewModel.resetFilters()
                        selectedStatusChip = "Svi"})
                    }
                }

                item {
                    Box(modifier = Modifier
                        .height(24.dp)
                        .width(1.dp)
                        .background(Color.LightGray))
                }

                val filters = listOf("Svi", "Odobreni", "Na čekanju", "Odbijeni")
                items(filters){filter ->
                    FilterChipItem(
                        text = filter,
                        selected = selectedStatusChip == filter,
                        onClick = {
                            selectedStatusChip = filter
                            viewModel.setStatusFilter(filterMap[filter]!!)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            //lista zahtjeva
            if(uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                    CircularProgressIndicator()
                }
            }else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(requestToDisplay.value) { request ->
                        RequestCardDean(request)
                    }
                }
            }
        }
        if(showFilterSheet){
            ModalBottomSheet(
                onDismissRequest = {showFilterSheet = false},
                sheetState = sheetState,

            ) { FilterBottomSheetContent(
                onApply = { date, name -> viewModel.setAdvancedFilter(date, name)
                showFilterSheet = false }
            )}
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
fun FilterBottomSheetContent(onApply: (String, String) -> Unit) {
    var dateText by remember { mutableStateOf("") }
    var nameText by remember { mutableStateOf("") }

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
        OutlinedTextField(
            value = dateText,
            onValueChange = { newText -> dateText = newText },
            placeholder = { Text("dd/mm/yyyy") },  //should be a pop up calendar
            singleLine = true,
            trailingIcon = {
                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color.Gray)
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
        /* TODO - popup kalendar umjesto upisivanja daatuma  */

        Spacer(modifier = Modifier.height(16.dp))

        Text("Ime", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = nameText,
            onValueChange = { newText -> nameText = newText },
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

        Button(
            onClick = { onApply(dateText, nameText)
                dateText = ""
                nameText = ""},
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2A47)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Primijeni", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }


    }
}

@Composable
fun FilterButton(onClick:() -> Unit){
    Surface(
        onClick = onClick,
        color = Color(0xFF1E2A47),
        shape  = RoundedCornerShape(50),
        modifier = Modifier.height(32.dp)) {

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
fun RequestCardDean(request: LeaveRequest) {
    val displayName = request.userEmail.substringBefore("@")
        .replace(".", " ")
        .split(" ")
        .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }

    val dateText = formatLeaveDates(request.leave_dates)

    val (statusLabel, statusColor) = when (request.status) {
        RequestSatus.Approved -> "Odobren" to Color(0xFF2E7D32)
        RequestSatus.Pending -> "Na čekanju" to Color(0xFFF9A825)
        RequestSatus.Denied -> "Odbijen" to Color(0xFFC62828)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ){
        Column(
            modifier = Modifier.padding(16.dp)
        ){
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Surface(
                    color = statusColor.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, statusColor.copy(0.3f)),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.width(100.dp)
                ){
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(vertical = 6.dp)
                    ) {
                        Text(
                            statusLabel,
                            color = statusColor,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                        )
                    }
                }
            }


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = request.type,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )
            }
                Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ){
                Icon(Icons.Default.CalendarToday,
                     contentDescription = null,
                     tint = Color.Gray,
                     modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = dateText,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

            }


        }
    }
}


fun formatLeaveDates(dates: List<LeaveDates?>? ):String{
    if(dates.isNullOrEmpty()) return "No dates"

    val firstRange = dates[0] ?: return "No dates "
    val start = firstRange.start?.toDate()
    val end = firstRange.end?.toDate()

    if(start == null || end ==null) return "Invalid date"
    val format = SimpleDateFormat("YYYY MMM dd")
    return "${format.format(start)} - ${format.format(end)}"
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
fun HRAppPreview() {
    DeanHomeScreen(viewModel())
}