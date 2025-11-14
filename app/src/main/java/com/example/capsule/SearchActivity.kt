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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
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
        Spacer(modifier = Modifier.height(24.dp))
        Row() {
            var Specializations = listOf("Neurologist" , "Allergist" , "Anesthesiologist" , "Cardiologists" , "Colon and Rectal Surgeon" , "Critical Care Medicine Specialist" , "Dermatologist" , "Endocrinologist" , "Emergency Medicine Specialist" , "Family Physician" , "Gastroenterologist" , "Geriatric Medicine Specialist" , "Hematologist" , "Nephrologist" , "Oncologist" , "Ophthalmologist" , "Pathologist" , "Otolaryngologist" , "Physiatrist" , "Psychiatrist")

            MyDropDown(Specializations)



        }
//        Button(onClick = {} ,
//            modifier = modifier.fillMaxWidth()
//                .padding(horizontal = 30.dp)
//                .height(60.dp),
//            shape = RoundedCornerShape(10.dp),
//            colors = ButtonColors(containerColor = Color(0xFF13c8ec) , contentColor = Color(0xFFFFFFFF) , disabledContainerColor = ButtonDefaults.buttonColors().disabledContainerColor , disabledContentColor = ButtonDefaults.buttonColors().disabledContentColor )
//        ) { Text("Apply Filters",
//            fontWeight = FontWeight.Bold,
//            fontSize = 15.sp
//            )
//        }

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
                    //                   TODO(search in the database)

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
                        //                   TODO(search in the database)
                    }
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp , vertical = 12.dp)
                    .background(color = Color.Transparent)
            ){Icon(imageVector = Icons.Default.History , contentDescription = "history" , modifier = modifier.padding(end = 10.dp))
                Text(resultText)}
        }
    } }
}



@Composable
fun MyDropDown(Specializations : List<String>) {

    // Declaring a boolean value to store
    // the expanded state of the Text Field
    var mExpanded by remember { mutableStateOf(false) }

    // Create a list of cities
    val mCities = Specializations

    // Create a string value to store the selected city
    var mSelectedText by remember { mutableStateOf("Speciality") }

    var mTextFieldSize by remember { mutableStateOf(Size.Zero)}

    // Up Icon when expanded and down icon when collapsed
    val icon = if (mExpanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    Column(Modifier.padding(20.dp)) {

        // Create an Outlined Text Field
        // with icon and not expanded
        Row(

            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(color = Color(0xFFdff2f6))
                .onGloballyPositioned { coordinates ->
                    // This value is used to assign to
                    // the DropDown the same width
                    mTextFieldSize = coordinates.size.toSize()
                }
                .clickable { mExpanded = !mExpanded },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween


        ){Text(mSelectedText,
                modifier = Modifier.padding(start = 30.dp)
        )
            Icon(icon,"contentDescription",
                Modifier
                    .padding(end = 30.dp)
            )
        }

        // Create a drop-down menu with list of cities,
        // when clicked, set the Text Field text as the city selected
        DropdownMenu(
            expanded = mExpanded,
            onDismissRequest = { mExpanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current){mTextFieldSize.width.toDp()})

        ) {
            mCities.forEach { label ->
                DropdownMenuItem(
                    text = { Text(text = label) },
                    onClick = {
                        mSelectedText = label
                        mExpanded = false
                    }
                )
            }
        }
    }
}


@Preview(showBackground = true , showSystemUi = true)
@Composable
fun GreetingPreview2() {
    CapsuleTheme {
        Search(searchResults = listOf("mohamed safwat" , "mohamed hany" , "youssef ahmed" , "hamza hesham"))
    }
}