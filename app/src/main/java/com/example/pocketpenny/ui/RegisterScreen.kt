package com.example.pocketpenny.ui

import android.R.attr.onClick
import android.R.attr.singleLine
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pocketpenny.R
import com.example.pocketpenny.data.User
import com.example.pocketpenny.data.UserDao
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(navController: NavController, userDao: UserDao) {

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    var firstNameError by remember { mutableStateOf("") }
    var lastNameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var usernameError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val skyBlue = Color(0xFF64B5F6)
    val darkBlue = Color(0xFF1565C0)
    val lightCard = Color(0xFFE3F2FD)

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xffbffcff), Color(0xff54c7ff), Color(0xff00a9fc))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient),

        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.speechbubble),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .offset(y = 90.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.pennywave),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .offset(y = 40.dp)
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xffddf4ff)),
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
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A5276)
                    )

                    Spacer(Modifier.height(20.dp))

                    //firstname
                    LoginInputField(
                        value = firstName,
                        onValueChange = { firstName = it; firstNameError = "" },
                        label = "First Name",
                        icon = Icons.Default.Person,
                        isError = firstNameError.isNotEmpty()
                    )
                    if (firstNameError.isNotEmpty()) {
                        Text(
                            firstNameError, color = Color.Red, fontSize = 12.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    //last name
                    LoginInputField(
                        value = lastName,
                        onValueChange = { lastName = it; lastNameError = "" },
                        label = "Last Name",
                        icon = Icons.Default.Person,
                        isError = lastNameError.isNotEmpty()
                    )
                    if (lastNameError.isNotEmpty()) {
                        Text(
                            lastNameError, color = Color.Red, fontSize = 12.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    LoginInputField(
                        value = email,
                        onValueChange = { email = it; emailError = "" },
                        label = "Email",
                        icon = Icons.Default.Email,
                        isError = emailError.isNotEmpty()
                    )
                    if (emailError.isNotEmpty()) {
                        Text(
                            emailError, color = Color.Red, fontSize = 12.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    Spacer(Modifier.height(10.dp))

                    LoginInputField(
                        value = password,
                        onValueChange = { password = it; passwordError = "" },
                        label = "Password",
                        icon = Icons.Default.Lock,
                        isPassword = true,
                        passwordVisible = passwordVisible,
                        onVisibilityToggle = { passwordVisible = !passwordVisible },
                        isError = passwordError.isNotEmpty()
                    )
                    if (passwordError.isNotEmpty()) ErrorText(passwordError)


                    Spacer(Modifier.height(10.dp))

                    LoginInputField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it; confirmPasswordError = "" },
                        label = "Confirm Password",
                        icon = Icons.Default.Lock,
                        isPassword = true,
                        passwordVisible = passwordVisible,
                        onVisibilityToggle = { passwordVisible = !passwordVisible },
                        isError = confirmPasswordError.isNotEmpty()
                    )
                    if (confirmPasswordError.isNotEmpty()) ErrorText(confirmPasswordError)

                    Spacer(Modifier.height(32.dp))

                    Button(
                        onClick = {
                            firstNameError = ""
                            lastNameError = ""
                            emailError = ""
                            usernameError = ""
                            passwordError = ""
                            confirmPasswordError = ""

                            var valid = true

                            if (firstName.isBlank()) {
                                firstNameError = "First name is required"; valid = false
                            }
                            if (lastName.isBlank()) {
                                lastNameError = "Last name is required"; valid = false
                            }
                            if (email.isBlank()) {
                                emailError = "Email is required"; valid = false
                            } else if (!email.contains("@") || !email.contains(".")) {
                                emailError = "Enter a valid email address"; valid = false
                            }

                            if (password.isBlank()) {
                                passwordError = "Password is required"; valid = false
                            } else if (password.length < 6) {
                                passwordError = "Password must be at least 6 characters"; valid =
                                    false
                            }
                            if (confirmPassword.isBlank()) {
                                confirmPasswordError = "Please confirm your password"; valid = false
                            } else if (password != confirmPassword) {
                                confirmPasswordError = "Passwords do not match"; valid = false
                            }

                            if (!valid) return@Button

                            scope.launch {
                                loading = true
                                val emailExists = userDao.checkIfEmailExists(email)
                                when {
                                    emailExists -> emailError = "Email already registered"
                                    else -> {
                                        userDao.registerUser(
                                            User(
                                                firstName = firstName,
                                                lastName = lastName,
                                                email = email,
                                                password = password
                                            )
                                        )
                                        Toast.makeText(
                                            context,
                                            "Registration successful!",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        navController.navigate("login")
                                    }
                                }
                                loading = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF54C7FF)),
                        enabled = !loading
                    ) {
                        Text(
                            if (loading) "Registering..." else "SIGN UP",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 18.sp
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    TextButton(onClick = { navController.navigate("login") }) {
                        Text("Already have an account? ", color = Color.Gray, fontSize = 13.sp)
                        Text(
                            "Log In",
                            color = darkBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorText(error: String) {
    Text(
        text = error,
        color = Color.Red,
        fontSize = 12.sp,
        modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
    )
}