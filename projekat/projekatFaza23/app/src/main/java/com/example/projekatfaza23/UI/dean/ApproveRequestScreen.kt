package com.example.projekatfaza23.UI.dean

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projekatfaza23.UI.home.TopAppBarSection
import com.example.projekatfaza23.UI.request.RequestHeader
import com.example.projekatfaza23.model.LeaveRequest
import com.example.projekatfaza23.model.RequestSatus
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

val primaryColor = Color(0xFF004D61)


/* TODO - make prettier screen, adjust spacing */ 
@Composable
fun ApproveRequestScreen(viewModel: DeanViewModel, navigateHome: () -> Unit){

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedRequest = uiState.selectedRequest

    if (selectedRequest == null) {
        return
    }

    val employees = uiState.employees

    val requestAuthor = remember(selectedRequest, employees) {
        employees.find { it.email == selectedRequest?.userEmail }
    }

    val request = selectedRequest!!
    val isProcessed = request.status != RequestSatus.PendingDean


    Scaffold(
        containerColor = Color(0xFFF5F7FA),
        topBar = {
        Column{
            TopAppBarSection()
            RequestHeader("Pregled zahtjeva", navigateHome = navigateHome)
        }},
        bottomBar = {
            if(!isProcessed)
                BottomBar(request, {viewModel.approveRequest(request)}, {viewModel.denyRequest(request)} ,navigateHome) }){ paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)) {
            Spacer(modifier = Modifier.height(4.dp))

            UserProfileHeader(selectedRequest?.userEmail ?: "", imageUrl = requestAuthor?.imageUrl.toString())
            Divider(color = Color.LightGray.copy(0.5f), thickness = 1.dp)

            RequestDetailsCard(request = request)

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)){
                //komentar zaposlenika
                Text(
                    text = "Razlog / Napomena",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                EmployeeCommentBox(selectedRequest?.explanation ?: "")
            }

            //odluka dekana
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Odluka dekana",
                style = MaterialTheme.typography.labelLarge,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = selectedRequest?.explanationDean ?: "",
                onValueChange = { newText ->
                    viewModel.setExplanationDean(newText)
                },
                placeholder = { Text("Upiši razlog odbijanja ili napomenu...", style = MaterialTheme.typography.bodyMedium)},
                modifier = Modifier.fillMaxWidth(),
                shape =  RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = primaryColor
                ),
                minLines = 4,
                readOnly = isProcessed,
                enabled = !isProcessed
            )
            }

            if (isProcessed) {
                Text(
                    text = "Ovaj zahtjev je već obrađen: ${request.status.name}",
                    color = if (request.status == RequestSatus.Approved) Color(0xFF2E7D32) else Color.Red,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

    }
}

fun timeStampToString(tmp : Timestamp):String{
    val date = tmp.toDate()
    val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return formatter.format(date)
}

@Composable
fun RequestDetailsCard(request: LeaveRequest){
    //kartica sa detaljima zahtjeva
    val dates = request.leave_dates?.firstOrNull()
    val startDateString = dates?.start?.let { timeStampToString(it) } ?: "-"
    val endDateString = dates?.end?.let { timeStampToString(it) } ?: "-"
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ){
        Column(modifier = Modifier.padding(20.dp)){

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Surface(
                    color = Color(0xFFFFF3E0),
                    shape = RoundedCornerShape(8.dp)
                ){
                    Text(
                        text = request.type,
                        color = Color(0xFFE65100),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }

                Text(
                    text = calculateDaysBetween(dates?.start, dates?.end),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            //datumi
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ){

                DateColumn("Početak", startDateString)

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center,
                ){
                    Divider(color = Color.LightGray.copy(0.5f), thickness = 1.dp,
                        modifier = Modifier.padding(start = 4.dp, end = 4.dp))
                    Icon(
                        Icons.Default.ArrowForward,
                        null,
                        tint = primaryColor,
                        modifier = Modifier.background(Color.White).padding(4.dp).size(16.dp)
                    )
                }

                DateColumn("Kraj",endDateString)

            }
        }

    }
}

@Composable
fun BottomBar(request: LeaveRequest, onApproved: (request: LeaveRequest) -> Unit, onDenied: (request: LeaveRequest) -> Unit, onBack: () -> Unit){
    Surface(
        shadowElevation = 16.dp,
        color = Color.White,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp).navigationBarsPadding(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ){
            OutlinedButton(
                onClick = {  onDenied(request)
                             onBack() },
                modifier = Modifier.weight(1f).height(52.dp),
                border = BorderStroke(1.5.dp, Color(0xFFD32F2F).copy(alpha = 0.8f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                shape = RoundedCornerShape(12.dp)
                ) { Text("Odbij", fontWeight = FontWeight.Bold) }

            Button(
                onClick = { onApproved(request)
                            onBack() },
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor ),
                elevation = ButtonDefaults.buttonElevation(4.dp)
            ) {
                Text("Odobri", fontWeight = FontWeight.Bold)
            }
        }
    }
}

fun calculateDaysBetween(start: Timestamp?, end: Timestamp?): String {
    if (start == null || end == null) return "0 dana"
    val diffInMillis = end.toDate().time - start.toDate().time
    val days = (diffInMillis / (1000 * 60 * 60 * 24)).toInt() + 1
    return "$days dana"
}
fun extractNameFromEmail(email: String):String{
    if (!email.contains("@")) return email

    val fullName = email.substringBefore("@")
        .split(".", "_")
        .joinToString(" "){ part ->
            part.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()            }
        }
    return fullName
}


fun extractInitials(email: String):String{
    val name = extractNameFromEmail(email)
    val parts = name.split(" ")
    val initials = if(parts.isEmpty()) ""
                    else if (parts.size==1) parts[0].take(1).uppercase()
                    else (parts[0].take(1) + parts[1].take(1)).uppercase()
    return initials
}

@Composable
fun EmployeeCommentBox(comment: String) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.05f)),
        modifier = Modifier.fillMaxWidth()
    ){
        Row(modifier = Modifier.padding(16.dp)){
            Box(modifier = Modifier
                .width(4.dp)
                .height(40.dp)
                .background(Color.Gray.copy(alpha = 0.3f), shape = CircleShape))
            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = comment,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray,
                lineHeight = 20.sp
            )
        }
    }
}
@Composable
fun UserProfileHeader(email: String, imageUrl: String?) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            shape = CircleShape,
            color = primaryColor.copy(alpha = 0.2f),
            modifier = Modifier.size(56.dp)
        ) {
            DeanProfileAvatar(
                imageUrl = imageUrl,
                email = email,
                modifier = Modifier.size(40.dp),
                fontSize = 18.sp
            )
        }
        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                extractNameFromEmail(email),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                email,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

        }
    }

}
@Composable
fun DateColumn(label: String, date : String){
    Column(
        horizontalAlignment = Alignment.Start
    ){
        Row(verticalAlignment = Alignment.CenterVertically){
                Icon(Icons.Default.DateRange, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                Spacer(modifier = Modifier.width(4.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(date, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)

    }
}



@Preview(showBackground = true)
@Composable
fun ApproveRequestPreview(){
    ApproveRequestScreen(viewModel(), {})
}