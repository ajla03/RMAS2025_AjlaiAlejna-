package com.example.projekatfaza23.UI.dean

import android.R
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
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
import com.example.projekatfaza23.UI.home.TopAppBarSection
import com.example.projekatfaza23.UI.request.RequestType
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.SemanticsProperties.ImeAction
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.projekatfaza23.data.auth.UserManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.compose
import java.util.Locale

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
    "Na čekanju" to "PendingDean",
    "Odbijeni" to "Denied"
)

//main screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeanHomeScreen(viewModel: DeanViewModel, navigateDirectory: () -> Unit, navigateRequest: () -> Unit, onLogoutClick: () -> Unit){
/*
     mozda cemo trebati ako se podaci budu fetchali sa interneta kada kliknemo na request
    LaunchedEffect(Unit) {
        delay(400)
        viewModel.resetSelectedRequest()
    }
*/
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showFilterSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var showProfileDialog by remember { mutableStateOf(false) }
    val user = UserManager.currentUser.collectAsState().value

    Scaffold(
        topBar =  {
            TopAppBarSection()},
        containerColor = Color(0xFFF5F7FA),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigateDirectory() },
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Lista zahtjeva",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                IconButton(onClick = { showProfileDialog = true }) {
                    DeanProfileAvatar(
                        imageUrl = user?.profilePictureURL.toString(),
                        email = user?.email ?: "",
                        modifier = Modifier.size(40.dp)
                            .border( 
                                width = 1.5.dp,
                                color = Color.Gray,
                                shape = CircleShape
                            ),
                        fontSize = 16.sp
                    )
                }
            }


            Spacer(modifier = Modifier.height(26.dp))

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
                        ResetButton(onClick = {viewModel.resetFilters()})
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
                        selected = (uiState.filterStatus) == filterMap[filter]!!,
                        onClick = {
                            viewModel.setStatusFilter(filterMap[filter]!!)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

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
                    items(uiState.displayRequests) { request ->
                        RequestCardDean(request, navigateRequest, {viewModel.setSelectedRequest(it)})
                    }
                }
            }
        }

        if (showProfileDialog) {
            ProfileDialog(
                deanName = (user?.name ?: "") + " " + (user?.lastName ?: ""),
                deanEmail = (user?.email ?: ""),
                onDismiss = { showProfileDialog = false },
                imageUrl = user?.profilePictureURL.toString(),
                onLogout = {
                    showProfileDialog = false
                    onLogoutClick() },
                role = "Dekan"
            )
        }

        if(showFilterSheet){
            ModalBottomSheet(
                onDismissRequest = {showFilterSheet = false},
                sheetState = sheetState,

            ) { FilterBottomSheetContent(
                currentName = uiState.searchQuery,
                currentStartMillis = uiState.dateRange.first,
                currentEndMillis = uiState.dateRange.second,
                onNameChange = { viewModel.updateNameFilter(it) },
                onDateRangeChange = { start, end -> viewModel.updateDateRangeFilter(start, end) },
                onApply = { showFilterSheet = false }
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
fun FilterBottomSheetContent(
    currentName: String,
    currentStartMillis: Long?,
    currentEndMillis: Long?,
    onNameChange: (String) -> Unit,
    onDateRangeChange: (Long?, Long?) -> Unit,
    onApply: () -> Unit
) {

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

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
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
fun RequestCardDean(request: LeaveRequest, navigateRequest: () -> Unit, setRequest: (LeaveRequest) -> Unit) {
    val displayName = request.userEmail.substringBefore("@")
        .replace(".", " ")
        .split(" ")
        .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }

    val dateText = formatLeaveDates(request.leave_dates)

    val (statusColor, statusLabel) = when (request.status) {
        RequestSatus.Pending -> Color(0xFFF57C00) to "Na čekanju"
        RequestSatus.PendingDean -> Color(0xFF1976D2) to "Čeka Dekana"
        RequestSatus.Approved -> Color(0xFF2E7D32) to "Odobreno"
        RequestSatus.Denied -> Color(0xFFC62828) to "Odbijeno"
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().clickable{  setRequest(request)
            navigateRequest() },
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



@Composable
fun ProfileDialog(onDismiss: () -> Unit,
                      onLogout: () -> Unit,
                      deanName : String,
                      deanEmail: String ,
                      imageUrl: String?,
                      role: String){
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                DeanProfileAvatar(
                    imageUrl = imageUrl,
                    email = deanEmail,
                    modifier = Modifier.size(80.dp),
                    fontSize = 28.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = deanName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = deanEmail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Text(
                    text = "Uloga: $role",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF1E2A47),
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onLogout,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFEBEE),
                        contentColor = Color(0xFFC62828)
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Odjavi se", fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = onDismiss) {
                    Text("Zatvori", color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun DeanProfileAvatar(imageUrl: String?, email: String, modifier: Modifier, fontSize: androidx.compose.ui.unit.TextUnit){
    val initials = extractInitials(email)

    if (!imageUrl.isNullOrBlank()) {
        SubcomposeAsyncImage(
            model = imageUrl,
            contentDescription = "Profile Picture",
            modifier = modifier.clip(CircleShape),
            contentScale = ContentScale.Crop
        ) {
            val state = painter.state
            if (state is AsyncImagePainter.State.Loading) {
                CircularProgressIndicator(modifier = Modifier.padding(2.dp))
            } else if (state is AsyncImagePainter.State.Error) {
                InitialsAvatarBackground(email = email, initials = initials, modifier = modifier, fontSize = fontSize)
            } else {
                SubcomposeAsyncImageContent()
            }
        }
    } else {
        InitialsAvatarBackground(email = email, initials = initials, modifier = modifier, fontSize = fontSize)
    }
}


@Composable
fun InitialsAvatarBackground(
    email: String,
    initials: String,
    modifier: Modifier,
    fontSize: androidx.compose.ui.unit.TextUnit
) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = getAvatarColor(email).copy(alpha = 0.8f),
        contentColor = Color.White
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = initials,
                style = MaterialTheme.typography.labelLarge,
                fontSize = fontSize,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HRAppPreview() {
    DeanHomeScreen(viewModel(), {}, {}, {})

}