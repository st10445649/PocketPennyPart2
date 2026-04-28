package com.example.pocketpenny.ui

import android.R.attr.singleLine
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.pocketpenny.R
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

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xffbffcff), Color(0xff54c7ff), Color(0xff00a9fc))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient),

        contentAlignment = Alignment.BottomCenter
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.pennywave),
                contentDescription = null,
                modifier = Modifier
                    .size(300.dp)
                    .offset(y = 40.dp)
            )
            //card for login inputs
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(24.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xffddf4ff)),
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
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A5276)
                    )
                    Text(
                        "Please enter your details",
                        fontSize = 14.sp,
                        color = Color(0xFF1A5276)
                    )

                    Spacer(Modifier.height(24.dp))

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

                    Spacer(Modifier.height(24.dp))

                    LoginInputField(
                        value = password,
                        onValueChange = { password = it; passwordError = "" },
                        label = "Password" ,
                        icon = Icons.Default.Lock,
                        isPassword = true,
                        passwordVisible = passwordVisible,
                        onVisibilityToggle = { passwordVisible = !passwordVisible },
                        isError = passwordError.isNotEmpty()
                    )
                    if (passwordError.isNotEmpty()) {
                        Text(
                            passwordError, color = Color.Red, fontSize = 12.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = {
                            emailError = ""
                            passwordError = ""
                            var valid = true

                            if (email.isBlank()) {
                                emailError = "Email is required"; valid = false
                            }
                            if (password.isBlank()) {
                                passwordError = "Password is required"; valid = false
                            }

                            if (!valid) return@Button

                            scope.launch {
                                loading = true
                                val user = userDao.getUserByEmail(email)
                                if (user != null && user.password == password) {
                                    Toast.makeText(
                                        context,
                                        "Welcome back ${user.firstName}!",
                                        Toast.LENGTH_LONG
                                    ).show()
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
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF54C7FF)),
                        enabled = !loading
                    ) {
                        Text(
                            if (loading) "Logging in..." else "LOGIN",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 18.sp
                        )
                    }

                    Spacer(Modifier.weight(1f))

                    TextButton(onClick = { navController.navigate("register") }) {
                        Text("Don't have an account? ", color = Color(0xFF1A5276), fontSize = 15.sp)
                        Text(
                            "Sign Up",
                            color = Color(0xff00a9fc),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoginInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onVisibilityToggle: () -> Unit = {},
    isError: Boolean = false
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(label, color = Color(0xFF1A5276),
            fontSize = 16.sp) },
        textStyle = LocalTextStyle.current.copy(fontSize = 16.sp, color = Color(0xFF1A5276)),
        leadingIcon = { Icon(icon, contentDescription = null, tint = Color(0xFF1A5276), modifier = Modifier.size(28.dp)) },
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = onVisibilityToggle) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = Color(0xFF1A5276)
                    )
                }
            }
        } else null,
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent,
            focusedIndicatorColor = Color(0xFF1A5276),
            unfocusedIndicatorColor = Color(0xFF1A5276),
            cursorColor = Color(0xFF1A5276)
        ),
        shape = RoundedCornerShape(0.dp)
    )
}