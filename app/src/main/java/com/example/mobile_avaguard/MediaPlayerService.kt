package com.example.mobile_avaguard

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MediaPlayerService : MediaSessionService() {
    private var mediaSession: MediaSession? = null
    private var player: Player? = null

    companion object {
        private const val CHANNEL_ID = "media_playback_channel"
        private const val NOTIFICATION_ID = 1
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        // Initialize your player here
        // For example, using ExoPlayer:
        // player = ExoPlayer.Builder(this).build()

        mediaSession = player?.let { player ->
            val build = MediaSession.Builder(this, player)
                //.setSessionActivity(null) // Add your pending intent here if needed
                .build()
            build
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Media Playback",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Media playback controls"
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession

    override fun onStartCommand(intent: android.content.Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
        return super.onStartCommand(intent, flags, startId)
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentTitle("Media Player")
            .setContentText("Playing media")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onDestroy() {
        mediaSession?.release()
        mediaSession = null
        player?.release()
        player = null
        super.onDestroy()
    }


}

class MediaViewModel : ViewModel() {
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private var mediaSession: MediaSession? = null
    private var player: Player? = null

    fun initializeMediaController(context: Context) {
        viewModelScope.launch {
            val sessionToken = SessionToken(
                context,
                android.content.ComponentName(context, MediaPlayerService::class.java)
            )

            // Set up player controls and state monitoring
            player?.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    viewModelScope.launch {
                        _isPlaying.emit(isPlaying)
                    }
                }
            })
        }
    }

    fun togglePlayPause() {
        player?.let {
            if (it.isPlaying) {
                it.pause()
            } else {
                it.play()
            }
        }
    }

    override fun onCleared() {
        player = null
        mediaSession = null
    }
}

@Composable
fun MediaPlayerScreen(viewModel: MediaViewModel) {
    val context = LocalContext.current
    val isPlaying by viewModel.isPlaying.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.initializeMediaController(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { viewModel.togglePlayPause() }
        ) {
            Text(if (isPlaying) "Pause" else "Play")
        }
    }
}