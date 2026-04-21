package com.example.pocketpenny.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.pocketpenny.data.User
import com.example.pocketpenny.data.UserDao
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(navController: NavController, userDao: UserDao) {

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }
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
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Sign Up",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = darkBlue
                )
                Text(
                    "Create your account",
                    fontSize = 13.sp,
                    color = Color.Gray
                )

                Spacer(Modifier.height(20.dp))

                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("First Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Last Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    trailingIcon = {
                        TextButton(onClick = { passwordVisible = !passwordVisible }) {
                            Text(if (passwordVisible) "Hide" else "Show", fontSize = 12.sp)
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    trailingIcon = {
                        TextButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Text(if (confirmPasswordVisible) "Hide" else "Show", fontSize = 12.sp)
                        }
                    },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(Modifier.height(12.dp))

                if (error.isNotEmpty()) {
                    Text(error, color = Color.Red, fontSize = 13.sp)
                    Spacer(Modifier.height(8.dp))
                }

                Button(
                    onClick = {
                        if (firstName.isBlank() || lastName.isBlank() || email.isBlank() ||
                            username.isBlank() || password.isBlank() || confirmPassword.isBlank()
                        ) {
                            error = "Please fill in all fields"
                            return@Button
                        }
                        if (!email.contains("@") || !email.contains(".")) {
                            error = "Enter a valid email address"
                            return@Button
                        }
                        if (password.length < 6) {
                            error = "Password must be at least 6 characters"
                            return@Button
                        }
                        if (password != confirmPassword) {
                            error = "Passwords do not match"
                            return@Button
                        }
                        scope.launch {
                            loading = true
                            val usernameExists = userDao.checkIfUsernameExists(username)
                            val emailExists = userDao.checkIfEmailExists(email)
                            when {
                                usernameExists -> error = "Username already taken"
                                emailExists -> error = "Email already registered"
                                else -> {
                                    userDao.registerUser(
                                        User(
                                            firstName = firstName,
                                            lastName = lastName,
                                            email = email,
                                            username = username,
                                            password = password
                                        )
                                    )
                                    Toast.makeText(context, "Registration successful!", Toast.LENGTH_LONG).show()
                                    navController.navigate("login")
                                }
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
                        if (loading) "Registering..." else "SIGN UP",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(Modifier.height(16.dp))

                TextButton(onClick = { navController.navigate("login") }) {
                    Text("Already have an account? ", color = Color.Gray, fontSize = 13.sp)
                    Text("Log In", color = darkBlue, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}