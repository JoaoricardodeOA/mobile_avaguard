package com.example.mobile_avaguard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.mobile_avaguard.ui.theme.Mobile_avaguardTheme

class FormActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Mobile_avaguardTheme {
                Scaffold {
                    test()
                }
            }
        }
    }
}
@Composable
fun test(){
    Column {
        Text(text = "Click Me")
    }
}