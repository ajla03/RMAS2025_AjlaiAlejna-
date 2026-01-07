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
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.projekatfaza23.data.LeaveRequest
import com.example.projekatfaza23.data.RequestSatus


@Composable
fun ClientHomeScreen() {
    val homeViewModel : InboxRequestViewModel = viewModel()
    val uiState by homeViewModel.uiState.collectAsState()
    Scaffold(
        topBar = {
            TopAppBarSection()
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Akcija */ },
                containerColor = Color.LightGray,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
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
            ProfileHeader()
            Spacer(modifier = Modifier.height(24.dp))
            RemainingLeaveSection(uiState.remainingLeaveDays)
            Spacer(modifier = Modifier.height(24.dp))
            RequestsCard(viewModel = homeViewModel)
        }
    }
}

@Composable
fun TopAppBarSection(){
    Surface(color = Color(0xFFE0E0E0)){
        Row(modifier = Modifier.fillMaxWidth()
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
            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(10.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text("Name LastName", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Row {
                Text("Status: ", fontSize = 14.sp)
                Text("Work from home", fontSize = 14.sp, color = Color.Red, fontWeight = FontWeight.Bold)
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
        modifier = Modifier.fillMaxWidth().fillMaxHeight(0.85f),
        color = Color(0xFFE0E0E0),
        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp, bottomStart = 40.dp, bottomEnd = 40.dp)
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
                        shape = RoundedCornerShape(16.dp),
                        colors = AssistChipDefaults.assistChipColors(containerColor = Color.LightGray)
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
                items(displayList){ request ->
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
            Text(text = "${request.type} | ${request.dateFrom} - ${request.dateTo}", fontSize = 12.sp, color = Color.Gray)
        }
        if(request.status.equals("Pending")){
            Surface(
                modifier = Modifier.padding(start = 20.dp),
                color = Color.White.copy(alpha = 0.6f),
                shape = RoundedCornerShape(12.dp)
            ){
                TextButton(onClick = {},
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    modifier = Modifier.height(30.dp)
                ) { 
                    Text("Cancel", color = Color.Gray, fontSize = 12.sp)
                }
            }

        }
    }
}

@Composable
fun BottomContactBar() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFEEEEEE)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text("Contact: 012 345 6789", fontSize = 10.sp)
            Text("|", fontSize = 10.sp)
            Text("Email: hr@domain.com", fontSize = 10.sp)
        }
    }
}
@Preview(showBackground = true)
@Composable
fun clientHomePreview(){
    ClientHomeScreen()
}