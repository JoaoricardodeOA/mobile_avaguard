package com.example.mobile_avaguard

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.mobile_avaguard.ui.theme.Mobile_avaguardTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp


class MainActivity : ComponentActivity() {
    private lateinit var audioManager: AudioManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Mobile_avaguardTheme {
                Scaffold {
                    LoginForm(
                        onLogin = { email, password ->
                            Toast.makeText(this, "Email logado $email", Toast.LENGTH_SHORT).show()
                        },
                        isLoading = false,
                        errorMessage = null,
                        modifier = Modifier.padding(16.dp) // Use padding on LoginForm directly
                    )
                }
            }
        }

        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_HEADSETHOOK || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
            val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            Toast.makeText(this, "Volume: $currentVolume / $maxVolume", Toast.LENGTH_SHORT).show()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}


@Composable
fun LoginForm(
    onLogin: (String, String) -> Unit,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Spacer(modifier = Modifier.height(125.dp))
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_avaguard),
                contentDescription = "",
                modifier = Modifier.align(Alignment.Center).graphicsLayer(scaleX = 2.5f, scaleY = 2.5f)// Centers the image in the Box
            )
        }
        Spacer(modifier = Modifier.height(125.dp))


        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = false
            },
            label = { Text("Digite o seu email") },
            isError = emailError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person, // You can use any icon you like
                    contentDescription = "Email Icon",
                    tint = Color.Blue
                )
            }
        )

        if (emailError) {
            Text(
                text = "Invalid email address",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = false
            },
            label = { Text("Digite a sua senha") },
            isError = passwordError,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                Icon(
                    painter = painterResource(R.drawable.baseline_remove_red_eye_24), // You can use any icon you like
                    contentDescription = "Email Icon",
                    tint = Color.Blue
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock, // You can use any icon you like
                    contentDescription = "Lock Icon",
                    tint = Color.Blue
                )
            }
        )

        if (passwordError) {
            Text(
                text = "Password must not be empty",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        Spacer(modifier = Modifier.height(125.dp))
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
                    val isPasswordValid = password.isNotEmpty()

                    if (isEmailValid && isPasswordValid) {
                        onLogin(email, password)
                    } else {
                        emailError = !isEmailValid
                        passwordError = !isPasswordValid
                    }
                },
                modifier = Modifier
                    .align(Alignment.Center)
                    .width(200.dp)  // Set the width of the button
                    .height(50.dp)
                    .padding(top = 8.dp),
                enabled = !isLoading,
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(
                        red = 83,
                        green = 96,
                        blue = 245
                    )
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Row {
                        Text("Entrar")
                        Spacer(modifier = Modifier.width(10.dp))
                        Icon(painter = painterResource(id = R.drawable.baseline_arrow_right_alt_24),
                            contentDescription = "",  tint = Color.White)
                    }

                }
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewLoginForm() {
    LoginForm(onLogin = { email, password -> /* Handle login */ })
}