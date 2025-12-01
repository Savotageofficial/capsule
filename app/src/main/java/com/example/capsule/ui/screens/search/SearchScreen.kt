package com.example.capsule.ui.screens.search

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.capsule.data.model.Doctor
import com.example.capsule.data.repository.SearchRepository
import com.example.capsule.ui.components.SpecializationDropdown
import com.example.capsule.ui.theme.Cyan
import com.example.capsule.ui.theme.WhiteSmoke

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    searchResults: List<String>,
    onBackClick: () -> Unit = {},
    onDoctorSelected: (String) -> Unit = {},
    navController: NavHostController
) {
    val context = LocalContext.current
    var selectedSpecialty by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Find Care",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            tint = Cyan,
                            contentDescription = "Back",
                            modifier = Modifier
                                .size(30.dp)
                                .clickable { onBackClick() }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = WhiteSmoke,
                    titleContentColor = Color(0xFF0A3140)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .background(WhiteSmoke)
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()

        ) {


            MySearchBar(
                searchResults = searchResults,
                currentQuery = searchQuery,
                onQueryChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Dropdown for Speciality
            SpecializationDropdown(
                selectedSpecialty = selectedSpecialty,
                onSpecialtySelected = { selectedSpecialty = it },
                modifier = Modifier.fillMaxWidth(),
                label = "Specialization",
                placeholder = "Select specialty"
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Search Button - adjust padding
            Button(
                onClick = {
                    performSearch(context, searchQuery, selectedSpecialty) { doctors ->
                        val names = ArrayList(doctors.map { it.name })
                        val ids = ArrayList(doctors.map { it.id })
                        val specialities = ArrayList(doctors.map { it.specialty })

                        navController.currentBackStackEntry?.savedStateHandle?.set("names", names)
                        navController.currentBackStackEntry?.savedStateHandle?.set("ids", ids)
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            "specialities",
                            specialities
                        )

                        navController.navigate("searchResults")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0CA7BA))
            ) {
                Text(
                    "Search",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

private fun performSearch(
    context: Context,
    name: String,
    speciality: String,
    onSearchSuccess: (List<Doctor>) -> Unit
) {
    val repository = SearchRepository()
    repository.getDoctorByName(name, Speciality = speciality) { doctors ->
        if (doctors.isNotEmpty()) {
            onSearchSuccess(doctors)
        } else {
            Toast.makeText(context, "No doctors found", Toast.LENGTH_SHORT).show()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MySearchBar(
    searchResults: List<String>,
    onQueryChange: (String) -> Unit,
    currentQuery: String,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var textFieldState by remember { mutableStateOf(currentQuery) }

    SearchBar(
        query = textFieldState,
        onQueryChange = {
            textFieldState = it
            onQueryChange(it)
            expanded = it.isNotEmpty()
        },
        onSearch = {
            expanded = false
        },
        active = expanded,
        onActiveChange = { expanded = it },
        placeholder = { Text("Search doctors") },
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
        },
        trailingIcon = {
            if (expanded) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear",
                    modifier = Modifier.clickable {
                        if (textFieldState.isNotEmpty()) {
                            textFieldState = ""
                            onQueryChange("")
                        } else {
                            expanded = false
                        }
                    }
                )
            }
        },
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 4.dp,
        shadowElevation = 4.dp,
        colors = SearchBarDefaults.colors(
            containerColor = Color(0xFFF5F5F5),
        ),
        content = {
            if (searchResults.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    items(searchResults.size) { index ->
                        val resultText = searchResults[index]
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    textFieldState = resultText
                                    onQueryChange(resultText)
                                    expanded = false
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "History",
                                tint = Color.Gray,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                            Text(resultText, fontSize = 16.sp)
                        }
                    }
                }
            }
        },
        modifier = modifier // Remove the .padding(horizontal = 16.dp) from here
    )
}