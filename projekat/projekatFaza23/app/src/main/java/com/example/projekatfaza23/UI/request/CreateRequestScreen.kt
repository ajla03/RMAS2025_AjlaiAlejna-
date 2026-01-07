package com.example.projekatfaza23.UI.request

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projekatfaza23.UI.home.InboxRequestViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun NewRequestScreen(onBack: () -> Unit, viewModel: InboxRequestViewModel = viewModel()){
    val uiState by viewModel.uiState.collectAsState()

    var  showTypeMenu by remember {mutableStateOf(false)}
    var  showDatePicker  by remember {mutableStateOf(false)}

    Scaffold(topBar = {
        Column{
            TopAppBarSection(onBack)
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .background(Color.White)
            )
            RequestHeader(onBack)

        }}){ padding ->
        Column(modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .padding(20.dp)){

            RequestTypeSelector(
                selectedType = uiState.currentRequest.type,
                isExpanded = showTypeMenu,
                onExpandChange = {showTypeMenu = it},
                onTypeSelected = { viewModel.onTypeChange(it)}
            )

            Spacer(modifier = Modifier.height(16.dp))

            DatePickerField(
                dateFrom = uiState.currentRequest.dateFrom,
                dateTo = uiState.currentRequest.dateTo,
                onClick = {showDatePicker = true})

            Spacer(modifier = Modifier.height(16.dp))

            ExplanationField(
                value = uiState.currentRequest.explanation,
                onValueChange = {viewModel.onExplanationChange(it)}
            )

            Spacer(modifier = Modifier.height(24.dp))
            AttachmentSection()
            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.submitLeaveRequest()
                            onBack()
                          },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004D61), contentColor = Color.White)
            ){
                Text("Send Request", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            if(showDatePicker){
                DateRangePickerPopup(onDismiss = {showDatePicker = false},
                                     onDatesSelected = {from, to -> viewModel.onDatesSelected(from, to)})
            }



        }
    }
}

@Composable
fun AttachmentSection(){
    Column{
        Text("Attachments", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically){
            AssistChip(onClick = {/* open file picker*/ },
                      label = {Text("Add a file")},
                      leadingIcon = {Icon(Icons.Default.Add, null)},
                      shape  = RoundedCornerShape(50.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("file_name.pdf", color = Color.Gray, fontSize = 12.sp)
        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerPopup(onDismiss: () -> Unit, onDatesSelected: (String, String) -> Unit){
    val state = rememberDateRangePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = {
            val from = formatMillisToDate(state.selectedStartDateMillis)
            val to = formatMillisToDate(state.selectedEndDateMillis)
            onDatesSelected(from, to)
            onDismiss()
        }){Text("OK")}},
        dismissButton = { TextButton(onClick = onDismiss){Text("CANCEL")}}
    ) {
        DateRangePicker(state = state,
                        modifier = Modifier.weight(1f).padding(16.dp),
                        title = {Text("SELECT DATES")})
    }
}

fun formatMillisToDate(millis: Long?): String{
    if(millis == null) return ""
    val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}
@Composable
fun DatePickerField(dateFrom: String, dateTo: String, onClick : () -> Unit){
    val textToShow = if (dateFrom.isNotEmpty()) "$dateFrom - $dateTo" else "Date (from - to)"
    OutlinedCard (onClick = onClick,
                 shape = RoundedCornerShape(12.dp),
                 modifier = Modifier
                     .fillMaxWidth()
                     .height(60.dp)) {
        Row(modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically){
            Text(textToShow, color = if(dateFrom.isEmpty()) Color.Gray else Color.Black)
            Icon(Icons.Default.KeyboardArrowDown,null)
        }
    }
}

@Composable
fun ExplanationField(value: String, onValueChange: (String) -> Unit){
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {Text("Explanation")},
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        shape = RoundedCornerShape(12.dp)
    )
}
@Composable
fun RequestTypeSelector(
    selectedType: String,
    isExpanded : Boolean,
    onExpandChange: (Boolean) -> Unit,
    onTypeSelected: (String) -> Unit
){
    val textToShow = if (selectedType.isNotEmpty()) selectedType else "Type of Request"
    Box{
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
                    modifier = Modifier.fillMaxWidth(0.85f)) {

            //treba mozda vise tipova ovdje
            listOf("Bolovanje", "Godisnji odmor").forEach { type ->
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
fun RequestHeader(onBack: () -> Unit){
    Surface(color = Color(0xFF004D61), modifier = Modifier.fillMaxWidth()){
        Row(modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically){
            Icon(Icons.Default.ArrowBack, contentDescription = null,
                tint = Color.White,
                modifier = Modifier.clickable{onBack()})
            Spacer(modifier = Modifier.width(24.dp))
            Text("New Request", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }

}
@Composable
fun TopAppBarSection(onBack: () -> Unit){
    Surface(color = Color(0xFFE0E0E0)){
        Row(modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically){
            Surface(
                color = Color.Gray,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.size(40.dp)
            ) {
                Text("HR", modifier = Modifier.wrapContentSize(), fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text("App name", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

    }
}

@Preview(showBackground = true)
@Composable
fun NewRequestPreview(){
    NewRequestScreen({})
}