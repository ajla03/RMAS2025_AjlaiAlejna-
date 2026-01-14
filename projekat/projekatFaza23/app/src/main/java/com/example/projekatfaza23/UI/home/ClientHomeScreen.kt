package com.example.projekatfaza23.UI.home
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.projekatfaza23.R
import com.example.projekatfaza23.UI.request.formatMillisToDate
import com.example.projekatfaza23.data.auth.UserManager
import com.example.projekatfaza23.model.LeaveRequest
import com.example.projekatfaza23.model.RequestSatus
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage


@Composable
fun ClientHomeScreen(viewModel: InboxRequestViewModel, createNewRequest : () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    Scaffold(
        topBar = {
            TopAppBarSection()
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {createNewRequest()},
                containerColor = Color(0xFF116379),
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            ProfileHeader()
            Spacer(modifier = Modifier.height(24.dp))
            RemainingLeaveSection(uiState.remainingLeaveDays)
            Spacer(modifier = Modifier.height(24.dp))
            RequestsCard(viewModel = viewModel)
        }
    }
}

@Composable
fun TopAppBarSection(){
    Surface(
           color = Color(0xFF116379),
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
                     modifier = Modifier.size(28.dp).border(1.dp, Color.LightGray,RoundedCornerShape(8.dp)).clip(RoundedCornerShape(4.dp)).scale(1.8f),
                     contentScale = ContentScale.Fit)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text("HR App", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
        }

    }
}

@Composable
fun ProfileHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(70.dp),
            shape = CircleShape,
            color = Color.LightGray
        ) {

            AsyncImage(
                model = UserManager.currentUser.collectAsState().value?.profilePictureURL?.toString()?.replace("http://", "https://"),
                contentDescription = "Profilna slika",
                placeholder = painterResource(R.drawable.hrapp_logo),
                error = painterResource(R.drawable.hrapp_logo),
                modifier = Modifier.clip(CircleShape)
            )
            //Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(10.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text("${UserManager.currentUser.collectAsState().value?.name} ${UserManager.currentUser.collectAsState().value?.lastName}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold)
            Row {
                Text("Status: ", fontSize = 14.sp)
                Text("At Work", fontSize = 14.sp, color = Color(80,150,100), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun RemainingLeaveSection(days : Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Remaining annual leave : ", fontSize = 18.sp)
        Text("$days days", fontSize = 22.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun RequestsCard(viewModel : InboxRequestViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val currentFilter by viewModel.currentFilter.collectAsState()

    var expanded by remember { mutableStateOf(false) }

    val filters = listOf("All", "Pending", "Approved", "Denied")

    Surface(
        modifier = Modifier.fillMaxWidth().fillMaxHeight(),
        color = Color(0x6FE0E0E0),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row (
                modifier =  Modifier.fillMaxWidth(), 
                horizontalArrangement =  Arrangement.SpaceBetween,
                verticalAlignment =  Alignment.CenterVertically
            ) {
                Text(
                    "Requests",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )
                Box {
                    AssistChip(
                        onClick = { expanded = true },
                        label = { Text(currentFilter) },
                        trailingIcon = {
                            Icon(
                                Icons.Default.KeyboardArrowDown,
                                contentDescription = null
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFFE0E0E0))
                    )

                    DropdownMenu(expanded = expanded,
                                 onDismissRequest = {expanded = false}) {
                        filters.forEach { filterName ->
                            DropdownMenuItem( text =  {Text(filterName)},
                                              onClick = {viewModel.setFilter(filterName)
                                                        expanded = false})
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            val displayList = viewModel.getFilteredRequests()
            // Lista zahtjeva
            LazyColumn {
                items(
                    items = displayList,
                    key = {it.id}){ request ->
                    RequestItem(request = request)
                    Divider(color = Color.LightGray)
                }
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        }
    }
}

@Composable
fun RequestItem(request : LeaveRequest) {
    val statusColor = when (request.status) {
        RequestSatus.Approved  -> Color.Green
        RequestSatus.Denied -> Color.Red
        else -> Color.Gray
    }
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(15.dp).background(statusColor, CircleShape))
        Spacer(modifier = Modifier.width(15.dp))
        Column {
            Text(text = request.status.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = "${request.type} | ${formatMillisToDate(request.dateFrom)} - ${formatMillisToDate(request.dateTo)}", fontSize = 12.sp, color = Color.Gray)
        }
        //TODO ovakav cancel je ruzan, dodaj ga u karticu kad se otvori zahtjev
//        if(request.status.name.equals("Pending")){
//            Surface(
//                modifier = Modifier.padding(start = 20.dp),
//                color = Color.White.copy(alpha = 0.6f),
//                shape = RoundedCornerShape(12.dp)
//            ){
//                TextButton(onClick = {},
//                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
//                    modifier = Modifier.height(30.dp)
//                ) {
//                    Text("Cancel", color = Color.Gray, fontSize = 12.sp)
//                }
//            }
//
//        }
    }
}

@Composable
fun BottomContactBar() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFE3E1E1)
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
fun clientHomePreview(){
    ClientHomeScreen(viewModel(), {})
}