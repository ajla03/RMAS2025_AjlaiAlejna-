package com.example.projekatfaza23.UI.dean

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.TextButton
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.projekatfaza23.UI.secretary.DashboardHeader
import com.example.projekatfaza23.data.auth.UserManager
import java.util.Locale


val filterMap = mapOf(
    "Svi" to "All",
    "Odobreni" to "Approved",
    "Na čekanju" to "PendingDean",
    "Odbijeni" to "Denied"
)

//main screen
@Composable
fun DeanHomeScreen(
    viewModel: DeanViewModel,
    navigateRequest: () -> Unit,
    onLogoutClick: () -> Unit,
    onNavigateToHistory: () -> Unit = {},
    onNavigateToDirectory : () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val user = UserManager.currentUser.collectAsState().value

    val pendingRequests = uiState.requests.filter { it.status == RequestSatus.PendingDean }
    DeanHomeScreenContent(
        isLoading = uiState.isLoading,
        displayRequests = pendingRequests,
        onTodayLeaveCount = uiState.onTodayLeaveCount,
        userName = user?.name ?: "",
        userLastName = user?.lastName ?: "",
        userEmail = user?.email ?: "",
        userImageUrl = user?.profilePictureURL?.toString(),
        navigateRequest = navigateRequest,
        onLogoutClick = onLogoutClick,
        onSetSelectedRequest = { viewModel.setSelectedRequest(it) },
        onNavigateToHistory = onNavigateToHistory,
        onNavigateToDirectory = onNavigateToDirectory
    )
}


@Composable
fun DeanHomeScreenContent(
    isLoading: Boolean,
    displayRequests: List<LeaveRequest>,
    onTodayLeaveCount: Int = 0,
    userName: String,
    userLastName: String,
    userEmail: String,
    userImageUrl: String?,
    navigateRequest: () -> Unit,
    onLogoutClick: () -> Unit,
    onSetSelectedRequest: (LeaveRequest) -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToDirectory: () -> Unit = {}
) {
/*
     mozda cemo trebati ako se podaci budu fetchali sa interneta kada kliknemo na request
    LaunchedEffect(Unit) {
        delay(400)
        viewModel.resetSelectedRequest()
    }
*/

    var showProfileDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar =  {
            TopAppBarSection()},
        containerColor = Color(0xFFF5F7FA),
        bottomBar = {
            DeanBottomNavigationBar(
                currentRoute = "home",
                onNavigateToHome = {},
                onNavigateToHistory = onNavigateToHistory,
                onNavigateToDirectory = onNavigateToDirectory
            )
        }
    ){
        paddingValues ->

        Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
        ) {

            DashboardHeader(
                role = "Dekan",
                pendingCount = displayRequests.size,
                onLeaveCount = onTodayLeaveCount,
                userImageUrl = userImageUrl,
                userEmail = userEmail,
                onProfileClick = {showProfileDialog = true}
            )

            Text(
                text = "Zahtjevi koji čekaju obradu",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
            )


            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp)
                ) {
                    if (displayRequests.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                                Text("Nema novih zahtjeva na čekanju.", color = Color.Gray)
                            }
                        }
                    } else {
                        items(displayRequests) { request ->
                            RequestCardDean(request, navigateRequest, { onSetSelectedRequest(it) })
                        }
                    }
                }
            }
        }

        if (showProfileDialog) {
            ProfileDialog(
                deanName = "$userName $userLastName",
                deanEmail = userEmail,
                onDismiss = { showProfileDialog = false },
                imageUrl = userImageUrl,
                onLogout = {
                    showProfileDialog = false
                    onLogoutClick()
                },
                role = "Dekan"
            )
        }
    }
}



fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
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
        DeanHomeScreenContent(
            isLoading = false,
            displayRequests = getMockRequests(),

            userName = "Ahmet",
            userLastName = "Dekanović",
            userEmail = "adekanovic@fit.ba",
            userImageUrl = null,
            navigateRequest = {},
            onLogoutClick = {},
            onSetSelectedRequest = {},
            onNavigateToHistory = {}
        )
}

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