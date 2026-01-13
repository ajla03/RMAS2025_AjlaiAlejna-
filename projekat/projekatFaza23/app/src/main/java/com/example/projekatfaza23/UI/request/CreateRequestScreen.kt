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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.LaunchedEffect
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
import com.example.projekatfaza23.UI.home.LeaveUiState
import com.example.projekatfaza23.UI.home.TopAppBarSection
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun NewRequestScreen(onBack: () -> Unit, viewModel: InboxRequestViewModel = viewModel()){
    val uiState by viewModel.uiState.collectAsState()


    NewRequestContent(
        uiState = uiState,
        onBack = onBack,
        onTypeChange = { viewModel.onTypeChange(it) },
        onDatesSelected = { from, to -> viewModel.onDatesSelected(from, to) },
        onExplanationChange = { viewModel.onExplanationChange(it) },
        sendRequest = { viewModel.sendRequest() },
        onFileSelected = {uri, name -> viewModel.onFileAttached(uri, name)},
        resetSuccessState = { viewModel.resetSuccessState() }
    )

}

@Composable
fun NewRequestContent(
    uiState: LeaveUiState,
    onBack: () -> Unit,
    onTypeChange: (String) -> Unit,
    onDatesSelected: (Long?, Long?) -> Unit,
    onFileSelected: (Uri?, String) -> Unit,
    onExplanationChange: (String) -> Unit,
    sendRequest: () -> Unit,
    resetSuccessState: () -> Unit
){
    val context = LocalContext.current
    val filePickerLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { //kreiramo launcher  za file picker
        uri : Uri? ->
        uri?.let {
            val fileName = getFileName(context, it) ?: "Unknown file"
            onFileSelected(it, fileName)
        }
    }

    var showTypeMenu by remember { mutableStateOf(false) }
    var  showDatePicker  by remember {mutableStateOf(false)}

    Scaffold(topBar = {
        Column{
            TopAppBarSection()
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
                onTypeSelected = { onTypeChange(it)}
            )

            Spacer(modifier = Modifier.height(16.dp))

            DatePickerField(
                dateFrom = formatMillisToDate(uiState.currentRequest.dateFrom),
                dateTo = formatMillisToDate(uiState.currentRequest.dateTo),
                onClick = {showDatePicker = true})

            Spacer(modifier = Modifier.height(16.dp))
            Text("Details", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            ExplanationField(
                value = uiState.currentRequest.explanation,
                onValueChange = { onExplanationChange(it)}
            )

            Spacer(modifier = Modifier.height(24.dp))
            AttachmentSection(
                fileName = uiState.currentRequest.fileName,
                onAddClick = {filePickerLauncher.launch("*/*")}
            )
            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    android.util.Log.d("PROVJERA", "KLIKNUT JE SEND!")
                    sendRequest()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                enabled = !uiState.isLoading,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004D61), contentColor = Color.White)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Send Request", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
            LaunchedEffect(uiState.isSuccess) {
                if(uiState.isSuccess){
                    onBack()
                    resetSuccessState()
                }
            }

            if(showDatePicker){
                DateRangePickerPopup(onDismiss = {showDatePicker = false},
                    onDatesSelected = {from, to -> onDatesSelected(from, to)})
            }



        }
    }
}
@Composable
fun AttachmentSection(fileName: String, onAddClick: ()-> Unit){
    Column{
        Text("Attachments", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically){
            AssistChip(onClick =  onAddClick,
                      label = {Text("Add a file")},
                      leadingIcon = {Icon(Icons.Default.Add, null)},
                      shape  = RoundedCornerShape(50.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = if(fileName.isEmpty()) "No file selected" else fileName, color = if (fileName.isEmpty()) Color.Gray else Color(0xFF004D61), fontSize = 12.sp)
        }
    }

}

fun getFileName(context: Context, uri: Uri): String?{
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null) //vraca metapodatke za taj uri
        try {
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) {
                    result = cursor.getString(index)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
    }

    if (result == null) {
        result = uri.path
        val cut = result?.lastIndexOf('/') ?: -1
        if (cut != -1) result = result?.substring(cut + 1)
    }
    return result
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerPopup(onDismiss: () -> Unit, onDatesSelected: (Long?, Long?) -> Unit){
    val state = rememberDateRangePickerState() //cuva  selectedStartDateMillis i EndDateMillis

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = {
            onDatesSelected(state.selectedStartDateMillis, state.selectedEndDateMillis)
            onDismiss() //zatvara popup
        }){Text("OK")}},
        dismissButton = { TextButton(onClick = onDismiss){Text("CANCEL")}}
    ) {
        DateRangePicker(state = state,
                        modifier = Modifier.padding(16.dp),
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


@Preview(showBackground = true)
@Composable
fun NewRequestPreview(){
  NewRequestContent(uiState = LeaveUiState(),
      onBack = {},
      onTypeChange = {},
      onDatesSelected = {_,_ -> },
      onExplanationChange = {},
      sendRequest = {},
      resetSuccessState = {},
      onFileSelected = {_, _ -> }
  )
}