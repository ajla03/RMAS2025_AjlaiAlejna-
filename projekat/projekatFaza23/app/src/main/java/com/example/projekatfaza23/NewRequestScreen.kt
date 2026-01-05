package com.example.projekatfaza23

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun NewRequestScreen(onBack: () -> Unit){
    var requestType by remember { mutableStateOf("TypeOfRequest") }
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
        Column(modifier = Modifier.padding(padding)
            .fillMaxSize()
            .padding(20.dp)){

            RequestTypeSelector(
                selectedType = requestType,
                isExpanded = showTypeMenu,
                onExpandChange = {showTypeMenu = it},
                onTypeSelected = {requestType = it}
            )

        }
    }
}

@Composable
fun RequestTypeSelector(
    selectedType: String,
    isExpanded : Boolean,
    onExpandChange: (Boolean) -> Unit,
    onTypeSelected: (String) -> Unit
){
    Box{
        OutlinedCard (onClick = {onExpandChange(true)},
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(60.dp)){
            Row(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically){

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
            Spacer(modifier = Modifier.width(26.dp))
            Text("New Request", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }

}
@Composable
fun TopAppBarSection(onBack: () -> Unit){
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

@Preview(showBackground = true)
@Composable
fun NewRequestPreview(){
    NewRequestScreen({})
}