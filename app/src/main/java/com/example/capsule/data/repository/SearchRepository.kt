package com.example.capsule.data.repository

import android.util.Log
import com.example.capsule.data.model.Doctor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.getField

class SearchRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    val doctorlist = mutableListOf<Doctor>(

    )

    fun getDoctorByName(Name: String){
        doctorlist.clear()


        db.collection("doctors")
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {


                    // Access each document
                    val doctorid = document.id
                    val doctorspecialization = document.getField<String>("specialty")
                    val doctorname = document.getField<String>("name")
                    val doctor = Doctor(id = doctorid , name = doctorname!! , specialty = doctorspecialization!!)
                    val data = document.data
//                    val doctor = document.toObject<Doctor>()

                    if (doctor.name.contains(Name)){

                        doctorlist.add(doctor)
                        
                    }

                    for (item in doctorlist){
                        Log.d("trace" , item.name)
                    }

                }
            }
    }
    fun getDoctorByName(Name: String , Speciality : String , callback: (List<Doctor>) -> Unit){
        doctorlist.clear()
//        Log.d("trace" , Name)
//        Log.d("trace" , Speciality)

        db.collection("doctors")
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    // Access each document
                    val doctorid = document.id
                    val doctorspecialization = document.getField<String>("specialty")
                    val doctorname = document.getField<String>("name")
                    val doctor = Doctor(id = doctorid , name = doctorname!! , specialty = doctorspecialization!!)
                    val data = document.data
//                    val doctor = document.toObject<Doctor>()

                    if (doctor.name.contains(Name, ignoreCase = true) && doctor.specialty.contains(Speciality , ignoreCase = true)){

                        doctorlist.add(doctor)

                    }
//                    for (item in doctorlist){
//                        Log.d("trace" , item.name)
//                    }

                }
                callback(doctorlist)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

}