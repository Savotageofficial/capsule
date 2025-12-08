package com.example.capsule.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.capsule.R
import com.example.capsule.data.model.TimeSlot
import com.example.capsule.util.ProfileImage
import com.example.capsule.util.formatDate

@Composable
fun UpcomingCard(
    name: String,
    appointmentType: String,
    timeSlot: TimeSlot,
    date: Long,
    profileImage: String? = null, // Add this parameter
    showMoreIcon: Boolean = false,
    onClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            // ----------- Profile + Texts ------------
            Row(verticalAlignment = Alignment.CenterVertically) {

                // Profile Picture - NOW USING profileImage parameter
                ProfileImage(
                    base64Image = profileImage,
                    defaultImageRes = R.drawable.patient_profile,
                    modifier = Modifier.size(52.dp),
                    onImageClick = null
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    // Patient Name
                    Text(
                        name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1C1C1C)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Appointment Type
                    Text(
                        text = appointmentType,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF333333)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Time Slot
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = Color(0xFF7D7D7D),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = timeSlot.toDisplayString(), // Use TimeSlot's display method
                            fontSize = 14.sp,
                            color = Color(0xFF6D6D6D)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Date
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = Color(0xFF7D7D7D),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = formatDate(date),
                            fontSize = 14.sp,
                            color = Color(0xFF7D7D7D)
                        )
                    }
                }
            }

            // ----------- Menu Button (Three dots) ------------
            if (showMoreIcon) {
                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_more),
                            contentDescription = "",
                            tint = Color(0xFF505050)
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = {
                                menuExpanded = false
                                onDeleteClick()
                            }
                        )
                    }
                }
            }
        }
    }
}