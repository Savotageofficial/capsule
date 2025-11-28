package com.example.capsule

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.capsule.ui.theme.CapsuleTheme

class ChatSelectionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CapsuleTheme {

            }
        }
    }
}

@Composable
fun ChatSelection(name: String, modifier: Modifier = Modifier) {


    LazyColumn {

    }

}

@Preview(showBackground = true , showSystemUi = true)
@Composable
fun ChatSelectionPreview() {
    CapsuleTheme {

    }
}