package com.example.projekatfaza23.UI.profile

import android.net.Uri
import android.view.Surface
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.projekatfaza23.R
import com.example.projekatfaza23.UI.dean.primaryColor
import com.example.projekatfaza23.UI.home.Status
import com.example.projekatfaza23.UI.Role

val cyan = Color(0xFF00E5FF)
val purple = Color(0xFFD500F9)
val pink = Color(0xFFFF355C)
val red = Color(0xFFFF3F34)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserMenu(
    isOpen: Boolean,
    userName: String,
    userEmail: String,
    userProfilePhoto: Uri?,
    currStatus: Status,
    onStatusChange: (Status) -> Unit,
    onDismiss: () -> Unit,
    navigateLogout: () -> Unit,
    role: String? = null,
    oSwitchRole: () -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val borerBrush = Brush.sweepGradient(listOf(cyan, purple, cyan))
    val statusBrush = Brush.horizontalGradient(listOf(cyan, pink))
    val logoutBrush = Brush.horizontalGradient(listOf(pink, red))

    if(isOpen){
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = primaryColor,
            shape = RoundedCornerShape(
                topStart = 0.dp,
                topEnd = 0.dp,
                bottomEnd = 30.dp,
                bottomStart = 24.dp
            ),
            modifier = Modifier.fillMaxHeight(0.75f)
        ) {
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding( horizontal = 24.dp, vertical = 12.dp)
                    .statusBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                        .background(borerBrush)
                        .padding(3.dp)
                        .clip(CircleShape)
                        .background(primaryColor)
                ) {
                    AsyncImage(
                        model = userProfilePhoto?.toString(),
                        contentDescription = "Profile picture",
                        placeholder = painterResource(R.drawable.no_photo),
                        error = painterResource(R.drawable.no_photo),
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = userName,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = userEmail,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Thin,
                    color = Color.LightGray.copy(alpha = 0.7f)
                )

                if (role != null && role != Role.Professor.name) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Surface(
                        color = Color.White.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                        modifier = Modifier.height(40.dp).fillMaxWidth(0.8f) // Malo uže od punog ekrana
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = cyan,
                                shape = RoundedCornerShape(50),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = if (role == "Dekan") "Profesor" else "Zaposlenik",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { oSwitchRole() }
                                    .fillMaxHeight()
                            ) {
                                Text(
                                    text = role,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Postavi status:",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Status.entries.forEach { status ->
                        val isSelected = currStatus == status

                        val bg =
                            if (isSelected) primaryColor.copy(alpha = 0.5f)
                            else primaryColor.copy(alpha = 0.3f)
                        val textColor = Color.White
                        val border =
                            if (isSelected) BorderStroke(2.dp, statusBrush)
                            else BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))

                        Surface(
                            shape = RoundedCornerShape(40.dp),
                            color = if (isSelected) Color.White else Color.Transparent,
                            border = BorderStroke(1.dp, Color.White),
                            modifier = Modifier
                                .height(40.dp)
                                .clickable { onStatusChange(status) }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp)
                            ) {
                                Icon(
                                    imageVector = getStatusIcon(status),
                                    contentDescription = null,
                                    tint = if (!isSelected) textColor else primaryColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = status.statusString,
                                    color = if (!isSelected) textColor else primaryColor,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        navigateLogout()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryColor.copy(alpha = 0.5f),
                        contentColor = pink //Color(0xFFC62828)
                    ),
                    border = BorderStroke(2.dp, logoutBrush),
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(50.dp)
                ) {
                    Text(
                        text = "Log Out",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun getStatusIcon(status: Status) : ImageVector {
    return when (status) {
        Status.AtWork -> Icons.Default.BusinessCenter
        Status.PaidLeave -> Icons.Default.Payments
        Status.AnnualLeave -> Icons.Default.BeachAccess
        Status.Away -> Icons.Default.EventBusy
    }
}