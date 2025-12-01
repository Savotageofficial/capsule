package com.example.capsule.ui.screens.patient

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.capsule.activities.ChatActivity
import com.example.capsule.R
import com.example.capsule.data.model.OfferItem
import com.example.capsule.data.model.Tip
import com.example.capsule.ui.components.DashboardCard
import com.example.capsule.ui.theme.Blue
import com.example.capsule.ui.theme.CapsuleTheme
import com.example.capsule.ui.theme.Green
import com.example.capsule.ui.theme.White
import com.example.capsule.ui.theme.WhiteSmoke

@Composable
fun HomePage(
    viewModel: PatientViewModel = viewModel(),
    modifier: Modifier = Modifier,
    onSearchClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onProfilePatientClick: () -> Unit = {},
    onAppointmentsClick: () -> Unit = {},
    onMessagesClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val patient = viewModel.patient.value
    val isLoading = viewModel.isLoading.value

    LaunchedEffect(Unit) {
        viewModel.loadCurrentPatientProfile()
    }

    if (patient == null || isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(White),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Blue)
        }
        return
    }

    Scaffold { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .background(WhiteSmoke)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // ---------------- HEADER ----------------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clickable { onProfilePatientClick() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // User avatar
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(
                            id = patient.profileImageRes ?: R.drawable.patient_profile
                        ),
                        contentDescription = "",
                        modifier = Modifier
                            .size(55.dp)
                            .clip(CircleShape)
                            .shadow(4.dp, CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text("Welcome back", color = Color.Gray, fontSize = 13.sp)
                        Text(
                            patient.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }

                IconButton(onClick = onSettingsClick) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "",
                        tint = Color.DarkGray,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))


            // ---------------- SEARCH BAR ----------------
            SearchBar(
                onClick = onSearchClick,
                color = Color(0xFF4CAF50),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))


            // ---------------- QUICK CARDS ----------------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                DashboardCard(
                    title = "Appointments",
                    icon = R.drawable.ic_calendar,
                    bgColor = Color(0xFFFFEAD8),
                    iconColor = Color(0xFFFF8728),
                    onClick = onAppointmentsClick,
                    modifier = Modifier.weight(1f)
                )

                DashboardCard(
                    title = "Chats",
                    icon = R.drawable.ic_messages,
                    bgColor = Color(0xFFE4FBE4),
                    iconColor = Green,
                    onClick = {
                        onMessagesClick()
                        context.startActivity(Intent(context, ChatActivity::class.java))
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(22.dp))


            // ---------------- PRESCRIPTIONS BUTTON ----------------
            Button(
                onClick = {
                    Toast.makeText(context, "Wait for it!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .padding(horizontal = 14.dp)
                    .height(55.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(Blue),
                shape = RoundedCornerShape(14.dp),
                elevation = ButtonDefaults.buttonElevation(6.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_prescription),
                    contentDescription = "",
                    tint = White,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text("Prescriptions", fontSize = 17.sp, color = White)
            }

            Spacer(modifier = Modifier.height(20.dp))


            // ---------------- OFFERS ----------------
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 14.dp)
            ) {
                val offers = listOf(
                    OfferItem(
                        title = "✨ 20% off your next consultation",
                        color = Color(0xFF4CAF50)
                    ),
                    OfferItem(
                        title = "💉 A new way of vaccination",
                        color = Color(0xFF347deb)
                    )
                )

                items(offers) { item ->
                    SliderItem(
                        Title = item.title,
                        Description = "Limited Time Offer",
                        backgroundColor = item.color
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))


            // ---------------- TIPS TITLE ----------------
            Text(
                text = "Health Tips",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))


            // ---------------- TIPS LIST ----------------
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                val tips = listOf(
                    Tip(
                        head = "5 Tips for a Healthy Life",
                        description = "Small lifestyle changes can greatly improve heart health.",
                        image = R.drawable.medical_headphones
                    ),
                    Tip(
                        head = "Understanding Blood Pressure",
                        description = "Learn what the numbers mean and how to manage them.",
                        image = R.drawable.medical_gauge
                    )
                )

                items(tips) { item ->
                    AdviceItem(
                        Head = item.head,
                        Description = item.description,
                        Image = item.image
                    )
                }
            }
        }
    }
}


@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    color: Color = Color(0xFF4CAF50)
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .padding(horizontal = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    color = color,
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable { onClick() }
                .padding(vertical = 10.dp, horizontal = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Bar",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Find Care",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 16.sp
                )
            }
        }
    }
}


@Composable
fun SliderItem(
    modifier: Modifier = Modifier,
    Title: String,
    Description: String,
    backgroundColor: Color
) {
    Card(
        modifier = modifier
            .padding(10.dp)
            .width(250.dp)
            .height(150.dp)
            .clip(RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(10.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize()
        ) {
            Text(
                text = Title,
                fontSize = 21.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.height(50.dp),
                color = Color(0xffffffff),
                lineHeight = 20.sp
            )

            Text(text = Description, fontSize = 15.sp, color = Color(0xffffffff))
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {},
                colors = ButtonColors(
                    containerColor = Color(0xFFFFFFFF),
                    contentColor = backgroundColor,
                    disabledContainerColor = ButtonDefaults.buttonColors().disabledContainerColor,
                    disabledContentColor = ButtonDefaults.buttonColors().disabledContentColor
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Learn More")
            }
        }
    }
}

@Composable
fun AdviceItem(
    modifier: Modifier = Modifier,
    Head: String,
    Description: String,
    Image: Int
) {
    Card(
        modifier = modifier
            .padding(10.dp)
            .fillMaxWidth()
            .height(150.dp)
            .shadow(200.dp, shape = RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFffffff)
        ),
        elevation = CardDefaults.cardElevation(150.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(15.dp)
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = Image),
                contentDescription = "headphones",
                modifier = Modifier
                    .clip(RoundedCornerShape(size = 20.dp))
                    .size(120.dp)
            )
            Column {
                Text(
                    text = Head,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 15.dp, top = 10.dp)
                )
                Text(
                    text = Description,
                    modifier = Modifier.padding(start = 15.dp, top = 10.dp),
                    color = Color(0xFF5e5e5e)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomepagePreview() {
    CapsuleTheme {
        HomePage(
            onProfilePatientClick = {},
            onSearchClick = {},
            onSettingsClick = {},
            onAppointmentsClick = {},
            onMessagesClick = {}
        )
    }
}