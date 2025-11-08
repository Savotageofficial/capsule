package com.example.capsule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.capsule.ui.theme.CapsuleTheme

class PatientHomePageActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CapsuleTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    HomePage(modifier = Modifier.padding(innerPadding))
                }


            }
        }
    }
}

@Composable
fun HomePage(modifier: Modifier = Modifier) {
    Column {
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Capsule",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            ProfileIcon(
                onClick = {
                    // TODO: Navigate to Profile Screen
                }
            )
        }

        Row {
            SearchBar (
                onClick = {
                    // TODO: u know what to do dimwit!
                }
            )

        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.SpaceAround , modifier = Modifier.fillMaxWidth()) {
            NavBox(
                icon = Icons.Default.DateRange,
                label = "Appointments",
                onClick = {
                    //TODO : weeeeeee
                }
            )
            NavBox(
                icon = Icons.AutoMirrored.Filled.Chat,
                label = "Chats",
                onClick = {
                    //TODO : weeeeeee
                }
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(modifier = Modifier.fillMaxWidth() , ) {
            val Offers = listOf(
                "offer11231421",
                "offer2",
                "offer3"
            )

            items(items = Offers){ item ->
                SliderItem(name = item)

            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Health Tips" , fontSize = 24.sp ,
            modifier = Modifier.padding(horizontal = 12.dp),
            fontWeight = FontWeight.Bold)


    }

}

@Composable
fun ProfileIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(48.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Profile",
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .padding(horizontal = 12.dp)
    ) {
        // Shadow Layer
        //deleted because its causing issues
        // Actual Green Bar
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    color = Color(0xFF4CAF50), // soft green
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable { onClick() }
                .padding(vertical = 10.dp, horizontal = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Search for a Doctor",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun NavBox(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier.padding(12.dp),
    onClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .width(120.dp)
            .height(120.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon Circle
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = Color(0xFF4CAF50).copy(alpha = 0.15f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Label
        Text(
            text = label,
            color = Color.Black.copy(alpha = 0.8f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}


@Composable
fun SliderItem(modifier: Modifier = Modifier, name : String){
    Card(modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(20.dp))    , colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF4CAF50)
                    ),
        elevation = CardDefaults.cardElevation(10.dp)


        ) {
            Box(
                modifier
                    .padding(10.dp)
                    .fillMaxSize() , contentAlignment = Alignment.Center
            ){
                Text(text = name , fontSize = 22.sp , fontWeight = FontWeight.Bold)

            }

    }


}
@Preview(showBackground = true , showSystemUi = true)
@Composable
fun HomepagePreview() {
    CapsuleTheme {
        HomePage()
    }
}

//@Preview(showBackground = true , showSystemUi = true)
//@Composable
//fun SliderItemPreview() {
//    CapsuleTheme {
//        SliderItem(name = "Card")
//    }
//}
