package com.example.capsule.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.capsule.ui.theme.White
import com.example.capsule.R

@Composable
fun UpcomingCard(
    name: String,
    details: String,
    showMoreIcon: Boolean = false, // show/hide icon
    onClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {} // callback for delete
) {
    var menuExpanded by remember { mutableStateOf(false) } // dropdown menu state

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {

            }
            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(details, color = Color.Gray, fontSize = 16.sp)
            }

            if (showMoreIcon) {
                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_more),
                            contentDescription = "More"
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
