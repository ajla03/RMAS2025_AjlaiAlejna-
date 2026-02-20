package com.example.projekatfaza23.UI.home
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.lazy.items
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.projekatfaza23.R
import com.example.projekatfaza23.data.auth.UserManager
import com.example.projekatfaza23.model.LeaveRequest
import com.example.projekatfaza23.model.RequestSatus
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.projekatfaza23.UI.dean.primaryColor
import com.example.projekatfaza23.UI.request.InboxRequestViewModel
import androidx.compose.material.icons.filled.ChildFriendly
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.projekatfaza23.UI.dean.exportLeaveRequestToPdf
import com.example.projekatfaza23.UI.dean.getMockRequests
import com.example.projekatfaza23.UI.request.RequestType
import com.example.projekatfaza23.UI.profile.UserMenu
import java.util.jar.Manifest

val SuccessColor = Color(0xFF2E7D32)
val WarningColor = Color(0xFFEF6C00)
val ErrorColor = Color(0xFFC62828)
val TextDark = Color(0xFF1A1C1E)
@Composable
fun ClientHomeScreen(viewModel: InboxRequestViewModel,
                     createNewRequest : () -> Unit,
                     navigateLogout: () -> Unit,
                     onSwitchRole: () -> Unit
                     ) {

    val uiState by viewModel.uiState.collectAsState()
    val currentFilter by viewModel.currentFilter.collectAsState()
    val requests = viewModel.getFilteredRequests()
    val user = UserManager.currentUser.collectAsState().value

    val role = remember(user?.email) {
        when (user?.email) {
            "ayla62553@gmail.com", "hodzic.alejna@gmail.com" -> "Dekan"
            "avonkoztuz@gmail.com", "hr.app.untz@gmail.com" -> "Sekretar"
            else -> null
        }
    }

    var isMenuOpen by remember { mutableStateOf(false) }
    val blurRadius by animateDpAsState(targetValue = if (isMenuOpen) 12.dp else 0.dp)


    // odobravanje notifikacija
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
        }
    )
    LaunchedEffect(Unit) {
        // ako je android verzija 13 ili noviji.... ( post notifikacija tek u androidu 13)
        // na ostalim automatski radi
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionCheck = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            )
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                launcher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {


        Box(modifier = Modifier.blur(blurRadius)) {
            ClientHomeScreenContent(
                remainingDays = uiState.remainingLeaveDays,
                requests = requests,
                isLoading = uiState.isLoading,
                currentFilter = currentFilter,
                userName = "${user?.name ?: ""} ${user?.lastName ?: ""}",
                userPhoto = user?.profilePictureURL,
                onFilterChange = { newFilter -> viewModel.setFilter(newFilter) },
                onCreateRequest = createNewRequest,
                isMenuOpen = isMenuOpen,
                onProfPicClick = {isMenuOpen = true},
                currStatus = uiState.status
            )
        }


        UserMenu(
            isOpen = isMenuOpen,
            userName = "${user?.name ?: ""} ${user?.lastName ?: ""}",
            userEmail = user?.email ?: "",
            userProfilePhoto = user?.profilePictureURL,
            currStatus = uiState.status,
            onStatusChange = {newStat -> viewModel.updateStatus(newStat)},
            onDismiss = {isMenuOpen = false},
            navigateLogout = navigateLogout,
            role = role,
            oSwitchRole = {
                isMenuOpen = false
                onSwitchRole()
            }
        )

    }

}


@Composable
fun ClientHomeScreenContent(
    remainingDays: Int,
    requests: List<LeaveRequest>,
    isLoading: Boolean,
    currentFilter: String,
    userName: String,
    userPhoto: Uri?,
    onFilterChange: (String) -> Unit,
    onCreateRequest: () -> Unit,
    isMenuOpen : Boolean,
    currStatus: Status,
    onProfPicClick : () -> Unit
){
    var selectedRequest by remember { mutableStateOf<LeaveRequest?>(null) }

    Scaffold(
        containerColor = Color(0xFFF5F7FA),
        topBar = {
            TopAppBarSection()
        },
        floatingActionButton = {
            if (!isMenuOpen) {
                FloatingActionButton(
                    onClick = { onCreateRequest() },
                    containerColor = primaryColor,
                    shape = CircleShape,
                    contentColor = Color.White,
                    elevation = FloatingActionButtonDefaults.elevation(6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
                }
            }
        },
        bottomBar = {
            BottomContactBar()
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            ProfileHeader(userName, userPhoto, remainingDays, onProfPicClick, currStatus)

            Spacer(modifier = Modifier.height(24.dp))

            RequestsHeaderAndFilter(currentFilter, onFilterChange)

            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = primaryColor)
                }
            } else if (requests.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EmptyStateMessage()
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(items = requests, key = { it.id }) { request ->
                        RequestItemCard(
                            request = request,
                            modifier = Modifier.clickable{
                                selectedRequest = request
                            }
                            )
                    }
                }
            }
        }
    }
    if (selectedRequest != null) {
        RequestPreview (
            request = selectedRequest!!,
            onDismiss = { selectedRequest = null },
        )
    }
}

@Composable
fun RequestsHeaderAndFilter(currentFilter: String, onFilterChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    val filtersMap = mapOf(
        "All" to "Sve",
        "Pending" to "Na čekanju",
        "Approved" to "Odobreno",
        "Denied" to "Odbijeno"
    )

    val displayFilter = filtersMap[currentFilter] ?: currentFilter

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Lista zahtjeva",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray
        )

        Box {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color.LightGray),
                modifier = Modifier.clickable { expanded = true }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(displayFilter, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                filtersMap.forEach { (key, value) ->
                    DropdownMenuItem(
                        text = { Text(value) },
                        onClick = {
                            onFilterChange(key)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
@Composable
fun TopAppBarSection(){
    Surface(
           color = Color.White,
           modifier = Modifier.fillMaxWidth().statusBarsPadding(),
           shadowElevation = 4.dp){
        Row(modifier = Modifier.fillMaxWidth()
            .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically){
            Surface(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(40.dp),
            ) {
                 Image(painter = painterResource(R.drawable.hrapp_logo),
                      contentDescription = "HR App logo",
                     modifier = Modifier.size(34.dp).border(1.dp, Color.LightGray,RoundedCornerShape(8.dp)).clip(RoundedCornerShape(4.dp)).scale(1.8f),
                     contentScale = ContentScale.Fit)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text("HR Aplikacija", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
        }

    }
}

@Composable
fun ProfileHeader(
    userName: String,
    userPhoto: Uri?,
    remainingDays: Int,
    onProfPicClick: () -> Unit,
    currStatus: Status
){
    val (textColor, bgColor) = when (currStatus) {
        Status.AtWork -> Pair(Color(0xFF2E7D32), Color(0xFFE3F1E5))
        Status.PaidLeave -> Pair(Color(0xFF1976D2), Color(0xFFCFD9E1))
        Status.AnnualLeave -> Pair(Color(0xFFEF6C00), Color(0xFFF1E7D9))
        Status.Away -> Pair(Color(0xFFD72525), Color(0xFFF1E5E6))
    }

Column (verticalArrangement = Arrangement.spacedBy(20.dp)){
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = userPhoto?.toString()?.replace("http://", "https://"),
            contentDescription = "Profilna slika",
            placeholder = painterResource(R.drawable.no_photo),
            error = painterResource(R.drawable.no_photo),
            modifier = Modifier.size(70.dp).clip(CircleShape)
                .border(1.dp, Color.LightGray, CircleShape)
                .clickable{onProfPicClick()}
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text( text = "Dobrodošli, ",
                   fontSize = 14.sp,
                color = Color.Gray)

            Text(
                userName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(2.dp))

            Surface(
                color = bgColor,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row (modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically){

                    Box(modifier = Modifier.size(6.dp).background(textColor, CircleShape))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        currStatus.statusString,
                        fontSize = 12.sp,
                        color = textColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Godišnji odmor", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
                Text("Preostali dani", style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
            }
            Text(
                text = "$remainingDays",
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold,
                color = primaryColor
            )
        }
    }
  }
}

@Composable
fun getIconForRequestType(typeString: String): ImageVector {
    return when (typeString) {
        "ANNUAL_LEAVE", "Godišnji odmor" -> Icons.Default.DateRange
        "MARRIAGE", "MARRIAGE_CHILD", "Stupanje u brak" -> Icons.Default.Favorite
        "BIRTH_WIFE", "Porođaj supruge" -> Icons.Default.ChildFriendly
        "DEATH_FAMILY", "ILLNESS_FAMILY", "NURSING_FAMILY" -> Icons.Default.LocalHospital
        "BLOOD_DONATION" -> Icons.Default.Bloodtype
        "RELOCATION", "HOUSE_CONSTRUCTION" -> Icons.Default.Home
        "NATURAL_DISASTER" -> Icons.Default.Warning
        "SPORT_CULTURE" -> Icons.Default.SportsSoccer
        "EXAM_PREP", "THESIS_PREP" -> Icons.Default.School
        "SICK_LEAVE", "Bolovanje" -> Icons.Default.Healing
        "UNPAID_LEAVE" -> Icons.Default.MoneyOff
        else -> Icons.Default.Description
    }
}

@Composable
fun RequestItemCard(request: LeaveRequest, modifier: Modifier) {

    val (statusColor, statusText) = when (request.status) {
        RequestSatus.Approved -> Pair(SuccessColor, "Odobreno")
        RequestSatus.Denied -> Pair(ErrorColor, "Odbijeno")
        else -> Pair(WarningColor, "Na čekanju")
    }

    val color = statusColor
    val label = statusText

    val requestTypeEnum = try {
        RequestType.valueOf(request.type)
    } catch (e: Exception) {
        RequestType.entries.find { it.displayName == request.type }
    }
    val displayTitle = requestTypeEnum?.displayName ?: request.type
    val typeIcon = getIconForRequestType(request.type)

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth().padding(bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(typeIcon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = displayTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )

                val dateText = if (request.leave_dates?.isNotEmpty() == true) {
                    "${formatTimestampToDate(request?.leave_dates?.first()?.start)} - ${formatTimestampToDate(request?.leave_dates?.first()?.end)}"
                } else "Datum nije definisan."

                Text(
                    text = dateText,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    color = color.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, color.copy(alpha = 0.2f))
                ) {
                    Text(
                        text = label,
                        color = color,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun EmptyStateMessage() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Schedule, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(60.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text("No requests found", color = Color.Gray)
    }
}


@Composable
fun BottomContactBar() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = primaryColor
    ) {
        Row(
            modifier = Modifier.padding(12.dp).navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text("Contact: 012 345 6789", fontSize = 10.sp, color = Color.White)
            Text("|", fontSize = 10.sp, color = Color.White)
            Text("Email: hr.app.untz@gmail.com", fontSize = 10.sp, color = Color.White)
        }
    }
}
@Preview(showBackground = true)
@Composable
fun ClientHomePreviewNew() {
    val mockRequests = getMockRequests()

    MaterialTheme {
        ClientHomeScreenContent(
            remainingDays = 16,
            requests = mockRequests,
            isLoading = false,
            currentFilter = "All",
            userName = "Alejna Hodzic",
            userPhoto = null,
            onFilterChange = {},
            onCreateRequest = {},
            isMenuOpen = false,
            currStatus = Status.AtWork,
            onProfPicClick = {}
        )
    }
}