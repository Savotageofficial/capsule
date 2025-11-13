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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.capsule.AuthRepository
import com.example.capsule.R

@Composable
fun LoginScreen(
    repository: AuthRepository,
    navController: NavHostController,
    onLoginSuccess: (userType: String) -> Unit = {},
    onSignUpClick: () -> Unit = {}
) {
    val inputBgColor = Color(0xFFE6F1F5)
    val inputPlaceholderColor = Color(0xFF6098AA)
    val buttonColor = Color(0xFF19CEFF)
    val buttonTextColor = Color.White
    val textGrayColor = Color(0xFF6B7C86)

    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF009FFD),
            Color(0xFF2A2A72)
        )
    )

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.size(350.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Email input
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text(text = "Email", color = inputPlaceholderColor, fontSize = 16.sp) },
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
            modifier = Modifier.fillMaxWidth().height(54.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Password input
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text(text = "Password", color = inputPlaceholderColor, fontSize = 16.sp) },
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
            modifier = Modifier.fillMaxWidth().height(54.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Forgot password
        Text(
            text = "Forgot Password?",
            modifier = Modifier
                .align(Alignment.End)
                .clickable {
                    if (email.isNotEmpty()) {
                        repository.sendPasswordResetEmail(email,
                            onSuccess = {
                                Toast.makeText(context, "Email sent",
                                    Toast.LENGTH_SHORT).show() },
                            onFailure = {
                                error -> Toast.makeText(context,
                                error, Toast.LENGTH_SHORT).show()
                            }
                        )
                    } else {
                        Toast.makeText(context, "Enter your email first", Toast.LENGTH_SHORT).show()
                    }
                },
            color = Color.White,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            textDecoration = TextDecoration.Underline
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Login button
        Button(
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    isLoading = true
                    repository.signIn(email, password,
                        onSuccess = { userType ->
                            isLoading = false
                            onLoginSuccess(userType) // Navigate according to user type
                        },
                        onFailure = { error ->
                            isLoading = false
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                        }
                    )
                } else {
                    Toast.makeText(context, "Please fill email and password", Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth().height(54.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(22.dp))
            } else {
                Text("Login", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = buttonTextColor)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Sign Up text
        Text(
            buildAnnotatedString {
                append("Don't have an account? ")
                withStyle(
                    style = SpanStyle(color = Color.White, fontWeight = FontWeight.Medium, textDecoration = TextDecoration.Underline)
                ) { append("Sign Up") }
            },
            fontSize = 15.sp,
            color = textGrayColor,
            modifier = Modifier.clickable { onSignUpClick() }
        )

        Spacer(modifier = Modifier.height(30.dp))
    }
}


@Preview (showBackground = true)
@Composable
fun LoginScreenPreview() {
    // Create a fake NavController for preview
    val navController = rememberNavController()

    // Create a dummy AuthRepository (won’t actually connect to Firebase)
    val repository = AuthRepository()

    MaterialTheme {
        LoginScreen(navController = navController, repository = repository)
    }
}