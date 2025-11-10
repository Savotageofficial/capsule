package com.example.capsule

import android.content.Intent
import android.os.Bundle
import android.util.Log
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


class SignUpActivity : ComponentActivity() {

    private lateinit var repository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = AuthRepository()
        setContent {
            CapsuleTheme {
                SignUpScreen(
                    onSignUpClick = { name, email, password, userType, specialization ->
                        createAccount(name, email, password, userType, specialization)
                    }
                )
            }
        }
    }

    private fun createAccount(name: String, email: String, password: String, userType: String, specialization: String?) {
        repository.createAccount(email, password, name, userType, specialization,
            onSuccess = {
                Toast.makeText(this, "Check your Email", Toast.LENGTH_SHORT).show()
            },
            onFailure = { error ->
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun SignUpScreen(
    onSignUpClick: (name: String, email: String, password: String, userType: String, specialization: String?) -> Unit = { _, _, _, _, _ -> }
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
        colors = listOf(
            Color(0xFF009FFD),
            Color(0xFF2A2A72)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBrush)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .size(200.dp)
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


        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
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
            onClick = { onSignUpClick(name, email, password, userType, if (userType == "Doctor") specialization else null) },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                text = "Sign Up",
                color = buttonTextColor,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
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
        singleLine = true,
    )
    Spacer(modifier = Modifier.height(14.dp))
}

