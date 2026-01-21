package com.example.projekatfaza23.UI.dean

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.projekatfaza23.UI.home.TopAppBarSection
import kotlin.random.Random

val avatarColors = listOf(
    Color(0xFF2196F3),
    Color(0xFF4CAF50),
    Color(0xFF9C27B0),
    Color(0xFFFF9800),
    Color(0xFFE91E63),
    Color(0xFF009688),
    Color(0xFF3F51B5)
)
data class Employee(
    val id: Int,
    val name: String,
    val title: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeanDirectoryScreen(navigateHome: () -> Unit){
// Dummy podaci za prikaz
    val employees = List(10) {
        Employee(it, "Name LastName", "Software Engineer")
    }

    Scaffold (
        topBar =  {TopAppBarSection()},
        containerColor = Color(0xFFF5F7FA),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO */ },
                containerColor = Color(0xFF2D3E50),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.PictureAsPdf, contentDescription = "Export PDF")
            }
        }
     ){ paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ){
            Spacer(modifier = Modifier.height(14.dp))

            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically){

                BackIcon(navigateHome)
                Spacer(modifier = Modifier.width(12.dp))


                Text(
                    text = "Direktorij",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black

                )

            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = "",
                    onValueChange = {},
                    placeholder = {Text("Pretraži zaposlene...", color = Color.Gray)},
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)},
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(50),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(12.dp))

                FilterButtonCircle({})
            }
            //lista
            Surface(
                modifier = Modifier.fillMaxSize().padding(top = 14.dp),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                color = Color.White,
                shadowElevation = 4.dp
            ){
                LazyColumn(
                    contentPadding = PaddingValues(top = 10.dp, start = 10.dp, end = 10.dp, bottom = 16.dp)                    ){
                    itemsIndexed(employees) { index, employee ->
                        EmployeeItem(employee)


                    }
                }

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
fun EmployeeItem(employee: Employee) {
    Card(
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 4.dp).padding(horizontal = 4.dp),
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
            Surface(
                modifier = Modifier.size(50.dp),
                shape = CircleShape,
                color = avatarColors[Random(employee.id).nextInt(avatarColors.size)].copy(0.8f),
                contentColor = Color.Black
            ) {
                Box(contentAlignment = Alignment.Center) {
                   Text("NL", color = Color.White, style = MaterialTheme.typography.labelLarge, fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ){
                Text(
                    text = employee.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = employee.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}



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

@Preview
@Composable
fun PreviewDirectory() {
        DeanDirectoryScreen({})
}