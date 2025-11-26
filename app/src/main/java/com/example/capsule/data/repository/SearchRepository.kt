package com.example.capsule.data.repository

import android.util.Log
import com.example.capsule.data.model.Doctor
import com.example.capsule.data.model.Patient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class SearchRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    var doctorlist = mutableListOf<Doctor>(

    )

    fun getDoctorByName(Name: String){


        db.collection("doctors")
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {


                    // Access each document
                    val data = document.data
                    val doctor = document.toObject<Doctor>()

                    if (doctor != null && doctor.name.contains(Name)){

                        doctorlist.add(doctor)
                        
                    }

                    for (item in doctorlist){
                        Log.d("trace" , item.name)
                    }

                }
            }
    }
}