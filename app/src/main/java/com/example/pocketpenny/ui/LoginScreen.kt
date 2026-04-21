package com.example.pocketpenny.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pocketpenny.data.UserDao
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController, userDao: UserDao) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val skyBlue = Color(0xFF64B5F6)
    val darkBlue = Color(0xFF1565C0)
    val lightCard = Color(0xFFE3F2FD)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(skyBlue, darkBlue)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = lightCard),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(28.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Welcome Back",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = darkBlue
                )
                Text(
                    "Please enter your details",
                    fontSize = 13.sp,
                    color = Color.Gray
                )

                Spacer(Modifier.height(24.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; emailError = "" },
                    label = { Text("Email") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = emailError.isNotEmpty()
                )
                if (emailError.isNotEmpty()) {
                    Text(emailError, color = Color.Red, fontSize = 12.sp,
                        modifier = Modifier.fillMaxWidth())
                }

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; passwordError = "" },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        TextButton(onClick = { passwordVisible = !passwordVisible }) {
                            Text(if (passwordVisible) "Hide" else "Show", fontSize = 12.sp)
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = passwordError.isNotEmpty()
                )
                if (passwordError.isNotEmpty()) {
                    Text(passwordError, color = Color.Red, fontSize = 12.sp,
                        modifier = Modifier.fillMaxWidth())
                }

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = {
                        emailError = ""
                        passwordError = ""
                        var valid = true

                        if (email.isBlank()) { emailError = "Email is required"; valid = false }
                        if (password.isBlank()) { passwordError = "Password is required"; valid = false }

                        if (!valid) return@Button

                        scope.launch {
                            loading = true
                            val user = userDao.getUserByEmail(email)
                            if (user != null && user.password == password) {
                                Toast.makeText(context, "Welcome back ${user.firstName}!", Toast.LENGTH_LONG).show()
                                navController.navigate("home")
                            } else {
                                passwordError = "Invalid email or password"
                            }
                            loading = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = skyBlue),
                    enabled = !loading
                ) {
                    Text(
                        if (loading) "Logging in..." else "LOGIN",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(Modifier.height(16.dp))

                TextButton(onClick = { navController.navigate("register") }) {
                    Text("Don't have an account? ", color = Color.Gray, fontSize = 13.sp)
                    Text("Sign Up", color = darkBlue, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}