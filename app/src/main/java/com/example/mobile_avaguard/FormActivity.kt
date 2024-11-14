package com.example.mobile_avaguard

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mobile_avaguard.ui.theme.Mobile_avaguardTheme
import java.util.Calendar

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
    var dateIncident by remember { mutableStateOf("") }
    var dateIncidentError by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }

    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        // Handle the file Uri (e.g., display or upload it)
        selectedFileUri = uri
    }

    Column {
        Box(
            modifier = Modifier.fillMaxWidth()
                .height(125.dp)
                .background(Color(0xFFC3C7FD))
        ){
            Image(
                painter = painterResource(id = R.drawable.logo_avaguard),
                contentDescription = "",
                modifier = Modifier.align(Alignment.Center).graphicsLayer(scaleX = 2.5f, scaleY = 2.5f)// Centers the image in the Box
            )
        }
        Spacer(modifier = Modifier.height(50.dp))
        Text(text = "Data do incidente")
        Box(
            modifier = Modifier.fillMaxWidth()
                .padding(16.dp)
                .background(Color(0xFFC3C7FD))
        )
        val context = LocalContext.current

        val datePickerDialog = DatePickerDialog(
            context,
            { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                calendar.set(selectedYear, selectedMonth, selectedDay)

                TimePickerDialog(
                    context,
                    { _: TimePicker, selectedHour: Int, selectedMinute: Int ->
                        calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                        calendar.set(Calendar.MINUTE, selectedMinute)

                        dateIncident = String.format(
                            "%02d/%02d/%04d %02d:%02d",
                            selectedDay,
                            selectedMonth + 1,
                            selectedYear,
                            selectedHour,
                            selectedMinute
                        )
                        dateIncidentError = false
                    },
                    hour,
                    minute,
                    true
                ).show()
            },
            year,
            month,
            day
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable { datePickerDialog.show() }
        ){
            OutlinedTextField(
            value = dateIncident,
            onValueChange = {
            },
            label = { Text("Informe a data e hora") },
            isError = dateIncidentError,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { datePickerDialog.show() },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Date Icon",
                    tint = Color(red = 83, green = 96, blue = 245)
                )
            },
            readOnly = true
        )
        }
        Text(text = "Descrição do ocorrido")

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            singleLine = false,      // Allows multiple lines
            maxLines = 5             // Maximum number of lines
        )
        Text(text = "Envio de provas")
        Button(
            onClick = {
                // Open file picker with specific MIME type (e.g., "*/*" for all files)
                filePickerLauncher.launch("*/*")
            }
        ) {
            Text("Select a File")
        }

        // Display the selected file URI
        selectedFileUri?.let {
            Text(text = "Selected File: $it", modifier = Modifier.padding(top = 16.dp))
        }
    }
}