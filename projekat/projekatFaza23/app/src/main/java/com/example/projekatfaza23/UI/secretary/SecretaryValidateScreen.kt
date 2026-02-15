package com.example.projekatfaza23.UI.secretary

import android.view.RoundedCorner
import android.view.Surface
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.projekatfaza23.UI.dean.RequestDetailsCard
import com.example.projekatfaza23.UI.dean.UserProfileHeader
import com.example.projekatfaza23.UI.dean.primaryColor
import com.example.projekatfaza23.UI.home.TopAppBarSection
import com.example.projekatfaza23.UI.request.RequestHeader
import com.example.projekatfaza23.model.LeaveRequest
import com.example.projekatfaza23.model.RequestSatus


@Composable
fun SecretaryValidateScreen(viewModel: SecretaryViewModel, navigateHome: () -> Unit){

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val request = uiState.selectedRequest
    val stats = uiState.stats

    if(request == null){
        Box(modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center){
            CircularProgressIndicator()
        }
        return
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
            SecretaryBottomBar(
                onReject = { viewModel.denyRequest()
                              navigateHome() },
                onForward = {
                        viewModel.validateRequest()
                        navigateHome() }
            )
        }
    ){ paddingValues ->

        Column(
            modifier = Modifier.padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ){
            Spacer(modifier = Modifier.height(8.dp))

            UserProfileHeader("ajla.bulic@fet.ba", null)
            Divider(color = Color.LightGray.copy(0.5f), thickness = 1.dp)

            RequestDetailsCard(request)

            Text(
                text = "Provjera stanja dana" ,
                style = MaterialTheme.typography.labelLarge,
                color  = Color.Gray
            )

            StatsValidationCard(stats)


            Column(verticalArrangement = Arrangement.spacedBy(8.dp)){
                Text(
                    text =  "Napomena za dekana (Opcionalno)",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Gray
                )

                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = { Text("Npr. Provjereno, dani se slažu sa evidencijom...", style = MaterialTheme.typography.bodySmall, color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = primaryColor
                    ),
                    minLines = 3
                )

            }

        }

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
                Text("Vrati na doradu", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, fontSize = 12.sp)
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

@Composable
@Preview(showBackground = true)
fun SecretaryValidatePreview(){
    SecretaryValidateScreen (viewModel(), {})
}