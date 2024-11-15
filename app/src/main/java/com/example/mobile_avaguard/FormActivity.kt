package com.example.mobile_avaguard

import android.Manifest
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.mobile_avaguard.ui.theme.Mobile_avaguardTheme
import java.util.Calendar

class FormActivity : ComponentActivity() {
    private lateinit var audioManager: AudioManager
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
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        val serviceIntent = Intent(this, MediaPlayerService::class.java)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent) // For Android O and above
        } else {
            startService(serviceIntent) // For Android Nougat and below
        }
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_HEADSETHOOK || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
            val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            Toast.makeText(this, "Volume: $currentVolume / $maxVolume", Toast.LENGTH_SHORT).show()
            createNotificationChannel(this)

            // Send notification
            sendNotification(this)
            return true
        }
        return super.onKeyDown(keyCode, event)
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
        Text(text = "Data do incidente", modifier = Modifier.padding(horizontal = 16.dp))
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
        Text(text = "Descrição do ocorrido", modifier = Modifier.padding(horizontal = 16.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            singleLine = false,      // Allows multiple lines
            maxLines = 5             // Maximum number of lines
        )
        Text(text = "Envio de provas", modifier = Modifier.padding(horizontal = 16.dp))
        Button(
            onClick = {
                // Open file picker with specific MIME type (e.g., "*/*" for all files)
                filePickerLauncher.launch("*/*")
            }, modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text("Escolha um arquivo.", modifier = Modifier.padding(horizontal = 16.dp))
        }

        // Display the selected file URI
        selectedFileUri?.let {
            Text(text = "Item selecionado: $it", modifier = Modifier.padding(horizontal = 16.dp))
        }

        Box(
            modifier = Modifier.fillMaxWidth().padding(vertical = 50.dp)
        ) {
            Button(
                onClick = {
                },
                modifier = Modifier
                    .align(Alignment.Center)
                    .width(200.dp)  // Set the width of the button
                    .height(50.dp)
                    .padding(top = 8.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(
                        red = 83,
                        green = 96,
                        blue = 245
                    )
                )
            ) {

                    Row {
                        Text("Enviar")
                        Spacer(modifier = Modifier.width(10.dp))
                    }


            }
        }
    }

}
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun sendNotification(context: Context) {
    val channelId = "your_channel_id"

    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_dialog_info)  // Replace with your app's icon
        .setContentTitle("Notification Title")
        .setContentText("This is the notification content.")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        // If permission is not granted, request it
        if (context is ComponentActivity) {
            ActivityCompat.requestPermissions(
                context,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_NOTIFICATION_PERMISSION
            )
        }
        return
    }

    // If permission is granted, send the notification
    with(NotificationManagerCompat.from(context)) {
        notify(1001, builder.build())
    }
}

fun createNotificationChannel(context: Context) {
    val channelId = "your_channel_id"
    val channelName = "Your Channel Name"
    val descriptionText = "Channel description"
    val importance = NotificationManager.IMPORTANCE_DEFAULT
    val channel = NotificationChannel(channelId, channelName, importance).apply {
        description = descriptionText
    }
    val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)
}
const val REQUEST_NOTIFICATION_PERMISSION = 100