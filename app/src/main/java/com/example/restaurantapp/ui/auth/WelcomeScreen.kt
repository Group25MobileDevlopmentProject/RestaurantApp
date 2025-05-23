package com.example.restaurantapp.ui.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.restaurantapp.R
import com.example.restaurantapp.ui.theme.*
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun WelcomeScreen(
    modifier: Modifier = Modifier,
    onAuthClick: () -> Unit,
    onHomeClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundGreen)
    ) {
        // Background Image with Gradient Overlay
        Image(
            painter = painterResource(id = R.drawable.irish_pub),
            contentDescription = "Background Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, DarkGreen.copy(alpha = 0.95f)),
                        startY = 400f
                    )
                )
        )

        // Main Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 40.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Restaurant Logo",
                    modifier = Modifier
                        .size(160.dp)
                        .padding(bottom = 16.dp)
                )

                Text(
                    text = "Bain Taithneamh As",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Enjoy a warm Irish welcome",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Buttons Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                WelcomeButton(text = "Login / Sign Up", color = IrishGreen, onClick = onAuthClick)
                val context = LocalContext.current

                WelcomeButton(
                    text = "Enter as Guest",
                    color = GoldenYellow,
                    onClick = {
                        val auth = Firebase.auth
                        auth.signInAnonymously()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    onHomeClick()  // Navigate to Home
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Failed to sign in as guest: ${task.exception?.localizedMessage}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }
                )

            }
        }
    }
}

// Styled Button Component
@Composable
fun WelcomeButton(text: String, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(12.dp, RoundedCornerShape(16.dp)),
        colors = ButtonDefaults.buttonColors(containerColor = color)
    ) {
        Text(text = text, fontSize = 22.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
    }
}
