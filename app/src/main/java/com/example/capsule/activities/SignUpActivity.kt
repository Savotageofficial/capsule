package com.example.capsule

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.capsule.ui.theme.CapsuleTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : ComponentActivity() {

    private lateinit var repository: AuthRepository
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        repository = AuthRepository()

        setContent {
            CapsuleTheme {

                var showOnboarding by remember { mutableStateOf(true) }
                var showSignUp by remember { mutableStateOf(false) }

                when {
                    showOnboarding -> {
                        OnboardingScreen(
                            onFinish = {
                                showOnboarding = false
                                showSignUp = true
                            }
                        )
                    }

                    showSignUp -> {
                        var isLoading by remember { mutableStateOf(false) }

                        SignUpScreen(
                            onSignUpClick = { name, email, password, userType, specialization ->
                                isLoading = true
                                createAccount(
                                    name, email, password, userType, specialization,
                                    onSuccess = {
                                        isLoading = false
                                        Toast.makeText(this, "Check your email", Toast.LENGTH_SHORT).show()
                                    },
                                    onFailure = { error ->
                                        isLoading = false
                                        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            },
                            isLoading = isLoading
                        )
                    }
                }
            }
        }
    }

    private fun createAccount(
        name: String,
        email: String,
        password: String,
        userType: String,
        specialization: String?,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        repository.createAccount(email, password, name, userType, specialization, onSuccess, onFailure)
    }
}

/* -------------------------------------------------------------------
   ---------------------- ONBOARDING SCREEN ---------------------------
   ------------------------------------------------------------------- */

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {

    val pages = listOf(
        OnboardingPage(
            title = "Manage your visits",
            desc = "Easily book, cancel and manage your doctor visits."
        ),
        OnboardingPage(
            title = "Explore specialties",
            desc = "Find the right doctor with the right specialty."
        ),
        OnboardingPage(
            title = "Get started quickly",
            desc = "Begin accessing all app features smoothly."
        )
    )

    var page by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF009FFD), Color(0xFF2A2A72))
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Spacer(modifier = Modifier.height(60.dp))

            Text(
                text = pages[page].title,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = pages[page].desc,
                fontSize = 18.sp,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 32.dp),
            )

            Spacer(modifier = Modifier.height(180.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .size(if (page == it) 14.dp else 8.dp)
                            .padding(4.dp)
                            .background(
                                if (page == it) Color.White else Color.White.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(50)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    if (page < pages.lastIndex) page++
                    else onFinish()
                },
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF19CEFF)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = if (page < pages.lastIndex) "Next" else "Get Started",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (page < pages.lastIndex) {
                Text(
                    text = "Skip",
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier.clickable { onFinish() }
                )
            }
        }
    }
}

data class OnboardingPage(val title: String, val desc: String)

/* -------------------------------------------------------------------
   ------------------------- SIGN UP SCREEN ---------------------------
   ------------------------------------------------------------------- */

@Composable
fun SignUpScreen(
    onSignUpClick: (String, String, String, String, String?) -> Unit,
    isLoading: Boolean
) {

    val gradientBrush = Brush.verticalGradient(
        listOf(Color(0xFF009FFD), Color(0xFF2A2A72))
    )

    var userType by remember { mutableStateOf("Patient") }

    var name by remember { mutableStateOf("") }
    var specialization by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Text(
                text = "Create Account",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(20.dp))

            // USER TYPE TOGGLE
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(Color(0x55FFFFFF), RoundedCornerShape(30.dp)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ToggleBox("Patient", userType == "Patient") { userType = "Patient" }
                ToggleBox("Doctor", userType == "Doctor") { userType = "Doctor" }
            }

            Spacer(modifier = Modifier.height(20.dp))

            CustomInput("Name", name) { name = it }

            if (userType == "Doctor") {
                CustomInput("Specialization", specialization) { specialization = it }
            }

            CustomInput("Email", email) { email = it }
            CustomInput("Password", password) { password = it }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    onSignUpClick(
                        name, email, password, userType,
                        if (userType == "Doctor") specialization else null
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(Color.White)
            ) {
                Text(
                    text = if (isLoading) "Loading..." else "Sign Up",
                    color = Color(0xFF2A2A72),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun ToggleBox(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .background(
                if (selected) Color.White else Color.Transparent,
                RoundedCornerShape(30.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            color = if (selected) Color(0xFF2A2A72) else Color.White,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun CustomInput(hint: String, text: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = text,
        onValueChange = onChange,
        placeholder = { Text(hint, color = Color(0xFFE3F2FD)) },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        singleLine = true,
        shape = RoundedCornerShape(18.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0x33FFFFFF),
            unfocusedContainerColor = Color(0x33FFFFFF),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = Color.White
        )
    )

    Spacer(modifier = Modifier.height(12.dp))
}
