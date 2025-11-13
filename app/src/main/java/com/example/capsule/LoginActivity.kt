//package com.example.capsule
//
//import android.content.Intent
//import android.os.Bundle
//import android.widget.Toast
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.SpanStyle
//import androidx.compose.ui.text.buildAnnotatedString
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextDecoration
//import androidx.compose.ui.text.withStyle
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.capsule.ui.theme.CapsuleTheme
//
//class LoginActivity : ComponentActivity() {
//
//    private lateinit var repository: AuthRepository
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        repository = AuthRepository()
//
//        // Check if already signed in
//        if (repository.isUserSignedIn()) {
//            repository.getCurrentUserType { userType ->
//                navigateToHome(userType ?: "Patient")
//            }
//            return
//        }
//
//        setContent {
//            CapsuleTheme {
//                LoginScreen(
//                    onLoginClick = { email, password ->
//                        signin(email, password)
//                    },
//                    onSignUpClick = {
//                        startActivity(Intent(this, SignUpActivity::class.java))
//                    },
//                    onForgotPasswordClick = { email ->
//                        repository.sendPasswordResetEmail(email,
//                            onSuccess = {
//                                Toast.makeText(this, "Email sent", Toast.LENGTH_SHORT).show()
//                            },
//                            onFailure = { error ->
//                                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
//                            }
//                        )
//                    }
//                )
//            }
//        }
//    }
//
//    private fun signin(email: String, password: String) {
//        repository.signIn(email, password,
//            onSuccess = { userType ->
//                navigateToHome(userType)
//            },
//            onFailure = { error ->
//                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
//            }
//        )
//    }
//
//    private fun navigateToHome(userType: String) {
//        val intent = if (userType == "Doctor") {
//            Intent(this, PatientHomePageActivity::class.java)
//        } else {
//            Intent(this, PatientHomePageActivity::class.java)
//        }
//        startActivity(intent)
//        finish()
//    }
//}
//
//@Composable
//fun LoginScreen(
//    onLoginClick: (email: String, password: String) -> Unit = { _, _ -> },
//    onSignUpClick: () -> Unit = {},
//    onForgotPasswordClick: (email: String) -> Unit = {}
//) {
//    val inputBgColor = Color(0xFFE6F1F5)
//    val inputPlaceholderColor = Color(0xFF6098AA)
//    val buttonColor = Color(0xFF19CEFF)
//    val buttonTextColor = Color.White
//    val textGrayColor = Color(0xFF6B7C86)
//
//    var email by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//
//    val gradientBackground = Brush.verticalGradient(
//        colors = listOf(
//            Color(0xFF009FFD),
//            Color(0xFF2A2A72)
//        )
//    )
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(gradientBackground)
//            .padding(horizontal = 24.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Top // start from the top
//    ) {
//
//        Spacer(modifier = Modifier.height(24.dp)) // optional small gap from top
//
//
//        Image(
//            painter = painterResource(id = R.drawable.logo),
//            contentDescription = "Logo",
//            modifier = Modifier
//                .size(350.dp) // adjust size if needed
//        )
//
//        Spacer(modifier = Modifier.height(20.dp)) // space before inputs
//
//        // Email input
//        OutlinedTextField(
//            value = email,
//            onValueChange = { email = it },
//            placeholder = { Text(text = "Email", color = inputPlaceholderColor, fontSize = 16.sp) },
//            colors = TextFieldDefaults.colors(
//                focusedContainerColor = inputBgColor,
//                unfocusedContainerColor = inputBgColor,
//                cursorColor = buttonColor,
//                focusedTextColor = Color.Black,
//                unfocusedTextColor = Color.Black,
//                focusedIndicatorColor = Color.Transparent,
//                unfocusedIndicatorColor = Color.Transparent
//            ),
//            shape = RoundedCornerShape(20.dp),
//            singleLine = true,
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(54.dp)
//        )
//
//        Spacer(modifier = Modifier.height(14.dp))
//
//        // Password input
//        OutlinedTextField(
//            value = password,
//            onValueChange = { password = it },
//            placeholder = {
//                Text(
//                    text = "Password",
//                    color = inputPlaceholderColor,
//                    fontSize = 16.sp
//                )
//            },
//            colors = TextFieldDefaults.colors(
//                focusedContainerColor = inputBgColor,
//                unfocusedContainerColor = inputBgColor,
//                cursorColor = buttonColor,
//                focusedTextColor = Color.Black,
//                unfocusedTextColor = Color.Black,
//                focusedIndicatorColor = Color.Transparent,
//                unfocusedIndicatorColor = Color.Transparent
//            ),
//            shape = RoundedCornerShape(20.dp),
//            singleLine = true,
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(54.dp)
//        )
//
//        Spacer(modifier = Modifier.height(12.dp))
//
//        // Forgot password
//        Text(
//            text = "Forgot Password?",
//            modifier = Modifier
//                .align(Alignment.End)
//                .clickable { onForgotPasswordClick(email) },
//            color = Color.White,
//            fontWeight = FontWeight.Medium,
//            fontSize = 14.sp,
//            textDecoration = TextDecoration.Underline
//        )
//
//        Spacer(modifier = Modifier.height(30.dp))
//
//        // Login button
//        Button(
//            onClick = { onLoginClick(email, password) },
//            colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
//            shape = RoundedCornerShape(20.dp),
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(54.dp)
//        ) {
//            Text(
//                text = "Login",
//                fontWeight = FontWeight.Bold,
//                fontSize = 18.sp,
//                color = buttonTextColor
//            )
//
//        }
//
//            Spacer(modifier = Modifier.height(40.dp))
//
//
//
//            Text(
//                buildAnnotatedString {
//                    append("Don't have an account? ")
//                    withStyle(
//                        style = SpanStyle(
//                            color = Color.White,
//                            fontWeight = FontWeight.Medium,
//                            textDecoration = TextDecoration.Underline
//                        )
//                    ) {
//                        append("Sign Up")
//                    }
//                },
//                fontSize = 15.sp,
//                color = textGrayColor,
//                modifier = Modifier.clickable { onSignUpClick() }
//            )
//
//            Spacer(modifier = Modifier.height(30.dp))
//
//    }
//}
