package com.example.capsule

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.capsule.data.model.Doctor
import com.example.capsule.ui.theme.CapsuleTheme

class SearchResultsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        val ResultNames = intent.getStringArrayListExtra("names")
//       for (name in ResultNames!!){
//
//            Log.d("trace" , name)
//
//        }
        val ResultIds = intent.getStringArrayListExtra("ids")
        val ResultSpecialities = intent.getStringArrayListExtra("specialities")

        var Results = mutableListOf<Doctor>()
        if (ResultNames != null) {
            for (name in ResultNames){
                Results.add(Doctor(name = name))
            }
        }
        if (ResultIds != null) {
            var count = 0
            for (Id in ResultIds){
                Results[count].id = Id
                count++
            }
        }
        if (ResultSpecialities != null) {
            var count = 0
            for (Spec in ResultSpecialities){
                Results[count].specialty = Spec
                count++
            }
        }

        for (doc in Results){
            Log.d("trace" , doc.name)
        }


        setContent {
            CapsuleTheme {
                SearchResult(results = Results)

            }
        }
    }
}

@Composable
fun SearchResult(modifier: Modifier = Modifier , results : List<Doctor>) {

    val context = LocalContext.current

    LazyColumn(
        modifier = modifier.fillMaxSize()
            .padding(top = 40.dp , start = 10.dp , end = 10.dp)
    ) {
        items(items = results){ item ->
            ItemCard(
                Title = item.name,
                Description = item.specialty,
                image = R.drawable.doc_prof_unloaded,
                onChatClick = {
                    val myintent = Intent(context, ChatActivity::class.java).apply {
                        putExtra("name" , item.name)
                        putExtra("Id" , item.id)

                    }
                    context.startActivity(myintent)
                }
            )

        }
    }

}



@Composable
fun ItemCard(
    modifier: Modifier = Modifier,
    Title: String, Description: String,
    backgroundColor: Color = Color(0xFFA4FDEE),
    image: Int,
    onclick: () -> Unit = {},
    onChatClick: () -> Unit = {}
) {
    Card(
        modifier
            .padding(top = 10.dp, start = 10.dp, end = 10.dp, bottom = 30.dp)
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),

        elevation = CardDefaults.cardElevation(50.dp),


        ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .weight(3f)
                // Clip only the top corners
                .clip(
                    RoundedCornerShape(20.dp).copy(
                        bottomStart = CornerSize(0.dp),
                        bottomEnd = CornerSize(0.dp)
                    )
                )
        ) {
            // In a real app, replace `painterResource` with an async image loader (like Coil)
            // that fetches an image URL. Using a placeholder image for this example.
            Image(
                painter = painterResource(id = image),
                contentDescription = "profileimage",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth()
            )

        }
//        Image(
//            painter = painterResource(R.drawable.pasta_recipe),
//            contentDescription = "pasta",
//            modifier = modifier.weight(3f)
//                .fillMaxWidth()
//        )
        Box(
            modifier = modifier.weight(2f)
        ){
            Column(
                modifier
                    .padding(10.dp)
                    .fillMaxSize()
            ) {


                Row(
                    modifier = modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        style = TextStyle(
                            letterSpacing = (-0.3).sp
                        ),
                        text = Title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.height(30.dp),
                        color = Color(0xff000000),
                        lineHeight = 20.sp
                    )


                }
                Row(
                    modifier = modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = Description, fontSize = 15.sp, color = Color(0xFF000000))
                }



            }

            FloatingActionButton(
                onClick = onChatClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Chat,
                    contentDescription = null
                )
            }


        }


    }


}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CapsuleTheme {
//        SearchResult(results = Results)
        ItemCard(Title = "yousef" , Description = "neurologist" , image = R.drawable.doc_prof_unloaded)
    }
}