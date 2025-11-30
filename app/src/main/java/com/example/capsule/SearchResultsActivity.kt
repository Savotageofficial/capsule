//package com.example.capsule
//
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.Scaffold
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.tooling.preview.Preview
//import com.example.capsule.data.model.Doctor
//import com.example.capsule.ui.components.DoctorResultCard
//import com.example.capsule.ui.theme.CapsuleTheme
//
//class SearchResultsActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//
//        val ResultNames = intent.getStringArrayListExtra("names")
//        val ResultIds = intent.getStringArrayListExtra("ids")
//        val ResultSpecialities = intent.getStringArrayListExtra("specialities")
//
//        var Results = mutableListOf<Doctor>()
//
//        // Create doctor objects from the intent data
//        if (ResultNames != null) {
//            for (name in ResultNames) {
//                Results.add(Doctor(name = name))
//            }
//        }
//        if (ResultIds != null) {
//            var count = 0
//            for (Id in ResultIds) {
//                Results[count] = Results[count].copy(id = Id)
//                count++
//            }
//        }
//        if (ResultSpecialities != null) {
//            var count = 0
//            for (Spec in ResultSpecialities) {
//                Results[count] = Results[count].copy(specialty = Spec)
//                count++
//            }
//        }
//
//        // Set default values for missing data
//        Results = Results.map { doctor ->
//            doctor.copy(
//                rating = if (doctor.rating == 0.0) 4.5 else doctor.rating,
//                sessionPrice = if (doctor.sessionPrice == 0.0) 50.0 else doctor.sessionPrice
//            )
//        }.toMutableList()
//
//        for (doc in Results) {
//            Log.d("trace", doc.name)
//        }
//
//        setContent {
//            CapsuleTheme {
//                Scaffold { innerPadding ->
//                    SearchResultsList(
//                        results = Results,
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun SearchResultsList(
//    modifier: Modifier = Modifier,
//    results: List<Doctor>,
//) {
//    val context = LocalContext.current
//
//    LazyColumn(
//        modifier = modifier.fillMaxSize()
//    ) {
//        items(items = results) { doctor ->
//
//            DoctorResultCard(
//                doctor = doctor,
//                onClick = {
//
//                    }
//                }
//            )
//        }
//    }
//}
//
//
//@Preview(showBackground = true)
//@Composable
//fun SearchResultsPreview() {
//    CapsuleTheme {
//        val sampleDoctors = listOf(
//            Doctor(
//                id = "1",
//                name = "John Smith",
//                specialty = "Cardiologist",
//                rating = 4.8,
//                sessionPrice = 75.0
//            ),
//            Doctor(
//                id = "2",
//                name = "Sarah Johnson",
//                specialty = "Neurologist",
//                rating = 4.6,
//                sessionPrice = 65.0
//            )
//        )
//        SearchResultsList(results = sampleDoctors)
//    }
//}