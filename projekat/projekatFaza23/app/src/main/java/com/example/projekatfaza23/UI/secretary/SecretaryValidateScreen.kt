package com.example.projekatfaza23.UI.secretary

import android.view.RoundedCorner
import android.view.Surface
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projekatfaza23.UI.dean.EmployeeCommentBox
import com.example.projekatfaza23.UI.dean.RequestDetailsCard
import com.example.projekatfaza23.UI.dean.UserProfileHeader
import com.example.projekatfaza23.UI.dean.calculateDaysBetween
import com.example.projekatfaza23.UI.dean.primaryColor
import com.example.projekatfaza23.UI.home.TopAppBarSection
import com.example.projekatfaza23.UI.request.RequestHeader
import com.example.projekatfaza23.model.LeaveDates
import com.example.projekatfaza23.model.LeaveRequest
import com.example.projekatfaza23.model.RequestSatus
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun SecretaryValidateScreen(viewModel: SecretaryViewModel, navigateHome: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SecretaryValidateContent(
        uiState = uiState,
        navigateHome = navigateHome,
        onReject = {
            viewModel.denyRequest()
            navigateHome()
        },
        onForward = {
            selectedDateRange ->
            viewModel.validateRequest(selectedDateRange)
            navigateHome()
        },
        onExplanationChange = { viewModel.updateExplanation(it) }
    )
}
@Composable
fun SecretaryValidateContent(
    uiState: SecretaryUIState,
    navigateHome: () -> Unit,
    onReject: () -> Unit,
    onForward: (LeaveDates) -> Unit,
    onExplanationChange: (String) -> Unit
) {
    val request = uiState.selectedRequest

    if (request == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    var selectedOptionIndex by remember { mutableStateOf(0) }
    val selectedRange = request.leave_dates?.getOrNull(selectedOptionIndex)
    val daysInSelectedOption = if (selectedRange != null)
        calculateDurationInt(selectedRange.start, selectedRange.end) else 0

    var showValidationError by remember { mutableStateOf(false) }

    val displayStats = uiState.stats?.copy(
        pendingDays = daysInSelectedOption,
        remainingDays = (uiState.stats.totalDays - uiState.stats.usedDays)
    )

    val (statusColor, statusText, statusContainerColor) = when (request.status) {
        RequestSatus.Approved -> Triple(
            Color(0xFF2E7D32),
            "ZAHTJEV JE ODOBREN",
            Color(0xFFE8F5E9)
        ) // Zelena
        RequestSatus.Denied -> Triple(
            Color(0xFFC62828),
            "ZAHTJEV JE ODBIJEN",
            Color(0xFFFFEBEE)
        )   // Crvena
        RequestSatus.PendingDean -> Triple(
            Color(0xFF1976D2),
            "ČEKA ODOBRENJE DEKANA",
            Color(0xFFE3F2FD)
        )

        else -> Triple(Color.Gray, "STATUS NEPOZNAT", Color.LightGray)
    }

    Scaffold(
        containerColor = Color(0xFFF5F7FA),
        topBar = {
            Column {
                TopAppBarSection()
                RequestHeader("Validacija zahtjeva", { navigateHome() })
            }
        },
        bottomBar = {
            if (request.status == RequestSatus.Pending) {
                SecretaryBottomBar(
                    onReject = {
                        if (uiState.explanationSecretary.trim().isEmpty()) {
                            showValidationError = true
                        } else {
                            onReject()
                        }
                    },
                    onForward = {
                        if (selectedRange != null) {
                            onForward(selectedRange)
                        }
                    })
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            UserProfileHeader(request.userEmail, null)
            Divider(color = Color.LightGray.copy(0.5f), thickness = 1.dp)

            request.leave_dates?.filterNotNull()?.let { dates ->
                DateOptionSelector(
                    options = dates,
                    selectedIndex = selectedOptionIndex,
                    onOptionSelected = { newIndex -> selectedOptionIndex = newIndex }
                )
            }
            RequestDetailsCard(request)

            //komentar zaposlenika
            Text(
                    text = "Razlog / Napomena",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Gray,
            )
            EmployeeCommentBox(
                request?.explanation?.takeIf { it.isNotBlank() } ?: "Nema komentara zaposlenika."
            )

            Text(
                text = "Provjera stanja dana",
                style = MaterialTheme.typography.labelLarge,
                color = Color.Gray
            )
            if (displayStats != null)
                StatsValidationCard(displayStats)

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                val isProcessed = request.status != RequestSatus.Pending

                val displayText = if (isProcessed) {
                    uiState.explanationSecretary.takeIf { it.isNotBlank() } ?: "Nema komentara od sekretara."
                } else {
                    uiState.explanationSecretary
                }

                Text(
                    text = if (isProcessed) "Napomena sekretara" else "Napomena (Obavezno prilikom odbijanja)",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Gray
                )

                OutlinedTextField(
                    value = displayText,
                    onValueChange = onExplanationChange,
                    isError = !isProcessed && showValidationError && uiState.explanationSecretary.trim().isEmpty(),                    placeholder = {
                        Text(
                            "Npr. Provjereno, dani se slažu sa evidencijom...",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    },
                    readOnly = isProcessed,
                    enabled = !isProcessed,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = primaryColor,
                        errorBorderColor = Color(0xFFC62828),
                        errorCursorColor = Color(0xFFC62828),
                        disabledTextColor = Color.DarkGray,
                        disabledBorderColor = Color.LightGray
                    ),
                    minLines = 3

                )
            }

            if (request.status != RequestSatus.Pending) {
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = statusContainerColor),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Text(
                        text = statusText,
                        modifier = Modifier.padding(16.dp),
                        color = statusColor,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

        }
    }
    if (showValidationError) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showValidationError = false },
            title = {
                Text(
                    text = "Nedostaje napomena",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD32F2F)
                )
            },
            text = {
                Text("Da biste odbili zahtjev, morate unijeti razlog u polje 'Napomena'.")
            },
            confirmButton = {
                Button(
                    onClick = { showValidationError = false },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                ) {
                    Text("U redu")
                }
            },
            containerColor = Color.White
        )
    }
}
@Composable
fun SecretaryBottomBar(onReject: () -> Unit, onForward: () -> Unit){
    Surface(
        shadowElevation = 16.dp,
        color = Color.White,
        modifier  = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ){
        Row(modifier =  Modifier.padding(20.dp)
            .navigationBarsPadding(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)){

            OutlinedButton(
                onClick = onReject,
                modifier  = Modifier.weight(1f).height(52.dp),
                border = BorderStroke(1.5.dp, Color(0xFFD32F2F).copy(alpha = 0.8f) ),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F) ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Odbij", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, fontSize = 12.sp)
            }


            Button(
                onClick = onForward,
                modifier  = Modifier.weight(1f).height(52.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = primaryColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Proslijedi dekanu", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, fontSize = 12.sp, color = Color.White)
            }
        }
    }
}


@Composable
fun DateOptionSelector(
    options: List<LeaveDates>,
    selectedIndex: Int,
    onOptionSelected:  (Int) -> Unit
){
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Odaberite termin (Opcije)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E2A47)
            )

            Spacer(modifier = Modifier.height(12.dp))


            options.forEachIndexed { index, dateRange ->
                val daysCount = calculateDaysBetween(dateRange.start, dateRange.end)
                val isSelected = index == selectedIndex
                val dateString = formatDatesForDisplay(dateRange.start, dateRange.end)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOptionSelected(index) }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = {onOptionSelected(index) },
                        colors = RadioButtonDefaults.colors(selectedColor = primaryColor)
                    )
                    Column(modifier = Modifier.padding(start = 8.dp)) {
                        Text(
                            text = "Opcija ${index + 1}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                        Text(
                            text = dateString,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if(isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))

                    Surface(
                        color = if(isSelected) Color(0xFFE8F5E9) else Color(0xFFF5F7FA),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "$daysCount",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = if(isSelected) Color(0xFF2E7D32) else Color.Gray
                        )
                    }
                }
                if (index < options.size - 1) {
                    Divider(modifier = Modifier.padding(start = 40.dp), color = Color.LightGray.copy(0.3f))
                }
            }
        }
    }
}
@Composable
fun StatsValidationCard(stats: UserVacationStats) {
    val remainingAfter = stats.remainingDays - stats.pendingDays
    val isNegativeBalance = remainingAfter < 0

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween) {

                Text("STAVKA",
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    fontSize = 12.sp,
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.sp)

                Text("DANI",
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    fontSize = 12.sp,
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.sp)
            }
            Divider(modifier = Modifier.padding(vertical = 8.dp))

            StatRow("Ukupno godišnjeg:", "${stats.totalDays}", color = Color.Black)
            StatRow("Već iskorišteno:", "-${stats.usedDays}", Color.Gray)

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Trenutno raspoloživo:", style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
                Text("${stats.remainingDays}", style = MaterialTheme.typography.bodyLarge, color = Color(0xFF004D61), fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isNegativeBalance) Color(0xFFFFEBEE) else Color(0xFFE8F5E9), RoundedCornerShape(4.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Ovaj zahtjev:", color = Color.Black, style = MaterialTheme.typography.labelMedium)
                Text("-${stats.pendingDays}", fontWeight = FontWeight.Bold, color = Color.Red, style = MaterialTheme.typography.labelMedium)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {

                Text("STANJE NAKON ODOBRENJA:", fontWeight = FontWeight.Bold, fontSize = 12.sp, style = MaterialTheme.typography.headlineMedium)
                Text(
                    "${stats.remainingDays - stats.pendingDays}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (isNegativeBalance) Color.Red else Color(0xFF2E7D32)
                )
            }

            if (isNegativeBalance) {
                Text(
                    text = "UPOZORENJE: Zaposlenik nema dovoljno dana!",
                    color = Color.Red,
                    fontSize = 12.sp,
                )
            }
        }
    }
}

@Composable
fun StatRow(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label,
            fontSize = 14.sp,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray)

        Text(value,
            color = color,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            style = MaterialTheme.typography.bodyMedium)
    }
}

fun calculateDurationInt(start: Timestamp?, end: Timestamp?): Int {
    if (start == null || end == null) return 0
    val startMillis = start.toDate().time
    val endMillis = end.toDate().time
    val diff = endMillis - startMillis
    val days = (diff / (1000 * 60 * 60 * 24)).toInt() + 1
    return if (days > 0) days else 0
}

fun formatDatesForDisplay(start: Timestamp?, end: Timestamp?): String {
    if (start == null || end == null) return ""
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return "${sdf.format(start.toDate())} - ${sdf.format(end.toDate())}"
}




@Composable
@Preview(showBackground = true)
fun SecretaryValidatePreview() {
    val now = Date()
    val fiveDaysLater = Date(now.time + (5L * 24 * 60 * 60 * 1000)) // +5 dana
    val tenDaysLater = Date(now.time + (10L * 24 * 60 * 60 * 1000)) // +10 dana

    val datesOption1 = LeaveDates(
        start = Timestamp(now),
        end = Timestamp(fiveDaysLater)
    )

    val datesOption2 = LeaveDates(
        start = Timestamp(tenDaysLater),
        end = Timestamp(tenDaysLater)
    )

    val mockRequest = LeaveRequest(
        id = "1",
        status = RequestSatus.Pending,
        type = "Godišnji odmor",
        explanation = "Planiram putovanje sa porodicom.",
        userEmail = "test@fet.ba",
        leave_dates = listOf(datesOption1, datesOption2)
    )

    val mockStats = UserVacationStats(
        totalDays = 20,
        usedDays = 5,
        pendingDays = 6,
        remainingDays = 15
    )

    val mockState = SecretaryUIState(
        selectedRequest = mockRequest,
        stats = mockStats,
        explanationSecretary = "Sve izgleda uredno."
    )

    SecretaryValidateContent(
        uiState = mockState,
        navigateHome = {},
        onReject = {},
        onForward = {},
        onExplanationChange = {}
    )
}