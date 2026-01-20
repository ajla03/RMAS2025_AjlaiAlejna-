package com.example.projekatfaza23.UI.dean

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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.projekatfaza23.UI.home.TopAppBarSection


data class Employee(
    val id: Int,
    val name: String,
    val title: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeanDirectoryScreen(onBack: () -> Unit){
// Dummy podaci za prikaz
    val employees = List(10) {
        Employee(it, "Name LastName", "Software Engineer")
    }

    Scaffold (
        topBar =  {TopAppBarSection()},
        containerColor = Color(0xFFF5F7FA),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: PDF Action */ },
                containerColor = Color(0xFF2D3E50), // Tamnoplava (kao na slici)
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
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically){

                BackIcon(onBack)
                Spacer(modifier = Modifier.width(16.dp))


                Text(
                    text = "Direktorij",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black

                )

            }
            FilterButton({})

            //lista
            Surface(
                modifier = Modifier.fillMaxSize().padding(top = 16.dp),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                color = Color.White,
                shadowElevation = 4.dp
            ){
                LazyColumn(contentPadding = PaddingValues(16.dp)){
                    itemsIndexed(employees) { index, employee ->
                        EmployeeItem(employee)

                        if (index < employees.lastIndex) {
                            HorizontalDivider(
                                color = Color.LightGray.copy(alpha = 0.3f)
                            )
                        }
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
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
            .padding(vertical = 4.dp),
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
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = Color(0xFFEEF2F6),
                contentColor = Color.Black
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color.Gray
                    )
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





@Preview
@Composable
fun PreviewDirectory() {
        DeanDirectoryScreen({})
}