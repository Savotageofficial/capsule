package com.example.capsule.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.capsule.R
import com.example.capsule.data.model.Doctor
import com.example.capsule.ui.theme.Gold
import com.example.capsule.ui.theme.Green
import com.example.capsule.util.ProfileImage

@Composable
fun DoctorResultCard(
    doctor: Doctor,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            // Circular doctor image
            ProfileImage(
                base64Image = doctor.profileImageBase64,
                defaultImageRes = R.drawable.doc_prof_unloaded,
                modifier = Modifier.size(86.dp),
                onImageClick = null
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                // -------- NAME --------
                Text(
                    text = if (doctor.name.startsWith("Dr. ")) doctor.name else "Dr. ${doctor.name}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(2.dp))

                // -------- SPECIALTY --------
                Text(
                    text = doctor.specialty,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )

                Spacer(Modifier.height(8.dp))

                // -------- ADDRESS (if available) --------
                if (doctor.clinicAddress.isNotEmpty()) {
                    Text(
                        text = doctor.clinicAddress,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                    Spacer(Modifier.height(8.dp))
                }

                // -------- RATING & PRICE --------
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    // Rating section
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.ic_star_filled),
                            tint = Gold,
                            contentDescription = "Rating",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = getRatingText(doctor.rating, doctor.reviewsCount),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Price section
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.ic_price),
                            tint = Green,
                            contentDescription = "Session price",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = getPriceText(doctor.sessionPrice),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}


private fun getRatingText(rating: Double, reviewsCount: Int): String {
    return when {
        rating > 0 && reviewsCount > 0 -> String.format("%.1f", rating)
        rating > 0 -> String.format("%.1f", rating)
        else -> "New"
    }
}


private fun getPriceText(price: Double): String {
    return when {
        price > 0 -> "EGP ${price.toInt()}"
        price == 0.0 -> "Free"
        else -> "Contact"
    }
}