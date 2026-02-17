package com.example.projekatfaza23.UI.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.projekatfaza23.UI.dean.primaryColor
import com.example.projekatfaza23.UI.secretary.StatusBadge
import com.example.projekatfaza23.UI.secretary.formatDatesForDisplay
import com.example.projekatfaza23.model.LeaveRequest
import com.example.projekatfaza23.model.RequestSatus

@Composable
fun RequestPreview(
    request: LeaveRequest,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color.LightGray),
            //TODO elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp)
            ){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        text = request.type,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Black
                    )

                    StatusBadge(status =
                        if (request.status == RequestSatus.Pending || request.status == RequestSatus.PendingDean)
                            RequestSatus.Pending
                        else
                            request.status
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                val createdAt = request.createdAt?.let { formatTimestampToDate(it) }
                Text(
                    text = "Poslano: $createdAt",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray)

                Text(
                    text = "Predloženi datumi:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold)

                Spacer(modifier = Modifier.height(8.dp))

                if(request.status == RequestSatus.Approved) {
                    val approvedDate = request.leave_dates?.firstOrNull()
                    if (approvedDate != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFF4CAF50), RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Odobreno",
                                tint = Color(0xFF4CAF50)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = formatDatesForDisplay(approvedDate.start, approvedDate.end),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                    } else {
                        //TODO
                        request.leave_dates?.forEach { dateRange ->
                            if (dateRange != null) {
                                Text(
                                    text = "• " + formatDatesForDisplay(
                                        dateRange.start,
                                        dateRange.end
                                    ),
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    if (request.explanation.isNotEmpty()) {
                        Text(
                            text = "Pojašnjenje razloga odsustva:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = request.explanation,
                            fontSize = 14.sp,
                            color = Color.DarkGray
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    if (request.status != RequestSatus.Pending) {
                        val responseExplanation =
                            if (request.explanationDean.isNotEmpty()) request.explanationDean
                            else request.explanationSecretary

                        if (responseExplanation.isNotEmpty()) {
                            Text(
                                text = "Komentar nadležnog:",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFD32F2F)
                            )
                            Text(
                                text = responseExplanation,
                                fontSize = 14.sp,
                                color = Color.DarkGray
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray)

                    if (request.file_info != null && request?.file_info?.file_name?.isNotEmpty() ?: false) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.AttachFile,
                                contentDescription = "Attachment",
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = request?.file_info?.file_name ?: "",
                                fontSize = 14.sp,
                                color = Color.Blue
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    if (request.status == RequestSatus.Approved) {
                        Button(
                            onClick = {
                                //TODO: Implement PDF download
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                Icons.Default.PictureAsPdf,
                                contentDescription = "PDF",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Preuzmi zahtjev (PDF)",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
