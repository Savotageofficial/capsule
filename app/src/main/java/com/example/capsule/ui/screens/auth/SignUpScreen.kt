package com.example.capsule.ui.screens.auth

import android.widget.Toast
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.capsule.AuthRepository
import com.example.capsule.R

@Composable
fun SignUpScreen(
    repository: AuthRepository,
    navController: NavHostController,
    onSignUpSuccess: (userType: String) -> Unit = {},
    onBackToLogin: () -> Unit = {}
) {
    val toggleBgColor = Color(0xFFDAE9EE)
    val toggleSelectedBg = Color.White
    val toggleSelectedText = Color(0xFF38494C)
    val toggleUnselectedText = Color(0xFF535356)
    val buttonColor = Color(0xFF19CEFF)
    val buttonTextColor = Color.White

    var userType by remember { mutableStateOf("Patient") }

    var name by remember { mutableStateOf("") }
    var specialization by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

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

            // Doctor/Patient toggle
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

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "Create Account",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(30.dp))

            InputField("Name", name) { name = it }
            if (userType == "Doctor") {
                InputField("Specialization", specialization) { specialization = it }
            }
            InputField("Email", email) { email = it }
            InputField("Password", password) { password = it }

            Spacer(modifier = Modifier.height(20.dp))

            // Sign up button
            Button(
                onClick = {
                    if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
                        Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    isLoading = true
                    repository.createAccount(
                        email = email,
                        password = password,
                        name = name,
                        userType = userType,
                        specialization = if (userType == "Doctor") specialization else null,
                        onSuccess = {
                            isLoading = false
                            Toast.makeText(context, "Check your email for verification", Toast.LENGTH_SHORT).show()
                            onSignUpSuccess(userType) // Navigate according to user type
                        },
                        onFailure = {
                            isLoading = false
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        }
                    )
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

            Spacer(modifier = Modifier.height(20.dp))

            // Go to login
            Text(
                text = "Already have an account? Login",
                fontSize = 15.sp,
                color = Color.White,
                modifier = Modifier.clickable { onBackToLogin() }
            )
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
        singleLine = true
    )
    Spacer(modifier = Modifier.height(14.dp))
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    val navController = rememberNavController()
    val repository = AuthRepository()

    MaterialTheme {
        SignUpScreen(navController = navController, repository = repository)
    }
}
