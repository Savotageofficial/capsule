package com.example.capsule

import android.os.Bundle
import android.util.Log
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.capsule.ui.theme.CapsuleTheme
import kotlin.math.exp

class SearchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CapsuleTheme {

                var SearchResults: List<String> = listOf("mohamed safwat" , "mohamed hany" , "youssef ahmed" , "hamza hesham")


                    Search(
                        searchResults = SearchResults

                    )


            }
        }
    }
}

@Composable
fun Search(modifier: Modifier = Modifier , searchResults: List<String>) {
    Column(modifier = modifier.background(color = Color(0xFFf5f2f2))
                                .fillMaxSize()) {
        Row (modifier = modifier.fillMaxWidth()
                                .padding(top = 30.dp)
            ,horizontalArrangement = Arrangement.Center){
            Text(text = "Find Care" , textAlign = TextAlign.Center , fontWeight = FontWeight.Bold , fontSize = 22.sp)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = modifier.fillMaxWidth() , horizontalArrangement = Arrangement.Center) {

            MySearchBar(searchResults = searchResults)







        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MySearchBar(text: String = "",
                searchResults: List<String>,
                modifier: Modifier = Modifier){
    var expanded by rememberSaveable { mutableStateOf(false) }
    var textFieldState by remember { mutableStateOf(text) }


    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = textFieldState,
                onQueryChange = {textFieldState = it
                    },
                onSearch = {
//                    onSearch(textFieldState.text.toString())
                    expanded = false
                },
                expanded = expanded,
                onExpandedChange = { expanded = it },
                placeholder = { Text("Search") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search , contentDescription = "Search")
                },
                trailingIcon = {

                    if (expanded){
                        Icon(imageVector = Icons.Default.Close , contentDescription = "close" ,
                            modifier = modifier.clickable{
                                if (textFieldState.isNotEmpty()){
                                textFieldState = ""}else{
                                    expanded = false
                                }
                            })
                    }
                }

            )
        },
        expanded = expanded,
        onExpandedChange = {expanded = it},

    ) {
        LazyColumn {
            items(count = searchResults.size){index ->
            val resultText = searchResults[index]
            Row(








                modifier = Modifier
                    .clickable {
                        textFieldState = resultText
                        expanded = false
                    }
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp , vertical = 12.dp)
                    .background(color = Color.Transparent)
            ){Icon(imageVector = Icons.Default.History , contentDescription = "history" , modifier = modifier.padding(end = 10.dp))
                Text(resultText)}
        }
    } }
}

@Preview(showBackground = true , showSystemUi = true)
@Composable
fun GreetingPreview2() {
    CapsuleTheme {
        Search(searchResults = listOf("mohamed safwat" , "mohamed hany" , "youssef ahmed" , "hamza hesham"))
    }
}