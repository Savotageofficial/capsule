package com.example.capsule.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FilterChipsRow(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    val filters = listOf("Upcoming", "Completed", "Cancelled")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { filter ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = {
                    Text(
                        text = filter,
                        fontSize = 14.sp,
                        fontWeight = if (selectedFilter == filter) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = when (filter) {
                        "Upcoming" -> Color(0x3328B463)
                        "Completed" -> Color(0x334195F4)
                        "Cancelled" -> Color(0x33E53935)
                        else -> Color(0x33CCCCCC)
                    },
                    containerColor = Color.White,
                    selectedLabelColor = when (filter) {
                        "Upcoming" -> Color(0xFF2E7D32)
                        "Completed" -> Color(0xFF1E88E5)
                        "Cancelled" -> Color(0xFFD32F2F)
                        else -> Color.Gray
                    },
                    labelColor = Color.Gray
                )
            )
        }
    }
}