package com.example.capsule

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.firestore.FirebaseFirestore

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun notEmptyList() {
        val latch = CountDownLatch(1)

        var result: List<String>? = null

        FirebaseFirestore.getInstance()
            .collection("patients")
            .document("SQMnxZdG9iVLP5LDcfRCjC9Sedf2")
            .get()
            .addOnSuccessListener { doc ->
                result = doc.get("msgHistory") as? List<String> ?: emptyList()
                latch.countDown()
            }
            .addOnFailureListener {
                latch.countDown()
            }

        latch.await(5, TimeUnit.SECONDS)

        assertNotEquals(emptyList<String>() , result)
    }
}