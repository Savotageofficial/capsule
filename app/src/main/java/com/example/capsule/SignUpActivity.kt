package com.example.capsule

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.capsule.ui.theme.CapsuleTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*

class SignUpActivity : ComponentActivity() {

    private lateinit var repository: AuthRepository
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = AuthRepository()

        setContent {
            CapsuleTheme {
                var showSplash by remember { mutableStateOf(true) }

                if (showSplash) {
                    SplashScreen()
                    // Run the check in background
                    LaunchedEffect(Unit) {
                        delay(1000)
                        checkUserStatus(
                            onUserFound = { userType ->
                                val intent = if (userType == "Doctor") {
                                    Intent(this@SignUpActivity, PatientHomePageActivity::class.java)
                                } else {
                                    Intent(this@SignUpActivity, PatientHomePageActivity::class.java)
                                }
                                startActivity(intent)
                                finish()
                            },
                            onUserNotFound = {
                                showSplash = false // Show the SignUp UI
                            }
                        )
                    }
                } else {
                    // Real Sign Up screen
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

    private fun checkUserStatus(onUserFound: (String) -> Unit, onUserNotFound: () -> Unit) {
        val user = auth.currentUser
        if (user != null) {
            user.reload().addOnSuccessListener {
                if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
                    db.collection("users").document(auth.currentUser!!.uid)
                        .get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                val userType = document.getString("userType") ?: "Patient"
                                // Navigate to MainActivity instead of PatientHomePageActivity
                                val intent = Intent(this@SignUpActivity, MainActivity::class.java).apply {
                                    putExtra("userType", userType)
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                                startActivity(intent)
                                finish()
                            } else {
                                auth.signOut()
                                onUserNotFound()
                            }
                        }
                } else {
                    auth.signOut()
                    onUserNotFound()
                }
            }
        } else {
            onUserNotFound()
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

@Composable
fun SplashScreen() {
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFF009FFD), Color(0xFF2A2A72))
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBrush),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.size(180.dp)
        )
    }
}

@Composable
fun SignUpScreen(
    onSignUpClick: (name: String, email: String, password: String, userType: String, specialization: String?) -> Unit = { _, _, _, _, _ -> },
    isLoading: Boolean = false
) {
    val toggleBgColor = Color(0xFFDAE9EE)
    val toggleSelectedBg = Color.White
    val toggleSelectedText = Color(0xFF38494C)
    val toggleUnselectedText = Color(0xFF535356)
    val buttonColor = Color(0xFF19CEFF)
    val buttonTextColor = Color.White

    var userType by remember { mutableStateOf("Patient") }
    var loginSelected by remember { mutableStateOf("Sign Up") }

    var name by remember { mutableStateOf("") }
    var specialization by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val context = LocalContext.current

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFF009FFD), Color(0xFF2A2A72))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBrush)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(200.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(toggleBgColor),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(30.dp))
                        .background(if (userType == "Patient") toggleSelectedBg else Color.Transparent)
                        .clickable { userType = "Patient" },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Patient",
                        fontSize = 17.sp,
                        color = if (userType == "Patient") toggleSelectedText else toggleUnselectedText,
                        fontWeight = FontWeight.Medium
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(30.dp))
                        .background(if (userType == "Doctor") toggleSelectedBg else Color.Transparent)
                        .clickable { userType = "Doctor" },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Doctor",
                        fontSize = 17.sp,
                        color = if (userType == "Doctor") toggleSelectedText else toggleUnselectedText,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Login",
                    fontSize = 16.sp,
                    color = if (loginSelected == "Login") toggleSelectedText else toggleUnselectedText,
                    modifier = Modifier.clickable {
                        loginSelected = "Login"
                        context.startActivity(Intent(context, LoginActivity::class.java))
                    }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Sign Up",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (loginSelected == "Sign Up") Color.Black else toggleUnselectedText,
                    modifier = Modifier.clickable { loginSelected = "Sign Up" }
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "Create Account",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(40.dp))

            InputField("Name", name) { name = it }
            if (userType == "Doctor") {
                InputField("Specialization", specialization) { specialization = it }
            }
            InputField("Email", email) { email = it }
            InputField("Password", password) { password = it }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    onSignUpClick(name, email, password, userType, if (userType == "Doctor") specialization else null)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                shape = RoundedCornerShape(20.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "Sign Up",
                        color = buttonTextColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun InputField(
    placeholderText: String,
    text: String,
    onValueChange: (String) -> Unit
) {
    val inputBgColor = Color(0xFFDAE9EE)
    val inputTextColor = Color(0xFF6098AA)
    val buttonColor = Color(0xFF19CEFF)

    OutlinedTextField(
        value = text,
        onValueChange = onValueChange,
        placeholder = { Text(text = placeholderText, color = inputTextColor, fontSize = 16.sp) },
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = inputBgColor,
            unfocusedContainerColor = inputBgColor,
            cursorColor = buttonColor,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(20.dp),
        singleLine = true,
    )
    Spacer(modifier = Modifier.height(14.dp))
}
