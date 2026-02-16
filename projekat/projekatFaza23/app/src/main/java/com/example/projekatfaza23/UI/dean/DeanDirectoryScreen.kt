package com.example.projekatfaza23.UI.dean

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.projekatfaza23.UI.home.TopAppBarSection
import com.example.projekatfaza23.data.db.UserEntity
import kotlin.random.Random
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import com.example.projekatfaza23.UI.home.Status
import kotlinx.coroutines.launch
import java.lang.StrictMath.abs

val avatarColors = listOf(
    Color(0xFF2196F3),
    Color(0xFF4CAF50),
    Color(0xFF9C27B0),
    Color(0xFFFF9800),
    Color(0xFFE91E63),
    Color(0xFF009688),
    Color(0xFF3F51B5)
)

fun getAvatarColor(email: String): Color {
    val index = abs(email.hashCode()) %  avatarColors.size
    return avatarColors[index]
}


@Composable
fun DeanDirectoryScreen(
    viewModel: DeanViewModel,
    navigateHome: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val pdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        if (uri != null) {
            scope.launch {
                exportEmployeesToPdf(context, uri, uiState.displayedEmployees)
            }
        }
    }

    val onBackAction = {
        viewModel.resetEmployeeSearchQuery()
        navigateHome()
    }

    BackHandler {
        onBackAction()
    }

    DeanDirectoryContent(
        employees = uiState.displayedEmployees,
        searchQuery = uiState.employeeSearchQuery,
        onSearchChange = { viewModel.updateEmployeeSearch(it) },
        onExportPdfClick = { pdfLauncher.launch("Zaposleni_Lista.pdf") },
        onBackClick = onBackAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeanDirectoryContent(
    employees: List<UserEntity>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onExportPdfClick: () -> Unit,
    onBackClick: () -> Unit
) {

    var selectedEmployee by remember { mutableStateOf<UserEntity?>(null) }
    val focusManager = LocalFocusManager.current
    val sheetState = rememberModalBottomSheetState()


    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            },
        topBar = { TopAppBarSection() },
        containerColor = Color(0xFFF5F7FA),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onExportPdfClick() },
                containerColor = Color(0xFF2D3E50),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.PictureAsPdf, contentDescription = "Export PDF")
            }
        },
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BackIcon({ onBackClick() })
                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Direktorij",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF004D61)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextField(
                    value = searchQuery,
                    onValueChange = { newValue -> onSearchChange(newValue) },
                    placeholder = { Text("Pretraži zaposlene...", color = Color.Gray) },
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
            }

            if (employees.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Nema rezultata.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(employees) { index, employee ->
                        EmployeeItem(employee, {
                            selectedEmployee = employee
                            focusManager.clearFocus()
                        })
                    }
                }
            }
        }

        if (selectedEmployee != null) {
            ModalBottomSheet(
                onDismissRequest = { selectedEmployee = null },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                EmployeeDetailSheet(selectedEmployee!!)
            }
        }
    }
}


@Composable
fun BackIcon(onBack: () -> Unit){
    IconButton(
        onClick = { onBack() }
    ) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = null)
    }
}
@Composable
fun EmployeeItem(employee: UserEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 4.dp).padding(horizontal = 4.dp)
            .clickable{ onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val initials = remember(employee.firstName, employee.lastName){
                val first = employee.firstName.firstOrNull()?.toString() ?: ""
                val second = employee.lastName.firstOrNull()?.toString() ?: ""
                (first + second).toUpperCase()
            }
            if (!employee.imageUrl.isNullOrBlank()) {
                SubcomposeAsyncImage(
                    model = employee.imageUrl,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                ) {
                    val state = painter.state

                    if (state is AsyncImagePainter.State.Error) {
                        InitialsAvatar(employee = employee, initials = initials)
                    } else {
                        SubcomposeAsyncImageContent()
                    }
                }
            } else {
                InitialsAvatar(
                    employee = employee,
                    initials = initials,
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ){
                Text(
                    text = employee.firstName + " " + employee.lastName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = employee.role,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
          }
        }
    }

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray,
            modifier = Modifier.width(90.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black,
            modifier = Modifier.weight(1f),
            overflow = TextOverflow.Ellipsis,
        )
    }
}
@Composable
fun InitialsAvatar(employee: UserEntity, initials: String) {
    Surface(
        modifier = Modifier.size(50.dp),
        shape = CircleShape,
        color = getAvatarColor(employee.email).copy(alpha = 0.8f),
        contentColor = Color.Black
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = initials,
                color = Color.White,
                style = MaterialTheme.typography.labelLarge,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun InitialsAvatarLarge(initials: String, color: Color) {
    Surface(
        modifier = Modifier.size(100.dp),
        shape = CircleShape,
        color = color.copy(0.8f),
        contentColor = Color.White
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = initials,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun VacationStatusCard(used: Int, total: Int) {
    val progress = if (total > 0) used.toFloat() / total.toFloat() else 0f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F7FA), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Godišnji odmor",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$used / $total dana",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2196F3)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = if (progress > 0.8f) Color(0xFFFF9800) else Color(0xFF2196F3),
            trackColor = Color(0xFFE0E0E0),
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Preostalo: ${total - used} dana",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

// bio filter kod search bara , ali ne znam po cemu bi se ovdje filtriralo
@Composable
fun FilterButtonCircle(onClick:() -> Unit){
    Surface(
        onClick = onClick,
        color = Color(0xFF1E2A47),
        shape  = CircleShape,
        shadowElevation = 2.dp,
        modifier = Modifier.size(50.dp)) {

        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = "filter",
                tint = Color.White,
            )
        }

    }
}

@Composable
fun EmployeeDetailSheet(employee: UserEntity) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val (textColor, bgColor) = when (employee.userStatus) {
            Status.AtWork.name-> Pair(Color(0xFF2E7D32), Color(0xFFE3F1E5))
            Status.PaidLeave.name -> Pair(Color(0xFF1976D2), Color(0xFFCFD9E1))
            Status.AnnualLeave.name -> Pair(Color(0xFFEF6C00), Color(0xFFF1E7D9))
            Status.Away.name -> Pair(Color(0xFFD72525), Color(0xFFF1E5E6))
            else -> Pair(Color.Gray, Color(0xFFEEEEEE))
        }

        val currentStatusEnum = try {
            Status.valueOf(employee.userStatus)
        } catch (_: IllegalArgumentException) {
            Status.AtWork
        }

        val initials = (employee.firstName.take(1) + employee.lastName.take(1)).uppercase()

        if (!employee.imageUrl.isNullOrBlank()) {
            SubcomposeAsyncImage(
                model = employee.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            ) {
                if (painter.state is AsyncImagePainter.State.Error) {
                    InitialsAvatarLarge(initials, getAvatarColor(employee.email))
                } else {
                    SubcomposeAsyncImageContent()
                }
            }
        } else {
            InitialsAvatarLarge(initials, getAvatarColor(employee.email))
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = Color.LightGray.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "${employee.firstName} ${employee.lastName}",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            color = bgColor,
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(6.dp).background(textColor, CircleShape))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = currentStatusEnum.statusString,
                    style = MaterialTheme.typography.bodyLarge,
                    color = textColor,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            DetailRow(label = "Titula:", value = employee.role)
            DetailRow(label = "Email:", value = employee.email)
            //DetailRow(label = "Telefon:", value = "+387 61 123 456")

            Spacer(modifier = Modifier.height(24.dp))

            VacationStatusCard(used = employee.usedDays, total = employee.totalDays)
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewDirectoryContent() {
    val mockEmployees = listOf(
        UserEntity(
            firstName = "Alejna",
            lastName = "Hodžić",
            email = "alejna@example.com",
            role = "Software Engineer",
            userStatus = "AtWork",
            usedDays = 5,
            totalDays = 25,
            imageUrl = null
        ),
        UserEntity(
            firstName = "Amar",
            lastName = "Bulić",
            email = "amar@example.com",
            role = "Dizajner",
            userStatus = "AnnualLeave",
            usedDays = 15,
            totalDays = 20,
            imageUrl = null
        )
    )
     DeanDirectoryContent(
            employees = mockEmployees,
            searchQuery = "",
            onSearchChange = {},
            onExportPdfClick = {},
            onBackClick = {}
        )
}