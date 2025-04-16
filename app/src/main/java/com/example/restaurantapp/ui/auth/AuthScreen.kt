package com.example.restaurantapp.ui.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.restaurantapp.ui.model.UserRoles
import com.example.restaurantapp.ui.theme.DarkGreen
import com.example.restaurantapp.ui.theme.IrishGreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(navController: NavController) {
    var isSignUp by remember { mutableStateOf(false) }
    val name = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val context = LocalContext.current
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    var isError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.navigate("welcome") }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DarkGreen)
            }
        }

        Text(
            text = if (isSignUp) "Sign Up" else "Login",
            fontSize = 28.sp,
            color = DarkGreen,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (isSignUp) {
            OutlinedTextField(
                value = name.value,
                onValueChange = { name.value = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        OutlinedTextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            isError = isError
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (email.value.isEmpty() || password.value.isEmpty() || (isSignUp && name.value.isEmpty())) {
                    Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                    isError = true
                } else {
                    if (isSignUp) {
                        auth.createUserWithEmailAndPassword(email.value, password.value)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val user = auth.currentUser
                                    val userData = hashMapOf(
                                        "name" to name.value,
                                        "email" to email.value,
                                        "role" to UserRoles.CUSTOMER,
                                        "userStatus" to "active",
                                        "profileImageUrl" to "",
                                        "createdAt" to FieldValue.serverTimestamp(),
                                        "updatedAt" to FieldValue.serverTimestamp()
                                    )

                                    user?.let {
                                        db.collection("users")
                                            .document(it.uid)
                                            .set(userData)
                                            .addOnSuccessListener {
                                                user.sendEmailVerification()
                                                    .addOnCompleteListener { verificationTask ->
                                                        if (verificationTask.isSuccessful) {
                                                            Toast.makeText(
                                                                context,
                                                                "Sign up successful! Verification email sent.",
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                            navController.navigate("home")
                                                        } else {
                                                            Toast.makeText(
                                                                context,
                                                                "Sign up successful, but email not sent: ${verificationTask.exception?.message}",
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                            navController.navigate("home")
                                                        }
                                                    }
                                            }
                                            .addOnFailureListener { e ->
                                                Toast.makeText(
                                                    context,
                                                    "Error saving user data: ${e.message}",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Sign up failed: ${task.exception?.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                    } else {
                        auth.signInWithEmailAndPassword(email.value, password.value)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val user = auth.currentUser
                                    if (user != null && user.isEmailVerified) {
                                        Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                                        navController.navigate("home")
                                    } else {
                                        Toast.makeText(context, "Please verify your email first.", Toast.LENGTH_LONG).show()
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Login failed: ${task.exception?.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                    }
                }
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = IrishGreen)
        ) {
            Text(text = if (isSignUp) "Sign Up" else "Login", fontSize = 20.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = { isSignUp = !isSignUp }) {
            Text(text = if (isSignUp) "Already have an account? Login" else "Don't have an account? Sign Up")
        }
    }
}
