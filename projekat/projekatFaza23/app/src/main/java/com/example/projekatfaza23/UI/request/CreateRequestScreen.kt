package com.example.projekatfaza23.UI.request
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projekatfaza23.UI.request.InboxRequestViewModel
import com.example.projekatfaza23.UI.home.LeaveUiState
import com.example.projekatfaza23.UI.home.TopAppBarSection
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.example.projekatfaza23.UI.home.formatTimestampToDate
import com.example.projekatfaza23.model.LeaveDates

enum class RequestType(val displayName: String, val maxDays: Int) {
    // Godisnji odmor
    ANNUAL_LEAVE("Godišnji odmor", 0),
    // Brak
    MARRIAGE("Stupanje u brak", 5),
    MARRIAGE_CHILD("Brak djeteta", 2),

    // Porodica i zdravlje
    BIRTH_WIFE("Porođaj supruge", 5),
    DEATH_FAMILY("Smrt člana porodice", 5),
    ILLNESS_FAMILY("Teža bolest u porodici", 5),
    NURSING_FAMILY("Njega člana nakon operacije", 5),
    BLOOD_DONATION("Dobrovoljno davanje krvi", 1),

    // Privatni poslovi i imovina
    RELOCATION("Selidba", 3),
    HOUSE_CONSTRUCTION("Gradnja/adaptacija kuće", 7),
    NATURAL_DISASTER("Elementarna nepogoda", 3),
    PRIVATE_GOV_BUSINESS("Privatni posao kod organa", 2),

    // Usavršavanje i kultura
    SPORT_CULTURE("Kulturni/sportski susreti", 7),
    EXAM_PREP("Stručni ili drugi ispit", 5),
    THESIS_PREP("Magistarski/doktorski rad", 5),


    // Bolovanje
    SICK_LEAVE("Bolovanje", 0),

    // Neplaceno odsustvo
    UNPAID_LEAVE("Neplaćeno odsustvo", 0);

    companion object {
        val allOptions = entries.map { it.displayName }

        fun getMaxDaysFor(name: String): Int {
            return entries.find { it.displayName == name }?.maxDays ?: 0
        }
    }
}
@Composable
fun NewRequestScreen(navigateHome: () -> Unit, viewModel: InboxRequestViewModel = viewModel()){
    val uiState by viewModel.uiState.collectAsState()


    NewRequestContent(
        uiState = uiState,
        navigateHome = navigateHome,
        onTypeChange = { viewModel.onTypeChange(it) },
        onDatesSelected = { from, to -> viewModel.onDatesSelected(from, to) },
        onRemoveDate = { index -> viewModel.removeDateRange(index) },
        onExplanationChange = { viewModel.onExplanationChange(it) },
        sendRequest = { viewModel.sendRequest() },
        onFileSelected = {uri, name -> viewModel.onFileAttached(uri, name)},
        resetSuccessState = { viewModel.resetSuccessState() },
        onClearError = { viewModel.clearError() }
    )

}

@Composable
fun NewRequestContent(
    uiState: LeaveUiState,
    navigateHome: () -> Unit,
    onTypeChange: (String) -> Unit,
    onDatesSelected: (Long?, Long?) -> Unit,
    onRemoveDate : (Int) -> Unit,
    onFileSelected: (Uri?, String) -> Unit,
    onExplanationChange: (String) -> Unit,
    sendRequest: () -> Unit,
    resetSuccessState: () -> Unit,
    onClearError: () -> Unit
){
    val context = LocalContext.current

    LaunchedEffect(uiState.isError) {
        if(uiState.isError && !uiState.errorMsg.isNullOrEmpty()){
            Toast.makeText(context, uiState.errorMsg, Toast.LENGTH_LONG).show()
        }
    }

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
            RequestHeader("New Request", navigateHome)

        }}){ padding ->
        Column(modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)){

            RequestTypeSelector(
                selectedType = uiState.currentRequest.type,
                isExpanded = showTypeMenu,
                onExpandChange = {showTypeMenu = it},
                onTypeSelected = { onTypeChange(it)}
            )

            Spacer(modifier = Modifier.height(16.dp))
            val datesList = uiState.currentRequest.leave_dates?.filterNotNull() ?: emptyList()

            CombinedDateList(
                leaveDates = datesList,
                onRemove = { index -> onRemoveDate(index) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = {
                    onClearError()
                    showDatePicker = true
                          },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF004D61))
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF004D61))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Date Range", color = Color(0xFF004D61))
            }


            Spacer(modifier = Modifier.height(16.dp))
            Text("Details", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            ExplanationField(
                value = uiState.currentRequest.explanation,
                onValueChange = { onExplanationChange(it)}
            )

            Spacer(modifier = Modifier.height(24.dp))
            AttachmentSection(
                fileName = uiState.currentRequest.file_info?.file_name,
                onAddClick = {filePickerLauncher.launch("*/*")}
            )
            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    //.height(80.dp),
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = uiState.isError && !uiState.errorMsg.isNullOrEmpty(),
                    enter = fadeIn(animationSpec = tween(500)),
                    exit = fadeOut(animationSpec = tween(500)),
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF44336)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        //let da zarobimo poruku, ako postane null tokom animacije, da koristi zadnju poznatu vrijednost
                        uiState.errorMsg?.let { message ->
                            Text(
                                text = message,
                                color = Color.White,
                                modifier = Modifier.padding(16.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
            Button(
                onClick = {
                    sendRequest()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF004D61),
                    contentColor = Color.White
                ),
                enabled = !uiState.isLoading &&
                        !uiState.isError &&
                        (uiState.currentRequest.leave_dates?.isNotEmpty() == true),
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Send Request", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
            LaunchedEffect(uiState.isSuccess) {
                if(uiState.isSuccess){
                    navigateHome()
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
fun AttachmentSection(fileName: String?, onAddClick: ()-> Unit){
    Column{
        Text("Attachments", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically){
            AssistChip(onClick =  onAddClick,
                      label = {Text("Add a file")},
                      leadingIcon = {Icon(Icons.Default.Add, null)},
                      shape  = RoundedCornerShape(50.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = if(fileName.isNullOrEmpty()) "No file selected" else fileName, color = if (fileName.isNullOrEmpty()) Color.Gray else Color(0xFF004D61), fontSize = 12.sp)
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
            Text(textToShow, color = if(dateFrom == "Date") Color.Gray else Color.Black)
            Icon(Icons.Default.KeyboardArrowDown,null)
        }
    }
}

@Composable
fun ExplanationField(value: String, onValueChange: (String) -> Unit){
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {Text("Explanation", color = Color.Gray)},
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

            //treba mozda vise tipova ovdje
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
fun RequestHeader(label: String, navigateHome: () -> Unit){
    Surface(color = Color(0xFF004D61), modifier = Modifier.fillMaxWidth()){
        Row(modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically){
            Icon(Icons.Default.ArrowBack, contentDescription = null,
                tint = Color.White,
                modifier = Modifier.clickable{navigateHome()})
            Spacer(modifier = Modifier.width(24.dp))
            Text(label, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }

}

@Composable
fun CombinedDateList(
    leaveDates: List<LeaveDates>,
    onRemove: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Odabrani datumi ",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedCard(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            if (leaveDates.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Nisu izabrani datumi.", color = Color.LightGray)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(leaveDates) { index, dates ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Pocetak: ${formatTimestampToDate(dates.start)}",
                                    fontSize = 14.sp,
                                    color = Color(0xFF004D61),
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Kraj:   ${formatTimestampToDate(dates.end)}",
                                    fontSize = 14.sp,
                                    color = Color(0xFF004D61)
                                )
                            }

                            IconButton(onClick = { onRemove(index) }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove",
                                    tint = Color.Red
                                )
                            }
                        }

                        if (index < leaveDates.size - 1) {
                            HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NewRequestPreview(){
  NewRequestContent(uiState = LeaveUiState(),
      navigateHome = {},
      onTypeChange = {},
      onDatesSelected = {_,_ -> },
      onExplanationChange = {},
      sendRequest = {},
      resetSuccessState = {},
      onRemoveDate = {},
      onFileSelected = {_, _ -> },
      onClearError = {}
  )
}